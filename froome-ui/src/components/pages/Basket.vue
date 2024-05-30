<template>
  <div id="basket-container" class="container mt-custom">
    <h2 class="text-center mb-4">Your Basket</h2>
    <div v-if="basketItems.length > 0">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div class="d-flex align-items-center">
          <label for="pageSize" class="form-label me-2 mb-0 d-inline">Items per page:</label>
          <select id="pageSize" v-model="size" @change="changeSize(size)" class="form-select d-inline w-auto">
            <option v-for="s in sizes" :key="s" :value="s">{{ s }}</option>
          </select>
        </div>
        <div>
          <button class="btn btn-primary me-2" :disabled="page === 0" @click="changePage(page - 1)">Previous</button>
          <button class="btn btn-primary" :disabled="page >= totalPages - 1" @click="changePage(page + 1)">Next</button>
          <button class="btn btn-secondary ms-2" @click="toggleView">
            {{ isTableView ? 'Switch to Card View' : 'Switch to Table View' }}
          </button>
          <button class="btn btn-success ms-2" @click="placeOrder">Place Order</button>
        </div>
      </div>

      <div v-if="isTableView">
        <table class="table table-bordered table-striped">
          <thead>
          <tr>
            <th class="text-center">Name</th>
            <th>Description</th>
            <th class="text-center">Price</th>
            <th class="text-center">Quantity</th>
            <th class="text-center">Action</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="product in basketItems" :key="product.id">
            <td class="text-center">{{ product.name }}</td>
            <td>{{ product.description }}</td>
            <td class="text-center">{{ product.price }} €</td>
            <td class="text-center">{{ quantities[product.id] }}</td>
            <td class="text-center">
              <button class="btn btn-danger" @click="removeFromBasket(product.id)">
                <i class="fas fa-trash-alt"></i>
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <div v-else>
        <div class="row">
          <div class="col-md-6" v-for="product in basketItems" :key="product.id">
            <ProductCardComponent
                :product="product"
                :quantities="quantities"
                :increaseQuantity="increaseQuantity"
                :decreaseQuantity="decreaseQuantity"
                :updateQuantity="updateQuantity"
                @remove="removeFromBasket"
            />
          </div>
        </div>
      </div>

      <ErrorNotification id="notification" v-if="error" :message="errorMessage" />
    </div>
    <div v-else>
      <p class="text-center">Your basket is empty.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import ErrorNotification from '../notifications/ErrorNotification.vue';
import ProductCardComponent from '../ProductCardComponent.vue';

const baseUrl = import.meta.env.VITE_ORDER_API_URL;
const productApiUrl = import.meta.env.VITE_PRODUCT_API_URL;
const router = useRouter();

const products = ref([]);
const quantities = ref({});
const error = ref(false);
const errorMessage = ref('');
const page = ref(0);
const size = ref(100);
const totalPages = ref(1);
const sizes = [10, 20, 50, 100];
const isTableView = ref(true);

const fetchProducts = () => {
  axios.get(`${productApiUrl}/api/products?page=${page.value}&size=${size.value}`)
      .then(response => {
        products.value = response.data.content.filter(product => product.stock > 0);
        const cart = JSON.parse(localStorage.getItem('cart')) || {};

        products.value.forEach(product => {
          if (cart[product.id]) {
            quantities.value[product.id] = cart[product.id];
          }
        });
      })
      .catch(err => {
        console.error('Failed to fetch products:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while fetching products.';
      });
};

const basketItems = computed(() => {
  const cart = JSON.parse(localStorage.getItem('cart')) || {};
  return products.value.filter(product => cart[product.id]);
});

const updateCart = (product) => {
  const cart = JSON.parse(localStorage.getItem('cart')) || {};
  cart[product.id] = quantities.value[product.id];
  localStorage.setItem('cart', JSON.stringify(cart));
};

const removeFromBasket = (productId) => {
  const cart = JSON.parse(localStorage.getItem('cart')) || {};
  delete cart[productId];
  localStorage.setItem('cart', JSON.stringify(cart));
  quantities.value[productId] = 0;
  products.value = products.value.filter(product => product.id !== productId); // Remove product from the list
};

const increaseQuantity = (product) => {
  if (quantities.value[product.id] < product.stock) {
    quantities.value[product.id]++;
    updateCart(product);
  } else {
    error.value = true;
    errorMessage.value = 'Quantity exceeds stock availability.';
  }
};

const decreaseQuantity = (product) => {
  if (quantities.value[product.id] > 0) {
    quantities.value[product.id]--;
    updateCart(product);
  }
};

const updateQuantity = (product) => {
  if (quantities.value[product.id] > product.stock) {
    quantities.value[product.id] = product.stock;
    error.value = true;
    errorMessage.value = 'Quantity exceeds stock availability.';
  }
  updateCart(product);
};

const placeOrder = () => {
  const bearerToken = localStorage.getItem('bearerToken');
  if (!bearerToken) {
    error.value = true;
    errorMessage.value = 'You need to be logged in to place an order.';
    return;
  }

  axios.post(`${baseUrl}/api/orders`, {}, {
    headers: {
      'Authorization': `Bearer ${bearerToken}`
    }
  })
      .then(response => {
        const orderId = response.data.id;
        const cart = JSON.parse(localStorage.getItem('cart')) || {};

        const orderPromises = Object.keys(cart).filter(productId => cart[productId] > 0).map(productId => {
          const quantity = cart[productId];
          return axios.post(`${baseUrl}/api/orders/${orderId}/items`, {
            productId: parseInt(productId),
            quantity: quantity
          }, {
            headers: {
              'Authorization': `Bearer ${bearerToken}`
            }
          })
              .then(() => {
                // Update stock
                const product = products.value.find(p => p.id === parseInt(productId));
                if (product) {
                  product.stock -= quantity;
                }
                delete cart[productId]; // Remove item from cart after successful addition
                localStorage.setItem('cart', JSON.stringify(cart));
              });
        });

        return Promise.all(orderPromises)
            .then(() => orderId)
            .catch(err => {
              // If any item addition fails, delete the order
              return axios.delete(`${baseUrl}/api/orders/${orderId}`, {
                headers: {
                  'Authorization': `Bearer ${bearerToken}`
                }
              }).then(() => {
                throw err;
              });
            });
      })
      .then((orderId) => {
        // Clear the cart and redirect to orders page
        localStorage.removeItem('cart');
        router.push('/payment/' + orderId);
      })
      .catch(err => {
        console.error('Failed to place order:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while placing the order.';
      });
};

const changePage = (newPage) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    page.value = newPage;
    fetchProducts();
  }
};

const changeSize = (newSize) => {
  size.value = newSize;
  page.value = 0;
  fetchProducts();
};

const toggleView = () => {
  isTableView.value = !isTableView.value;
};

onMounted(fetchProducts);
</script>

<style scoped>
#basket-container {
  max-width: 800px;
  margin-top: 100px;
}

.mt-custom {
  margin-top: 100px;
}

#notification {
  margin-top: 50px;
}
</style>

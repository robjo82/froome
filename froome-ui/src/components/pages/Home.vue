<template>
  <div id="home-container" class="container mt-custom">
    <h2 class="text-center mb-4">Products</h2>
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
      </div>
    </div>

    <div v-if="isTableView">
      <table class="table table-bordered table-striped">
        <thead>
        <tr>
          <th class="text-center min-w-150">Name</th>
          <th class="min-w-250">Description</th>
          <th class="text-center min-w-100">Price</th>
          <th class="text-center min-w-170">Action</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="product in availableProducts" :key="product.id">
          <td class="text-center">{{ product.name }}</td>
          <td>{{ product.description }}</td>
          <td class="text-center">{{ product.price }} €</td>
          <td>
            <div class="input-group">
              <button class="btn btn-secondary" @click="decreaseQuantity(product)">-</button>
              <input type="number" class="form-control text-center" v-model.number="quantities[product.id]" @input="updateQuantity(product)" min="0" :max="product.stock || Infinity" />
              <button class="btn btn-secondary" @click="increaseQuantity(product)">+</button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-else>
      <div class="row">
        <div class="col-md-6" v-for="product in availableProducts" :key="product.id">
          <ProductCardComponent
              :product="product"
              :quantities="quantities"
              :increaseQuantity="increaseQuantity"
              :decreaseQuantity="decreaseQuantity"
              :updateQuantity="updateQuantity"
          />
        </div>
      </div>
    </div>

    <ErrorNotification id="notification" v-if="error" :message="errorMessage" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import axios from 'axios';
import ErrorNotification from '../notifications/ErrorNotification.vue';
import ProductCardComponent from '../ProductCardComponent.vue';

const baseUrl = import.meta.env.VITE_PRODUCT_API_URL;

const products = ref([]);
const quantities = ref({});
const error = ref(false);
const errorMessage = ref('');
const page = ref(0);
const size = ref(10);
const totalPages = ref(1);
const sizes = [10, 20, 50, 100];
const isTableView = ref(true);

let timeoutId = null;

const fetchProducts = () => {
  axios.get(`${baseUrl}/api/products?page=${page.value}&size=${size.value}`)
      .then(response => {
        products.value = response.data.content.filter(product => product.stock > 0);
        totalPages.value = response.data.totalPages;
        const cart = JSON.parse(localStorage.getItem('cart')) || {};

        products.value.forEach(product => {
          if (cart[product.id]) {
            if (cart[product.id] > product.stock) {
              // Si le produit n'est pas en stock, supprimez-le du panier
              delete cart[product.id];
            } else {
              // Si le produit est en stock, définissez la quantité à la dernière valeur définie
              quantities.value[product.id] = cart[product.id];
            }
          } else {
            if (!(product.id in quantities.value)) {
              quantities.value[product.id] = 0;
            }
          }
        });

        // Mettez à jour le panier dans localStorage après avoir supprimé les éléments non disponibles
        localStorage.setItem('cart', JSON.stringify(cart));
      })
      .catch(err => {
        console.error('Failed to fetch products:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while fetching products.';
      });
};

// Rafraîchir les stocks toutes les 30 secondes
const refreshStocks = () => {
  setInterval(() => {
    fetchProducts();
  }, 30000); // 30 seconds
};

const availableProducts = computed(() => {
  return products.value.filter(product => product.stock > 0);
});

const updateCart = (product) => {
  const cart = JSON.parse(localStorage.getItem('cart')) || {};
  cart[product.id] = quantities.value[product.id];
  localStorage.setItem('cart', JSON.stringify(cart));
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

// Watch for changes in quantities to ensure they do not exceed the stock
watch(quantities, (newQuantities) => {
  if (timeoutId) {
    clearTimeout(timeoutId);
  }

  timeoutId = setTimeout(() => {
    products.value.forEach(product => {
      if (newQuantities[product.id] > product.stock) {
        newQuantities[product.id] = product.stock;
        error.value = true;
        errorMessage.value = 'Quantity exceeds stock availability.';
        updateCart(product); // Ensure cart is updated with correct value
      } else {
        updateCart(product); // Update cart without changing quantity
      }
    });
    timeoutId = null; // Reset the timeout ID
  }, 1000); // Delay of 1 second
}, {deep: true});

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

onMounted(() => {
  fetchProducts();
  refreshStocks(); // Start refreshing stocks every 30 seconds
});
</script>

<style scoped>
#home-container {
  max-width: 800px;
  margin-top: 100px;
}

.mt-custom {
  margin-top: 100px;
}

#notification {
  margin-top: 50px;
}

.min-w-250 {
  min-width: 250px;
}

.min-w-170 {
  min-width: 170px;
}

.min-w-150 {
  min-width: 150px;
}

.min-w-100 {
  min-width: 100px;
}
</style>

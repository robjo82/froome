<template>
  <div id="admin-container" class="container mt-custom">
    <h2 class="text-center mb-4">Admin Interface</h2>
    <!-- Users Section -->
    <h4 class="mb-3">Users</h4>
    <table class="table table-bordered table-striped text-center">
      <thead>
      <tr>
        <th>Username</th>
        <th>Email</th>
        <th>Address</th>
        <th>Is Admin</th>
        <th>Nb Orders</th>
        <th>Total Amount</th>
        <th class="actions-column">Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in users" :key="user.id">
        <td v-if="!user.isEditing" class="align-middle">{{ user.username }}</td>
        <td v-else class="align-middle"><input type="text" v-model="user.editUsername" class="form-control"></td>

        <td v-if="!user.isEditing" class="align-middle">{{ user.email }}</td>
        <td v-else class="align-middle"><input type="email" v-model="user.editEmail" class="form-control"></td>

        <td v-if="!user.isEditing" class="align-middle">{{ user.address }}</td>
        <td v-else class="align-middle"><input type="text" v-model="user.editAddress" class="form-control"></td>

        <td class="align-middle">
          <input type="checkbox" v-model="user.editAdmin" @change="updateAdmin(user)">
        </td>

        <td class="align-middle">{{ user.nbOrders }}</td>
        <td class="align-middle">{{ user.totalAmount }} €</td>
        <td class="align-middle">
          <i v-if="!user.isEditing" class="fas fa-pencil-alt" @click="startEditing(user)"></i>
          <i v-else class="fas fa-check" @click="saveUser(user)"></i>
          <i class="fas fa-trash-alt" @click="deleteUser(user.id)"></i>
        </td>
      </tr>
      </tbody>
    </table>
    <div class="stats mt-4">
      <p><strong>Number of Users:</strong> {{ users.length }}</p>
      <p><strong>Average Orders per User:</strong> {{ averageOrdersPerUser.toFixed(2) }}</p>
      <p><strong>Average Total Amount per User:</strong> {{ averageTotalAmountPerUser.toFixed(2) }} €</p>
    </div>

    <!-- Orders Section -->
    <h4 class="mt-5 mb-3">Orders</h4>
    <table class="table table-bordered table-striped text-center">
      <thead>
      <tr>
        <th>User ID</th>
        <th>Date</th>
        <th>Nb Items</th>
        <th>Amount</th>
        <th>Status</th>
        <th class="actions-column">Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="order in orders" :key="order.id">
        <td class="align-middle">{{ order.userId }}</td>
        <td class="align-middle">{{ formatDate(order.orderDate) }}</td>
        <td class="align-middle">{{ order.nbItems }}</td>
        <td class="align-middle">{{ order.amount }} €</td>
        <td class="align-middle">
          <select v-model="order.status" @change="updateOrderStatus(order)" class="form-select">
            <option value="CREATED">CREATED</option>
            <option value="PAID">PAID</option>
            <option value="SHIPPED">SHIPPED</option>
            <option value="DELIVERED">DELIVERED</option>
          </select>
        </td>
        <td class="align-middle">
          <i class="fas fa-trash-alt" @click="deleteOrder(order.id)"></i>
        </td>
      </tr>
      </tbody>
    </table>
    <div class="stats mt-4">
      <p><strong>Total Orders:</strong> {{ orders.length }}</p>
      <p><strong>Total Amount:</strong> {{ totalOrderAmount.toFixed(2) }} €</p>
      <p><strong>Average Order Amount:</strong> {{ averageOrderAmount.toFixed(2) }} €</p>
    </div>

    <!-- Products Section -->
    <h4 class="mt-5 mb-3">Products</h4>
    <table class="table table-bordered table-striped text-center">
      <thead>
      <tr>
        <th class="name-column">Name</th>
        <th>Description</th>
        <th>Price</th>
        <th>Stock</th>
        <th class="actions-column">Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="product in products" :key="product.id">
        <td v-if="!product.isEditing" class="align-middle">{{ product.name }}</td>
        <td v-else class="align-middle"><input type="text" v-model="product.editName" class="form-control"></td>

        <td v-if="!product.isEditing" class="align-middle">{{ product.description }}</td>
        <td v-else class="align-middle"><input type="text" v-model="product.editDescription" class="form-control"></td>

        <td v-if="!product.isEditing" class="align-middle">{{ product.price }} €</td>
        <td v-else class="align-middle"><input type="number" v-model.number="product.editPrice" class="form-control"></td>

        <td v-if="!product.isEditing" class="align-middle">{{ product.stock }}</td>
        <td v-else class="align-middle"><input type="number" v-model.number="product.editStock" class="form-control"></td>

        <td class="align-middle">
          <i v-if="!product.isEditing" class="fas fa-pencil-alt" @click="startEditingProduct(product)"></i>
          <i v-else class="fas fa-check" @click="saveProduct(product)"></i>
          <i class="fas fa-trash-alt" @click="deleteProduct(product.id)"></i>
        </td>
      </tr>
      <tr>
        <td><input type="text" v-model="newProduct.name" class="form-control" placeholder="New product name"></td>
        <td><input type="text" v-model="newProduct.description" class="form-control" placeholder="New product description"></td>
        <td><input type="number" v-model.number="newProduct.price" class="form-control" placeholder="Price"></td>
        <td><input type="number" v-model.number="newProduct.stock" class="form-control" placeholder="Stock"></td>
        <td><button class="btn btn-success" @click="createProduct">Add Product</button></td>
      </tr>
      </tbody>
    </table>
    <div class="stats mt-4">
      <p><strong>Number of Products:</strong> {{ products.length }}</p>
      <p><strong>Total Stock:</strong> {{ totalStock }}</p>
      <p><strong>Total Stock Value:</strong> {{ totalStockValue.toFixed(2) }} €</p>
    </div>
    <SuccessNotification class="notification" v-if="success" :message="successMessage" />
    <ErrorNotification class="notification" v-if="error" :message="errorMessage" />
  </div>
</template>


<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import ErrorNotification from '../notifications/ErrorNotification.vue';
import SuccessNotification from '../notifications/SuccessNotification.vue';

const userUrl = import.meta.env.VITE_USER_API_URL;
const ordersUrl = import.meta.env.VITE_ORDER_API_URL;
const productsUrl = import.meta.env.VITE_PRODUCT_API_URL;

const error = ref(false);
const errorMessage = ref('');
const success = ref(false);
const successMessage = ref('');
const users = ref([]);
const orders = ref([]);
const products = ref([]);
const newProduct = ref({ name: '', description: '', price: 0, stock: 0 });

const fetchUsers = () => {
  axios.get(`${userUrl}/api/users`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        users.value = response.data.map(user => ({
          ...user,
          editUsername: user.username,
          editEmail: user.email,
          editAddress: user.address,
          editAdmin: user.admin,
          isEditing: false,
          nbOrders: 0,
          totalAmount: 0
        }));
        fetchOrders();
      })
      .catch(err => {
        console.error('Failed to fetch users:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while fetching users.';
      });
};

const fetchOrders = () => {
  axios.get(`${ordersUrl}/api/orders`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        const ordersData = response.data;
        const userOrderData = {};

        ordersData.forEach(order => {
          const nbItems = order.orderItems.reduce((sum, item) => sum + item.quantity, 0);
          const amount = order.orderItems.reduce((sum, item) => sum + item.price, 0);

          if (!userOrderData[order.userId]) {
            userOrderData[order.userId] = {
              nbOrders: 0,
              totalAmount: 0
            };
          }

          userOrderData[order.userId].nbOrders++;
          userOrderData[order.userId].totalAmount += amount;

          orders.value.push({
            ...order,
            nbItems: nbItems,
            amount: amount
          });
        });

        users.value.forEach(user => {
          if (userOrderData[user.id]) {
            user.nbOrders = userOrderData[user.id].nbOrders;
            user.totalAmount = userOrderData[user.id].totalAmount;
          }
        });
      })
      .catch(err => {
        console.error('Failed to fetch orders:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while fetching orders.';
      });
};

const fetchProducts = () => {
  axios.get(`${productsUrl}/api/products?size=100000`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        products.value = response.data.content.map(product => ({
          ...product,
          editName: product.name,
          editDescription: product.description,
          editPrice: product.price,
          editStock: product.stock,
          isEditing: false
        }));
      })
      .catch(err => {
        console.error('Failed to fetch products:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while fetching products.';
      });
};

const startEditing = (user) => {
  user.isEditing = true;
};

const saveUser = (user) => {
  const updatedUser = {
    ...user,
    username: user.editUsername,
    email: user.editEmail,
    address: user.editAddress,
    admin: user.editAdmin
  };

  axios.put(`${userUrl}/api/users/${user.id}`, updatedUser, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        Object.assign(user, response.data);
        user.editUsername = user.username;
        user.editEmail = user.email;
        user.editAddress = user.address;
        user.editAdmin = user.admin;
        user.isEditing = false;
        success.value = true;
        successMessage.value = 'User updated successfully!';
        setTimeout(() => {
          success.value = false;
        }, 5000);
      })
      .catch(err => {
        console.error('Failed to save user:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while saving user.';
      });
};

const updateAdmin = (user) => {
  const updatedUser = {
    ...user,
    admin: user.editAdmin
  };

  axios.put(`${userUrl}/api/users/${user.id}`, updatedUser, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        user.admin = response.data.admin;
        success.value = true;
        successMessage.value = 'Admin status updated successfully!';
        setTimeout(() => {
          success.value = false;
        }, 5000);
      })
      .catch(err => {
        console.error('Failed to update admin status:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while updating admin status.';
      });
};

const deleteUser = (userId) => {
  axios.delete(`${userUrl}/api/users/${userId}`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(() => {
        users.value = users.value.filter(user => user.id !== userId);
        success.value = true;
        successMessage.value = 'User deleted successfully!';
        setTimeout(() => {
          success.value = false;
        }, 5000);
      })
      .catch(err => {
        console.error('Failed to delete user:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while deleting user.';
      });
};

const updateOrderStatus = (order) => {
  axios.put(`${ordersUrl}/api/orders/${order.id}?status=${order.status}`, null, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        Object.assign(order, response.data);
        success.value = true;
        successMessage.value = 'Order status updated successfully!';
        setTimeout(() => {
          success.value = false;
        }, 5000);
      })
      .catch(err => {
        console.error('Failed to update order status:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while updating order status.';
      });
};

const deleteOrder = (orderId) => {
  axios.delete(`${ordersUrl}/api/orders/${orderId}`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(() => {
        orders.value = orders.value.filter(order => order.id !== orderId);
        success.value = true;
        successMessage.value = 'Order deleted successfully!';
        setTimeout(() => {
          success.value = false;
        }, 5000);
      })
      .catch(err => {
        console.error('Failed to delete order:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while deleting order.';
      });
};

const startEditingProduct = (product) => {
  product.isEditing = true;
};

const saveProduct = (product) => {
  const updatedProduct = {
    ...product,
    name: product.editName,
    description: product.editDescription,
    price: product.editPrice,
    stock: product.editStock
  };

  axios.put(`${productsUrl}/api/products/${product.id}`, updatedProduct, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        Object.assign(product, response.data);
        product.editName = product.name;
        product.editDescription = product.description;
        product.editPrice = product.price;
        product.editStock = product.stock;
        product.isEditing = false;
        success.value = true;
        successMessage.value = 'Product updated successfully!';
        setTimeout(() => {
          success.value = false;
        }, 5000);
      })
      .catch(err => {
        console.error('Failed to save product:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while saving product.';
      });
};

const deleteProduct = (productId) => {
  axios.delete(`${productsUrl}/api/products/${productId}`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(() => {
        products.value = products.value.filter(product => product.id !== productId);
        success.value = true;
        successMessage.value = 'Product deleted successfully!';
        setTimeout(() => {
          success.value = false;
        }, 5000);
      })
      .catch(err => {
        console.error('Failed to delete product:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while deleting product.';
      });
};

const createProduct = () => {
  if (!newProduct.value.name || !newProduct.value.description || newProduct.value.price <= 0 || newProduct.value.stock < 0) {
    error.value = true;
    errorMessage.value = 'Please fill out all fields correctly.';
    return;
  }

  axios.post(`${productsUrl}/api/products/create`, newProduct.value, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        products.value.push({
          ...response.data,
          editName: response.data.name,
          editDescription: response.data.description,
          editPrice: response.data.price,
          editStock: response.data.stock,
          isEditing: false
        });
        newProduct.value = { name: '', description: '', price: 0, stock: 0 };
        success.value = true;
        successMessage.value = 'Product created successfully!';
        setTimeout(() => {
          success.value = false;
        }, 5000);
      })
      .catch(err => {
        console.error('Failed to create product:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while creating product.';
      });
};

// Computed properties for statistics
const averageOrdersPerUser = computed(() => {
  if (users.value.length === 0) return 0;
  const totalOrders = users.value.reduce((sum, user) => sum + user.nbOrders, 0);
  return totalOrders / users.value.length;
});

const averageTotalAmountPerUser = computed(() => {
  if (users.value.length === 0) return 0;
  const totalAmount = users.value.reduce((sum, user) => sum + user.totalAmount, 0);
  return totalAmount / users.value.length;
});

const totalOrderAmount = computed(() => {
  return orders.value.reduce((sum, order) => sum + order.amount, 0);
});

const averageOrderAmount = computed(() => {
  if (orders.value.length === 0) return 0;
  return totalOrderAmount.value / orders.value.length;
});

const totalStock = computed(() => {
  return products.value.reduce((sum, product) => sum + product.stock, 0);
});

const totalStockValue = computed(() => {
  return products.value.reduce((sum, product) => sum + (product.price * product.stock), 0);
});

const formatDate = (dateArray) => {
  if (!Array.isArray(dateArray) || dateArray.length < 6) {
    return 'Invalid Date';
  }
  const [year, month, day, hour, minute, second] = dateArray;
  return `${day}/${month}/${year} ${hour}:${minute}:${second}`;
};

onMounted(() => {
  fetchUsers();
  fetchProducts();
});
</script>



<style scoped>
#admin-container {
  max-width: 1000px;
  margin-top: 100px;
}

.mt-custom {
  margin-top: 100px;
}

.table .align-middle {
  vertical-align: middle !important;
}

.table .actions-column {
  width: 150px;
}

.table .name-column {
  width: 200px;
}

.table .fas {
  cursor: pointer;
  margin-left: 10px;
}

.notification {
  top: 70px;
}

.stats {
  margin-top: 20px;
}
</style>

<template>
  <div id="account-container" class="container mt-custom">
    <h2 class="text-center mb-4">My Account</h2>

    <!-- User Information -->
    <div class="user-info mb-4">
      <h4>User Information</h4>
      <div v-if="!isEditing">
        <p><strong>Username:</strong> {{ user.username }} <i class="fas fa-pencil-alt" @click="startEditing"></i></p>
        <p><strong>Email:</strong> {{ user.email }} <i class="fas fa-pencil-alt" @click="startEditing"></i></p>
        <p><strong>Address:</strong> {{ user.address }} <i class="fas fa-pencil-alt" @click="startEditing"></i></p>
      </div>
      <div v-else>
        <div class="mb-3">
          <label for="username" class="form-label">Username</label>
          <input type="text" id="username" v-model="editUser.username" class="form-control">
        </div>
        <div class="mb-3">
          <label for="email" class="form-label">Email</label>
          <input type="email" id="email" v-model="editUser.email" class="form-control">
        </div>
        <div class="mb-3">
          <label for="address" class="form-label">Address</label>
          <input type="text" id="address" v-model="editUser.address" class="form-control">
        </div>
        <button class="btn btn-primary" @click="saveUser">Save</button>
        <button class="btn btn-secondary" @click="cancelEditing">Cancel</button>
      </div>
    </div>

    <!-- Orders Summary -->
    <div class="orders-summary mb-4">
      <h4>Orders Summary</h4>
      <div v-for="(total, month) in monthlyTotals" :key="month">
        <p><strong>{{ month }}:</strong> {{ total }} €</p>
      </div>
    </div>

    <!-- Delete Account -->
    <button class="btn btn-danger" @click="deleteAccount">Delete My Account</button>
    <ErrorNotification class="notification" v-if="error" :message="errorMessage" />
    <SuccessNotification class="notification" v-if="success" :message="successMessage" :duration="1000" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
import ErrorNotification from '../notifications/ErrorNotification.vue';
import SuccessNotification from "../notifications/SuccessNotification.vue";

const userUrl = import.meta.env.VITE_USER_API_URL;
const ordersUrl = import.meta.env.VITE_ORDER_API_URL;
const route = useRoute();
const router = useRouter();
const error = ref(false);
const success = ref(false);
const errorMessage = ref('');
const successMessage = ref('');
const user = ref({});
const editUser = ref({});
const isEditing = ref(false);
const monthlyTotals = ref({});

const fetchUserDetails = () => {
  axios.get(`${userUrl}/api/users/me`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        user.value = response.data;
        editUser.value = { ...response.data };
      })
      .catch(err => {
        console.error('Failed to fetch user details:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while fetching user details.';
      });
};

const fetchUserOrders = () => {
  axios.get(`${ordersUrl}/api/orders/mine`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        const orders = response.data;
        const totals = {};
        orders.forEach(order => {
          const month = new Date(formatDate(order.orderDate)).toLocaleString('default', { month: 'long', year: 'numeric' });
          if (!totals[month]) {
            totals[month] = 0;
          }
          order.orderItems.forEach(item => {
            totals[month] += item.price;
          });
        });
        monthlyTotals.value = totals;
      })
      .catch(err => {
        console.error('Failed to fetch user orders:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while fetching orders.';
      });
};

const startEditing = () => {
  isEditing.value = true;
};

const cancelEditing = () => {
  isEditing.value = false;
  editUser.value = { ...user.value }; // Revert changes
};

const saveUser = () => {
  axios.put(`${userUrl}/api/users/${user.value.id}`, editUser.value, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        user.value = response.data;
        isEditing.value = false;
        success.value = true;
        successMessage.value = 'User details saved successfully.';
        setTimeout(() => {
          success.value = false;
        }, 1000);
      })
      .catch(err => {
        console.error('Failed to save user details:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while saving user details.';
      });
};

const deleteAccount = () => {
  axios.delete(`${userUrl}/api/users/${user.value.id}`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(() => {
        localStorage.removeItem('bearerToken');
        success.value = true;
        successMessage.value = 'Account deleted successfully.';
        setTimeout(() => {
          router.push('/');
        }, 1000);
      })
      .catch(err => {
        console.error('Failed to delete account:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while deleting the account.';
      });
};

const formatDate = (dateArray) =>
    {
      if (!Array.isArray(dateArray) || dateArray.length < 6) {
        return 'Invalid Date';
      }
      const [year, month, day] = dateArray;
      return `${year}-${month}-${day}`;
    }
;

onMounted(() => {
  fetchUserDetails();
  fetchUserOrders();
});
</script>

<style scoped>
#account-container {
  max-width: 800px;
  margin-top: 100px;
}

.mt-custom {
  margin-top: 100px;
}

.user-info .fas {
  cursor: pointer;
  margin-left: 10px;
}

.orders-summary {
  margin-top: 30px;
}

.notification {
  top: 70px;
}
</style>

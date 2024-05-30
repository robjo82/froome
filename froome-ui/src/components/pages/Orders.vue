<template>
  <div id="orders-container" class="container mt-custom">
    <h2 class="text-center mb-4">Your Orders</h2>
    <table class="table table-bordered table-striped text-center">
      <thead>
      <tr>
        <th>Date</th>
        <th>Status</th>
        <th>Total Price</th>
        <th>Action</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="order in orders" :key="order.id">
        <td>{{ formatDate(order.orderDate) }}</td>
        <td>{{ order.status }}</td>
        <td>{{ calculateTotalPrice(order.orderItems) }} €</td>
        <td>
          <button v-if="order.status === 'CREATED'" class="btn btn-primary" @click="goToPayment(order.id)">Go to Payment</button>
        </td>
      </tr>
      </tbody>
    </table>
    <ErrorNotification id="notification" v-if="error" :message="errorMessage" />
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import ErrorNotification from '../notifications/ErrorNotification.vue';

const ordersUrl = import.meta.env.VITE_ORDER_API_URL;
const router = useRouter();
const error = ref(false);
const errorMessage = ref('');
const orders = ref([]);

const fetchOrders = () => {
  axios.get(`${ordersUrl}/api/orders/mine`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('bearerToken')}` }
  })
      .then(response => {
        orders.value = response.data;
      })
      .catch(err => {
        console.error('Failed to fetch orders:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while fetching orders.';
      });
};

const formatDate = (dateArray) => {
  if (!Array.isArray(dateArray) || dateArray.length < 6) {
    return 'Invalid Date';
  }
  const [year, month, day, hour, minute, second] = dateArray;
  return `${day}/${month}/${year} ${hour}:${minute}:${second}`;
};

const calculateTotalPrice = (orderItems) => {
  return orderItems.reduce((sum, item) => sum + item.price, 0);
};

const goToPayment = (orderId) => {
  router.push(`/payment/${orderId}`);
};

onMounted(fetchOrders);
</script>
<style scoped>
#orders-container {
  max-width: 800px;
  margin-top: 100px;
  margin-bottom: 200px;
}

.mt-custom {
  margin-top: 100px;
}
</style>



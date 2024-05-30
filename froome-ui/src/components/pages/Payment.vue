<template>
  <div id="payment-container" class="container mt-custom">
    <h2 class="text-center mb-4">Order Summary</h2>
    <div class="order-details mb-4">
      <p><strong>Order Date:</strong> {{ formatDate(order.orderDate) }}</p>
      <p><strong>Total Amount:</strong> {{ totalAmount }} €</p>
      <p><strong>User:</strong> {{ user.username }}</p>
      <p><strong>Address:</strong> {{ user.address }}</p>
    </div>
    <h4 class="mb-3">Order Items</h4>
    <table class="table table-bordered table-striped text-center">
      <thead>
      <tr>
        <th>Product</th>
        <th>Quantity</th>
        <th>Price</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in order.orderItems" :key="item.id">
        <td>{{ productNames[item.productId] }}</td>
        <td>{{ item.quantity }}</td>
        <td>{{ item.price }} €</td>
      </tr>
      </tbody>
    </table>
    <div class="form-check">
      <input type="checkbox" id="acceptTos" v-model="acceptTos" class="form-check-input">
      <label for="acceptTos" class="form-check-label">I accept the <a href="#">Terms and Conditions</a></label>
    </div>
    <div class="d-flex justify-content-between mt-4">
      <router-link to="/" class="btn btn-secondary left-element">Return to Shopping</router-link>
      <div class="d-flex">
        <button class="btn btn-success right-element me-2" @click="confirmPayment" :disabled="!acceptTos">Proceed to Payment</button>
        <button class="btn btn-danger right-element" @click="cancelOrder">Cancel Order</button>
      </div>
    </div>
    <!-- Confirmation Modal -->
    <div v-if="showConfirmation" class="modal" tabindex="-1" role="dialog" style="display: block;">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Confirm Payment</h5>
            <button type="button" class="close" @click="showConfirmation = false" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <p>The total amount of {{ totalAmount }} € will be charged. Do you want to proceed?</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showConfirmation = false">Cancel</button>
            <button type="button" class="btn btn-primary" @click="processPayment">Confirm</button>
          </div>
        </div>
      </div>
    </div>
    <!-- Success Notification -->
    <SuccessNotification class="notification" v-if="paymentSuccess" message="Payment successful!" :duration="1000" />
    <ErrorNotification class="notification" id="notification" v-if="error" :message="errorMessage" />
    <SuccessNotification class="notification" v-if="cancelSuccess" message="Order canceled successfully!" :duration="1000" />
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import axios from 'axios';
import {useRoute, useRouter} from 'vue-router';
import ErrorNotification from '../notifications/ErrorNotification.vue';
import SuccessNotification from '../notifications/SuccessNotification.vue';

const userUrl = import.meta.env.VITE_USER_API_URL;
const orderUrl = import.meta.env.VITE_ORDER_API_URL;
const productUrl = import.meta.env.VITE_PRODUCT_API_URL;
const paymentUrl = import.meta.env.VITE_PAYMENT_API_URL;
const route = useRoute();
const router = useRouter();
const error = ref(false);
const errorMessage = ref('');
const cancelSuccess = ref(false);

const order = ref({});
const user = ref({});
const productNames = ref({});
const acceptTos = ref(false);
const showConfirmation = ref(false);
const paymentSuccess = ref(false);
const orderId = route.params.orderId;
const totalAmount = ref(0);

const fetchOrderDetails = () => {
  axios.get(`${orderUrl}/api/orders/${orderId}`,
      {headers: {'Authorization': `Bearer ${localStorage.getItem('bearerToken')}`}})
      .then(response => {
        order.value = response.data;
        calculateTotalAmount();
        fetchUserDetails(order.value.userId);
        fetchProductNames(order.value.orderItems);
      })
      .catch(err => {
        console.error('Failed to fetch order details:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while fetching order details.';
      });
};

const fetchUserDetails = (userId) => {
  axios.get(`${userUrl}/api/users/${userId}`,
      {headers: {'Authorization': `Bearer ${localStorage.getItem('bearerToken')}`}})
      .then(response => {
        user.value = response.data;
      })
      .catch(err => {
        console.error('Failed to fetch user details:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while fetching user details.';
      });
};

const fetchProductNames = (orderItems) => {
  const requests = orderItems.map(item => {
    if (!productNames.value[item.productId]) {
      return axios.get(`${productUrl}/api/products/${item.productId}`)
          .then(response => {
            productNames.value[item.productId] = response.data.name;
          })
          .catch(err => {
            console.error(`Failed to fetch product name for productId ${item.productId}:`, err);
          });
    }
  });
  return Promise.all(requests);
};

const calculateTotalAmount = () => {
  totalAmount.value = order.value.orderItems.reduce((sum, item) => sum + item.price, 0);
};

const formatDate = (dateArray) => {
  if (!Array.isArray(dateArray) || dateArray.length < 6) {
    return 'Invalid Date';
  }
  const [year, month, day, hour, minute, second] = dateArray;
  return `${day}/${month}/${year} ${hour}:${minute}:${second}`;
};

const confirmPayment = () => {
  showConfirmation.value = true;
};

const processPayment = () => {
  showConfirmation.value = false;
  axios.post(`${paymentUrl}/api/payments`, {}, {
    params: {orderId: orderId},
    headers: {'Authorization': `Bearer ${localStorage.getItem('bearerToken')}`}
  })
      .then(() => {
        paymentSuccess.value = true;
        showConfirmation.value = false;
        setTimeout(() => {
          router.push('/orders');
        }, 1000);
      })
      .catch(err => {
        console.error('Payment failed:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while processing the payment.';
      });
};

const cancelOrder = () => {
  axios.delete(`${orderUrl}/api/orders/${orderId}`, {
    headers: {'Authorization': `Bearer ${localStorage.getItem('bearerToken')}`}
  })
      .then(() => {
        cancelSuccess.value = true;
        setTimeout(() => {
          router.push('/orders');
        }, 1000);
      })
      .catch(err => {
        console.error('Order cancellation failed:', err);
        error.value = true;
        errorMessage.value = err.response?.data?.message || 'An error occurred while canceling the order.';
      });
};

onMounted(() => {
  fetchOrderDetails();
});
</script>

<style scoped>
#payment-container {
  max-width: 800px;
  margin-top: 100px;
  margin-bottom: 200px;
}

.mt-custom {
  margin-top: 100px;
}

.order-details {
  font-size: 1.2em;
}

.modal {
  background-color: rgba(0, 0, 0, 0.5);
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-dialog {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.notification {
  top: 70px;
}
</style>

<template>
  <div id="register-container" class="container mt-custom">
    <div class="row">
      <div class="col-md-8">
        <h2 class="text-center mb-4">Register</h2>
        <form @submit.prevent="handleRegister" class="bg-light p-4 rounded shadow-sm">
          <div class="mb-3">
            <label for="username" class="form-label">Username:</label>
            <input type="text" id="username" v-model="username" class="form-control" required />
          </div>
          <div class="mb-3">
            <label for="email" class="form-label">Email:</label>
            <input type="email" id="email" v-model="email" class="form-control" required />
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">Password:</label>
            <input type="password" id="password" v-model="password" class="form-control" required />
          </div>
          <div class="mb-3">
            <label for="address" class="form-label">Address:</label>
            <input type="text" id="address" v-model="address" class="form-control" required />
          </div>
          <div class="mb-3 form-check">
            <input type="checkbox" id="admin" v-model="admin" class="form-check-input" />
            <label for="admin" class="form-check-label">Admin</label>
          </div>
          <button type="submit" class="btn btn-primary w-100">Register</button>
        </form>
        <ErrorNotification v-if="error" :message="errorMessage" class="mt-5" />
      </div>
      <div class="col-md-4 d-flex align-items-center justify-content-center">
        <div class="text-center">
          <p>Already have an account?</p>
          <button @click="goToLogin" class="btn btn-link p-1 align-baseline">Login</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import ErrorNotification from '../notifications/ErrorNotification.vue';

const username = ref('');
const email = ref('');
const password = ref('');
const address = ref('');
const admin = ref(false);
const error = ref(false);
const errorMessage = ref('');
const router = useRouter();

const baseUrl = import.meta.env.VITE_USER_API_URL;

const handleRegister = () => {
  error.value = false;
  errorMessage.value = '';

  const registerUrl = `${baseUrl}/api/users/register`;
  const bearerToken = localStorage.getItem('bearerToken');

  const headers = bearerToken ? { Authorization: `Bearer ${bearerToken}` } : {};

  const requestBody = {
    username: username.value,
    email: email.value,
    password: password.value,
    address: address.value,
    admin: admin.value
  };

  axios.post(registerUrl, requestBody, { headers })
      .then(response => {
        console.log('Registration successful:', response.data);
        router.push('/login');
      })
      .catch(err => {
        console.error('Registration failed:', err);
        error.value = true;
        if (err.response && err.response.data) {
          errorMessage.value = err.response.data.message || 'An error occurred during registration.';
        } else {
          errorMessage.value = err.message || 'An unknown error occurred.';
        }
      });
};

const goToLogin = () => {
  router.push('/login');
};
</script>

<style scoped>
#register-container {
  max-width: 800px;
  margin-top: 100px;
}

.mt-custom {
  margin-top: 50px;
}
</style>

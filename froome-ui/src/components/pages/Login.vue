<template>
  <div id="login-container" class="container">
    <h2 class="text-center mb-4">Login</h2>
    <form @submit.prevent="handleLogin" class="bg-light p-4 rounded shadow-sm">
      <div class="mb-3">
        <label for="email" class="form-label">Email:</label>
        <input type="email" id="email" v-model="email" class="form-control" required />
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">Password:</label>
        <input type="password" id="password" v-model="password" class="form-control" required />
      </div>
      <button type="submit" class="btn btn-primary w-100">Login</button>
    </form>
    <ErrorNotification v-if="error" :message="errorMessage" class="mt-5" />
    <div class="mt-3 text-center">
      <p>Don't have an account? <button @click="goToRegister" class="btn btn-link p-1 align-baseline">Register</button></p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import ErrorNotification from '../notifications/ErrorNotification.vue';

const email = ref('');
const password = ref('');
const error = ref(false);
const errorMessage = ref('');
const router = useRouter();
const store = useStore();

const baseUrl = import.meta.env.VITE_USER_API_URL;

const handleLogin = () => {
  error.value = false;
  errorMessage.value = '';

  const loginUrl = `${baseUrl}/api/users/login?email=${encodeURIComponent(email.value)}&password=${encodeURIComponent(password.value)}`;

  axios.post(loginUrl, {})
      .then(response => {
        const bearerToken = response.data;
        localStorage.setItem('bearerToken', bearerToken);
        store.dispatch('fetchUser').then(() => {
          router.push('/');
        });
      })
      .catch(err => {
        console.error('Login failed:', err);
        error.value = true;
        errorMessage.value = err.response.data.message || 'An error occurred during login.';
      });
};

const goToRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
#login-container {
  max-width: 400px;
  margin-top: 100px;
}
</style>

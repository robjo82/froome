<template>
  <div class="logout-container">
    <p >You have been logged out. Redirecting to login page...</p>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import {onMounted} from "vue";

const router = useRouter();
const store = useStore();

store.dispatch('logout').then(() => {
  router.push('/login');
});

onMounted(() => {
  localStorage.removeItem('bearerToken');
  localStorage.removeItem('cart');
});
</script>

<style scoped>
.logout-container {
  max-width: 400px;
  margin: 100px auto;
  text-align: center;
}
</style>

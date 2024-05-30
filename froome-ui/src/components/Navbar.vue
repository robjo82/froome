<template>
  <nav class="navbar navbar-expand-md navbar-dark fixed-top bg-purple">
    <div class="container-fluid">
      <a class="navbar-brand" href="/">Froome</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarCollapse">
        <ul class="navbar-nav me-auto mb-2 mb-md-0">
          <li class="nav-item">
            <router-link class="nav-link" active-class="active" to="/">Home</router-link>
          </li>
          <li class="nav-item" v-if="isLoggedIn">
            <router-link class="nav-link" to="/account">Account</router-link>
          </li>
          <li class="nav-item" v-if="isLoggedIn">
            <router-link class="nav-link" to="/orders">Orders</router-link>
          </li>
          <li class="nav-item" v-if="isAdmin">
            <router-link class="nav-link" to="/admin">Admin</router-link>
          </li>
          <li class="nav-item" v-if="isLoggedIn">
            <router-link class="nav-link" to="/logout">Log out</router-link>
          </li>
        </ul>
        <div class="d-flex align-items-center">
          <router-link to="/basket" class="nav-link text-white position-relative">
            <i class="fas fa-shopping-cart"></i>
            <span v-if="cartItemCount > 0" class="badge bg-danger position-absolute translate-middle-x">{{ cartItemCount }}</span>
          </router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, computed} from 'vue';
import { useStore } from 'vuex';

const store = useStore();

const cartItemCount = ref(0);

const isLoggedIn = computed(() => store.getters.isLoggedIn);
const isAdmin = computed(() => store.getters.isAdmin);

const updateCartItemCount = () => {
  const cart = JSON.parse(localStorage.getItem('cart')) || {};
  cartItemCount.value = Object.values(cart).reduce((acc, quantity) => acc + quantity, 0);
};

onMounted(() => {
  store.dispatch('fetchUser');
  updateCartItemCount();
  const intervalId = setInterval(updateCartItemCount, 1000);
  window.addEventListener('storage', updateCartItemCount);

  onBeforeUnmount(() => {
    clearInterval(intervalId);
    window.removeEventListener('storage', updateCartItemCount);
  });
});
</script>

<style scoped>
.bg-purple {
  background-color: #6f42c1;
}

.badge {
  font-size: 0.6em; /* Adjusted for smaller size */
  padding: 0.25em 0.4em; /* Adjusted padding for smaller size */
  top: -0.2em; /* Adjusted position for smaller size */
  right: 0.5em;
}

.fa-shopping-cart {
  border-right: 1em solid transparent;
}

.position-absolute {
  position: absolute;
}

.translate-middle-x {
  transform: translateX(50%);
}
</style>

<template>
  <div class="card mb-3">
    <div class="card-header">{{ product.name }}</div>
    <div class="card-body">
      <p class="card-text">{{ product.description }}</p>
      <div class="d-flex justify-content-between align-items-center">
        <div class="text-primary">{{ product.price }} €</div>
        <div class="input-group w-auto">
          <button class="btn btn-secondary" @click="decreaseQuantity(product)">-</button>
          <input type="number" class="form-control text-center" v-model.number="quantities[product.id]" @input="updateQuantity(product)" min="0" :max="product.stock || Infinity" />
          <button class="btn btn-secondary" @click="increaseQuantity(product)">+</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>

// Props
defineProps({
  product: Object,
  quantities: Object,
  increaseQuantity: Function,
  decreaseQuantity: Function,
  updateQuantity: Function
});
</script>

<style scoped>
.card-header {
  font-weight: bold;
}
</style>

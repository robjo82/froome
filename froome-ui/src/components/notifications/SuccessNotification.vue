<template>
  <div v-if="visible" class="alert alert-success alert-dismissible fade show" role="alert">
    <strong>Success!</strong> {{ message }}
    <button type="button" class="btn-close" aria-label="Close" @click="closeNotification"></button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

// Props
const props = defineProps({
  message: {
    type: String,
    required: true
  },
  duration: {
    type: Number,
    default: 5000 // 5 seconds
  }
});

// State
const visible = ref(true);

// Methods
const closeNotification = () => {
  visible.value = false;
};

// Automatically hide the notification after the specified duration
watch(visible, (newValue) => {
  if (newValue) {
    setTimeout(() => {
      visible.value = false;
    }, props.duration);
  }
});
</script>


<style scoped>
.alert {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
}
</style>

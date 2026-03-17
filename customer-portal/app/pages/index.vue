<script setup lang="ts">
definePageMeta({ layout: false })

onMounted(() => {
  const token = localStorage.getItem('token')
  navigateTo(token ? '/subscription' : '/login')
})
</script>

<template>
  <div />
</template>

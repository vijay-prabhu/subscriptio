<script setup lang="ts">
const { apiFetch } = useApi()

const hasPaymentMethod = ref(false)
const cardInfo = ref({ brand: 'Visa', lastFour: '4242', expMonth: 12, expYear: 2027 })
const loading = ref(false)

// In a real app, this would fetch from the backend
// For demo, we show a mock payment method
onMounted(() => {
  hasPaymentMethod.value = true
})
</script>

<template>
  <div class="max-w-2xl space-y-6">
    <h2 class="text-lg font-semibold text-white">Billing & Payment</h2>

    <!-- Current Payment Method -->
    <div class="bg-zinc-900 border border-zinc-800 rounded-xl p-6">
      <h3 class="text-sm font-medium text-zinc-400 mb-4">Payment Method</h3>

      <div v-if="hasPaymentMethod" class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <div class="w-12 h-8 bg-zinc-800 rounded flex items-center justify-center">
            <svg class="w-8 h-5 text-blue-400" viewBox="0 0 32 20" fill="currentColor">
              <rect width="32" height="20" rx="3" fill="currentColor" opacity="0.1" />
              <text x="4" y="14" font-size="8" fill="currentColor">VISA</text>
            </svg>
          </div>
          <div>
            <p class="text-white text-sm font-medium">
              {{ cardInfo.brand }} ending in {{ cardInfo.lastFour }}
            </p>
            <p class="text-zinc-500 text-xs">
              Expires {{ cardInfo.expMonth }}/{{ cardInfo.expYear }}
            </p>
          </div>
        </div>
        <button class="px-3 py-1.5 text-xs text-zinc-400 border border-zinc-700 rounded-lg hover:text-white hover:border-zinc-600 transition-colors">
          Update
        </button>
      </div>

      <div v-else class="text-center py-4">
        <p class="text-zinc-400 text-sm">No payment method on file</p>
        <button class="mt-3 px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-500 transition-colors">
          Add Payment Method
        </button>
      </div>
    </div>

    <!-- Billing Info -->
    <div class="bg-zinc-900 border border-zinc-800 rounded-xl p-6">
      <h3 class="text-sm font-medium text-zinc-400 mb-4">Billing Information</h3>
      <div class="space-y-3 text-sm">
        <div class="flex justify-between">
          <span class="text-zinc-500">Next billing date</span>
          <span class="text-white">Apr 16, 2026</span>
        </div>
        <div class="flex justify-between">
          <span class="text-zinc-500">Billing cycle</span>
          <span class="text-white">Monthly</span>
        </div>
      </div>
    </div>

    <!-- Stripe Integration Note -->
    <div class="bg-zinc-900/50 border border-zinc-800 rounded-xl p-4">
      <p class="text-xs text-zinc-600">
        Payment processing is handled securely by Stripe. Your card details are never stored on our servers.
      </p>
    </div>
  </div>
</template>

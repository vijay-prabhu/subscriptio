<script setup lang="ts">
import { formatCurrency, formatDate } from '~/utils/formatters'

interface Plan {
  id: string
  name: string
  price: number
  currency: string
  billingInterval: string
  trialDays: number
  description: string
  isActive: boolean
}

interface Subscription {
  id: string
  status: string
  plan: Plan
  currentPeriodStart: string
  currentPeriodEnd: string
  trialEnd: string | null
  cancelAtPeriodEnd: boolean
}

const { apiFetch } = useApi()

const subscription = ref<Subscription | null>(null)
const plans = ref<Plan[]>([])
const loading = ref(true)
const error = ref('')
const cancelDialogOpen = ref(false)
const cancelling = ref(false)

// Load tenant, subscription, and plans
onMounted(async () => {
  try {
    const tenants = await apiFetch<{ content: { id: string }[] }>('/api/v1/admin/tenants?page=0&size=1')
    if (tenants.content.length > 0) {
      const tenantId = tenants.content[0].id
      const plansData = await apiFetch<{ content: Plan[] }>(`/api/v1/admin/plans?tenantId=${tenantId}&page=0&size=20`)
      plans.value = plansData.content.filter(p => p.isActive)

      const subsData = await apiFetch<{ content: Subscription[] }>(`/api/v1/admin/subscriptions?tenantId=${tenantId}&page=0&size=1`)
      if (subsData.content.length > 0) {
        subscription.value = subsData.content[0]
      }
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Failed to load'
  } finally {
    loading.value = false
  }
})

async function cancelSubscription() {
  if (!subscription.value) return
  cancelling.value = true
  try {
    const updated = await apiFetch<Subscription>(`/api/v1/subscription/${subscription.value.id}/cancel`, {
      method: 'POST',
    })
    subscription.value = updated
    cancelDialogOpen.value = false
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Cancel failed'
  } finally {
    cancelling.value = false
  }
}
</script>

<template>
  <div class="max-w-4xl space-y-8">
    <h2 class="text-lg font-semibold text-white">My Subscription</h2>

    <div v-if="loading" class="text-zinc-500">Loading...</div>

    <div v-else-if="subscription" class="space-y-6">
      <!-- Current Plan Card -->
      <div class="bg-zinc-900 border border-zinc-800 rounded-xl p-6">
        <div class="flex items-start justify-between">
          <div>
            <div class="flex items-center gap-3">
              <h3 class="text-xl font-bold text-white">{{ subscription.plan.name }}</h3>
              <StatusBadge :status="subscription.status" />
            </div>
            <p class="text-zinc-500 text-sm mt-1">{{ subscription.plan.description }}</p>
          </div>
          <div class="text-right">
            <p class="text-2xl font-bold text-white">{{ formatCurrency(subscription.plan.price) }}</p>
            <p class="text-zinc-500 text-sm">/ {{ subscription.plan.billingInterval }}</p>
          </div>
        </div>

        <div class="mt-6 grid grid-cols-2 gap-4 text-sm">
          <div>
            <p class="text-zinc-500">Current Period</p>
            <p class="text-white">{{ formatDate(subscription.currentPeriodStart) }} - {{ formatDate(subscription.currentPeriodEnd) }}</p>
          </div>
          <div v-if="subscription.trialEnd">
            <p class="text-zinc-500">Trial Ends</p>
            <p class="text-white">{{ formatDate(subscription.trialEnd) }}</p>
          </div>
        </div>

        <div v-if="subscription.cancelAtPeriodEnd" class="mt-4 p-3 bg-amber-900/20 border border-amber-800 rounded-lg text-sm text-amber-400">
          Your subscription will be cancelled at the end of the current period.
        </div>

        <div v-if="!subscription.cancelAtPeriodEnd && subscription.status !== 'CANCELLED' && subscription.status !== 'EXPIRED'" class="mt-6">
          <button
            class="px-4 py-2 text-sm text-red-400 border border-red-800 rounded-lg hover:bg-red-900/20 transition-colors"
            @click="cancelDialogOpen = true"
          >
            Cancel Subscription
          </button>
        </div>
      </div>

      <!-- Available Plans -->
      <div>
        <h3 class="text-sm font-medium text-zinc-400 mb-4">Available Plans</h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div
            v-for="plan in plans"
            :key="plan.id"
            :class="[
              'bg-zinc-900 border rounded-xl p-5 transition-colors',
              plan.id === subscription.plan.id
                ? 'border-blue-600'
                : 'border-zinc-800 hover:border-zinc-700'
            ]"
          >
            <div class="flex items-center justify-between mb-2">
              <h4 class="font-semibold text-white">{{ plan.name }}</h4>
              <span v-if="plan.id === subscription.plan.id" class="text-xs text-blue-400">Current</span>
            </div>
            <p class="text-zinc-500 text-xs mb-3">{{ plan.description }}</p>
            <p class="text-lg font-bold text-white">
              {{ formatCurrency(plan.price) }}
              <span class="text-zinc-500 text-sm font-normal">/ {{ plan.billingInterval }}</span>
            </p>
            <p v-if="plan.trialDays > 0" class="text-xs text-blue-400 mt-1">{{ plan.trialDays }}-day free trial</p>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="bg-zinc-900 border border-zinc-800 rounded-xl p-8 text-center">
      <p class="text-zinc-400">No active subscription</p>
      <p class="text-zinc-600 text-sm mt-1">Choose a plan to get started</p>
    </div>

    <!-- Cancel Dialog -->
    <div v-if="cancelDialogOpen" class="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
      <div class="bg-zinc-900 border border-zinc-800 rounded-xl p-6 max-w-sm w-full mx-4">
        <h3 class="text-lg font-semibold text-white">Cancel Subscription?</h3>
        <p class="text-zinc-400 text-sm mt-2">
          Your subscription will remain active until the end of the current billing period. You won't be charged again.
        </p>
        <div class="flex gap-3 mt-6">
          <button
            class="flex-1 py-2 text-sm bg-zinc-800 text-white rounded-lg hover:bg-zinc-700 transition-colors"
            @click="cancelDialogOpen = false"
          >
            Keep Plan
          </button>
          <button
            :disabled="cancelling"
            class="flex-1 py-2 text-sm bg-red-600 text-white rounded-lg hover:bg-red-500 transition-colors disabled:opacity-50"
            @click="cancelSubscription"
          >
            {{ cancelling ? 'Cancelling...' : 'Cancel Plan' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

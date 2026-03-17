<script setup lang="ts">
import { formatCurrency, formatDate } from '~/utils/formatters'

interface Invoice {
  id: string
  invoiceNumber: string
  status: string
  total: number
  currency: string
  dueDate: string
  paidAt: string | null
  periodStart: string
  periodEnd: string
  lineItems: { description: string; quantity: number; unitPrice: number; amount: number }[]
}

const { apiFetch } = useApi()

const invoices = ref<Invoice[]>([])
const loading = ref(true)
const selectedInvoice = ref<Invoice | null>(null)

onMounted(async () => {
  try {
    const tenants = await apiFetch<{ content: { id: string }[] }>('/api/v1/admin/tenants?page=0&size=1')
    if (tenants.content.length > 0) {
      const data = await apiFetch<{ content: Invoice[] }>(
        `/api/v1/admin/invoices?tenantId=${tenants.content[0].id}&page=0&size=50`
      )
      invoices.value = data.content
    }
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="max-w-4xl space-y-6">
    <h2 class="text-lg font-semibold text-white">Invoices</h2>

    <div v-if="loading" class="text-zinc-500">Loading...</div>

    <div v-else-if="invoices.length === 0" class="bg-zinc-900 border border-zinc-800 rounded-xl p-8 text-center">
      <p class="text-zinc-400">No invoices yet</p>
      <p class="text-zinc-600 text-sm mt-1">Invoices appear after your first billing cycle</p>
    </div>

    <div v-else class="space-y-3">
      <div
        v-for="inv in invoices"
        :key="inv.id"
        class="bg-zinc-900 border border-zinc-800 rounded-xl p-4 hover:border-zinc-700 transition-colors cursor-pointer"
        @click="selectedInvoice = inv"
      >
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div>
              <p class="text-white font-medium text-sm">{{ inv.invoiceNumber }}</p>
              <p class="text-zinc-500 text-xs">{{ formatDate(inv.periodStart) }} - {{ formatDate(inv.periodEnd) }}</p>
            </div>
          </div>
          <div class="flex items-center gap-4">
            <StatusBadge :status="inv.status" />
            <p class="text-white font-semibold">{{ formatCurrency(inv.total, inv.currency) }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Invoice Detail Modal -->
    <div v-if="selectedInvoice" class="fixed inset-0 bg-black/60 flex items-center justify-center z-50">
      <div class="bg-zinc-900 border border-zinc-800 rounded-xl p-6 max-w-md w-full mx-4">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-white">{{ selectedInvoice.invoiceNumber }}</h3>
          <StatusBadge :status="selectedInvoice.status" />
        </div>

        <div class="space-y-3 text-sm">
          <div class="flex justify-between">
            <span class="text-zinc-500">Period</span>
            <span class="text-white">{{ formatDate(selectedInvoice.periodStart) }} - {{ formatDate(selectedInvoice.periodEnd) }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-zinc-500">Due Date</span>
            <span class="text-white">{{ formatDate(selectedInvoice.dueDate) }}</span>
          </div>
          <div v-if="selectedInvoice.paidAt" class="flex justify-between">
            <span class="text-zinc-500">Paid</span>
            <span class="text-emerald-400">{{ formatDate(selectedInvoice.paidAt) }}</span>
          </div>

          <hr class="border-zinc-800" />

          <div v-for="item in selectedInvoice.lineItems" :key="item.description" class="flex justify-between">
            <span class="text-zinc-400">{{ item.description }}</span>
            <span class="text-white">{{ formatCurrency(item.amount) }}</span>
          </div>

          <hr class="border-zinc-800" />

          <div class="flex justify-between font-semibold">
            <span class="text-white">Total</span>
            <span class="text-white">{{ formatCurrency(selectedInvoice.total) }}</span>
          </div>
        </div>

        <button
          class="w-full mt-6 py-2 text-sm bg-zinc-800 text-white rounded-lg hover:bg-zinc-700 transition-colors"
          @click="selectedInvoice = null"
        >
          Close
        </button>
      </div>
    </div>
  </div>
</template>

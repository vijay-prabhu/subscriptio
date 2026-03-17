<script setup lang="ts">
const navItems = [
  { to: '/subscription', label: 'Subscription', icon: 'M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15' },
  { to: '/invoices', label: 'Invoices', icon: 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z' },
  { to: '/billing', label: 'Billing', icon: 'M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z' },
]

const route = useRoute()

function handleLogout() {
  if (import.meta.client) {
    localStorage.removeItem('token')
    navigateTo('/login')
  }
}
</script>

<template>
  <div class="min-h-screen bg-zinc-950 flex">
    <aside class="w-60 bg-zinc-900 border-r border-zinc-800 flex flex-col">
      <div class="p-6">
        <h1 class="text-lg font-bold text-white">SubscriptIO</h1>
        <p class="text-xs text-zinc-500 mt-0.5">Customer Portal</p>
      </div>
      <nav class="flex-1 px-3 space-y-1">
        <NuxtLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          :class="[
            'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-colors',
            route.path.startsWith(item.to)
              ? 'bg-blue-600/20 text-blue-400'
              : 'text-zinc-400 hover:text-white hover:bg-zinc-800'
          ]"
        >
          <svg class="w-5 h-5 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
            <path stroke-linecap="round" stroke-linejoin="round" :d="item.icon" />
          </svg>
          {{ item.label }}
        </NuxtLink>
      </nav>
      <div class="p-3">
        <button
          class="w-full px-3 py-2 text-sm text-zinc-500 hover:text-white rounded-lg hover:bg-zinc-800 transition-colors text-left"
          @click="handleLogout"
        >
          Sign out
        </button>
      </div>
    </aside>
    <main class="flex-1 p-8">
      <slot />
    </main>
  </div>
</template>

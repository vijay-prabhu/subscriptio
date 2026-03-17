<script setup lang="ts">
definePageMeta({ layout: 'auth' })

const { apiFetch, setToken } = useApi()

const email = ref('alice@example.com')
const password = ref('demo')
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  loading.value = true
  error.value = ''

  try {
    const data = await apiFetch<{ accessToken: string }>('/api/v1/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email: email.value, password: password.value }),
    })
    setToken(data.accessToken)
    navigateTo('/subscription')
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Login failed'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="w-full max-w-sm">
    <div class="text-center mb-8">
      <h1 class="text-2xl font-bold text-white">SubscriptIO</h1>
      <p class="text-zinc-500 text-sm mt-1">Customer Portal</p>
    </div>
    <form class="bg-zinc-900 border border-zinc-800 rounded-xl p-6 space-y-4" @submit.prevent="handleLogin">
      <div>
        <label class="block text-sm text-zinc-400 mb-1">Email</label>
        <input
          v-model="email"
          type="email"
          required
          class="w-full px-3 py-2 bg-zinc-800 border border-zinc-700 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
      <div>
        <label class="block text-sm text-zinc-400 mb-1">Password</label>
        <input
          v-model="password"
          type="password"
          required
          class="w-full px-3 py-2 bg-zinc-800 border border-zinc-700 rounded-lg text-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
      <p v-if="error" class="text-red-400 text-sm">{{ error }}</p>
      <button
        type="submit"
        :disabled="loading"
        class="w-full py-2.5 bg-blue-600 hover:bg-blue-500 text-white text-sm font-medium rounded-lg transition-colors disabled:opacity-50"
      >
        {{ loading ? 'Signing in...' : 'Sign in' }}
      </button>
      <p class="text-xs text-zinc-600 text-center">Demo: alice@example.com / demo</p>
    </form>
  </div>
</template>

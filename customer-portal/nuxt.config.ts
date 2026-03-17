export default defineNuxtConfig({
  compatibilityDate: '2024-11-01',
  devtools: { enabled: true },
  future: { compatibilityVersion: 4 },

  css: ['~/assets/css/main.css'],

  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },

  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || 'http://localhost:8080',
    },
  },

  app: {
    head: {
      title: 'SubscriptIO - Customer Portal',
      meta: [
        { name: 'description', content: 'Manage your subscription, invoices, and billing' },
      ],
    },
  },
})

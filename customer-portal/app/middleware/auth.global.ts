export default defineNuxtRouteMiddleware((to) => {
  if (import.meta.server) return

  const publicRoutes = ['/login', '/']
  if (publicRoutes.includes(to.path)) return

  const token = localStorage.getItem('token')
  if (!token) {
    return navigateTo('/login')
  }
})

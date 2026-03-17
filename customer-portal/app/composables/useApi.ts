export function useApi() {
  const config = useRuntimeConfig()
  const apiBase = config.public.apiBase

  function getToken(): string | null {
    if (!import.meta.client) return null
    return localStorage.getItem('token')
  }

  function setToken(token: string) {
    if (import.meta.client) {
      localStorage.setItem('token', token)
    }
  }

  function clearToken() {
    if (import.meta.client) {
      localStorage.removeItem('token')
    }
  }

  async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
    const token = getToken()
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...((options.headers as Record<string, string>) || {}),
    }

    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }

    const res = await fetch(`${apiBase}${path}`, { ...options, headers })

    if (res.status === 401) {
      clearToken()
      if (import.meta.client) navigateTo('/login')
      throw new Error('Unauthorized')
    }

    if (!res.ok) {
      const error = await res.json().catch(() => ({ message: 'Request failed' }))
      throw new Error(error.message || `HTTP ${res.status}`)
    }

    return res.json()
  }

  return { apiFetch, getToken, setToken, clearToken }
}

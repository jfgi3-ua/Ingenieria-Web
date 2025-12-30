import { beforeEach, describe, expect, it, vi } from "vitest"
import { createPinia, setActivePinia } from "pinia"

async function setupRouter() {
  setActivePinia(createPinia())
  const { useAuthStore } = await import("@/stores/auth.store")
  const store = useAuthStore()
  store.loadSession = vi.fn().mockResolvedValue(null) as unknown as typeof store.loadSession
  const { default: router } = await import("@/router/index")
  return { router, store }
}

describe("router guard", () => {
  beforeEach(() => {
    vi.resetModules()
    vi.clearAllMocks()
  })

  it("redirige a /login si la ruta requiere auth y no hay sesion", async () => {
    const { router, store } = await setupRouter()
    store.isAuthenticated = false

    await router.push("/servicios")
    await router.isReady()

    expect(router.currentRoute.value.path).toBe("/login")
    expect(router.currentRoute.value.query.redirect).toBe("/servicios")
    expect(router.currentRoute.value.query.reason).toBe("auth")
  })

  it("redirige a /inicio si ya esta autenticado y navega a /login", async () => {
    const { router, store } = await setupRouter()
    store.isAuthenticated = true

    await router.push("/login")
    await router.isReady()

    expect(router.currentRoute.value.path).toBe("/inicio")
  })

  it("permite acceder a /inicio sin sesion", async () => {
    const { router, store } = await setupRouter()
    store.isAuthenticated = false

    await router.push("/inicio")
    await router.isReady()

    expect(router.currentRoute.value.path).toBe("/inicio")
  })
})

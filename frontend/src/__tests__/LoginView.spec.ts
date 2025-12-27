import { beforeEach, describe, expect, it, vi } from "vitest"
import { flushPromises, mount } from "@vue/test-utils"
import { createPinia, setActivePinia } from "pinia"
import { createRouter, createMemoryHistory } from "vue-router"
import LoginView from "@/views/LoginView.vue"
import { useAuthStore } from "@/stores/auth.store"

async function mountWithRouter(query: Record<string, string> = {}) {
  const router = createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: "/login", component: LoginView },
      { path: "/inicio", component: { template: "<div />" } },
      { path: "/registro", component: { template: "<div />" } },
    ],
  })

  await router.push({ path: "/login", query })
  await router.isReady()

  const wrapper = mount(LoginView, {
    global: { plugins: [router] },
  })

  return { wrapper, router }
}

describe("LoginView", () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it("muestra mensaje de sesion requerida si reason=auth", async () => {
    const { wrapper } = await mountWithRouter({ reason: "auth" })
    expect(wrapper.text()).toContain("Sesion requerida:")
  })

  it("envia login y redirige al destino", async () => {
    const store = useAuthStore()
    const loginSpy = vi.spyOn(store, "login").mockResolvedValue({ id: 1 } as any)

    const { wrapper, router } = await mountWithRouter({ redirect: "/inicio" })
    const replaceSpy = vi.spyOn(router, "replace").mockResolvedValue(undefined as any)

    await wrapper.find('input[type="email"]').setValue("test@fitgym.com")
    await wrapper.find('input[type="password"]').setValue("Password123")
    await wrapper.find("form").trigger("submit.prevent")
    await flushPromises()

    expect(loginSpy).toHaveBeenCalled()
    expect(replaceSpy).toHaveBeenCalledWith("/inicio")
  })

  it("muestra error de credenciales cuando el login falla", async () => {
    const store = useAuthStore()
    vi.spyOn(store, "login").mockRejectedValue(new Error("HTTP 401: Credenciales invalidas."))

    const { wrapper } = await mountWithRouter()

    await wrapper.find('input[type="email"]').setValue("test@fitgym.com")
    await wrapper.find('input[type="password"]').setValue("Password123")
    await wrapper.find("form").trigger("submit.prevent")
    await flushPromises()

    expect(wrapper.text()).toContain("Credenciales invalidas.")
  })
})

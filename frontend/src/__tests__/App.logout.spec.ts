import { describe, expect, it, vi } from "vitest"
import { mount } from "@vue/test-utils"
import { createPinia, setActivePinia } from "pinia"
import { createRouter, createMemoryHistory } from "vue-router"
import App from "@/App.vue"
import { useAuthStore } from "@/stores/auth.store"

describe("App logout", () => {
  it("muestra logout y redirige al pulsar", async () => {
    const pinia = createPinia()
    setActivePinia(pinia)
    const store = useAuthStore()
    store.socio = {
      id: 1,
      nombre: "Test",
      correoElectronico: "test@fitgym.com",
      estado: "ACTIVO",
      idTarifa: 1,
      tarifaNombre: "Basico",
    }

    const router = createRouter({
      history: createMemoryHistory(),
      routes: [
        { path: "/inicio", component: { template: "<div />" } },
        { path: "/login", component: { template: "<div />" } },
      ],
    })

    const replaceSpy = vi.spyOn(router, "replace").mockResolvedValue(undefined as any)
    const logoutSpy = vi.spyOn(store, "logout").mockResolvedValue(undefined as any)

    const wrapper = mount(App, {
      global: { plugins: [pinia, router] },
    })

    await router.isReady()

    const button = wrapper.get(".logout-navbar-button")
    await button.trigger("click")

    expect(logoutSpy).toHaveBeenCalled()
    expect(replaceSpy).toHaveBeenCalledWith("/login")
  })
})

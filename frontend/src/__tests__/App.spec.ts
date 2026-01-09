import { describe, it, expect } from "vitest"
import { mount } from "@vue/test-utils"
import { createPinia, setActivePinia } from "pinia"
import { createRouter, createMemoryHistory } from "vue-router"
import App from "../App.vue"

describe("App", () => {
  it("renders navigation links", async () => {
    // App usa RouterLink/RouterView y Pinia, asi que inyectamos ambos.
    const pinia = createPinia()
    setActivePinia(pinia)

    const router = createRouter({
      history: createMemoryHistory(),
      routes: [
        { path: "/registro", component: { template: "<div />" } },
        { path: "/login", component: { template: "<div />" } },
        { path: "/inicio", component: { template: "<div />" } },
      ],
    })

    const wrapper = mount(App, {
      global: { plugins: [pinia, router] },
    })

    await router.isReady()

    expect(wrapper.text()).toContain("Registrarse")
    expect(wrapper.text()).toContain("Iniciar ses")
  })
})

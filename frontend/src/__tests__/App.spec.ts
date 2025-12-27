import { describe, it, expect } from "vitest"
import { mount } from "@vue/test-utils"
import { createRouter, createWebHistory } from "vue-router"
import App from "../App.vue"

describe("App", () => {
  it("renders navigation links", async () => {
    // App uses RouterLink/RouterView, so provide a minimal router to avoid injection errors.
    const router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: "/tarifas", component: { template: "<div />" } },
        { path: "/registro", component: { template: "<div />" } },
        { path: "/inicio", component: { template: "<div />" } },
      ],
    })

    const wrapper = mount(App, {
      global: { plugins: [router] },
    })

    await router.isReady()

    expect(wrapper.text()).toContain("Tarifas")
    expect(wrapper.text()).toContain("Registro")
  })
})

import { describe, expect, it, vi, beforeEach } from "vitest"
import { flushPromises, mount } from "@vue/test-utils"
import { createRouter, createMemoryHistory } from "vue-router"
import RegistroView from "@/views/RegistroView.vue"

// Mock API layer to keep tests focused on the wizard flow and validation logic.
vi.mock("@/services/tarifas", () => ({
  listarTarifas: vi.fn().mockResolvedValue([
    { id: 1, nombre: "Basico", cuota: 29.99, descripcion: "Plan basico", clasesGratisMes: 2 },
    { id: 2, nombre: "Premium", cuota: 49.99, descripcion: "Plan premium", clasesGratisMes: 6 },
  ]),
}))

vi.mock("@/services/pagos", () => ({
  initPago: vi.fn(),
  verifyPago: vi.fn(),
}))

vi.mock("@/services/socios", () => ({
  registrarSocio: vi.fn(),
}))

async function mountRegistro() {
  const router = createRouter({
    history: createMemoryHistory(),
    routes: [{ path: "/registro", component: RegistroView }],
  })
  await router.push("/registro")
  await router.isReady()

  const wrapper = mount(RegistroView, {
    global: {
      plugins: [router],
    },
  })
  await flushPromises()
  return wrapper
}

describe("RegistroView", () => {
  beforeEach(() => {
    sessionStorage.clear()
  })

  it("avanza al paso 2 cuando los datos personales son validos", async () => {
    const wrapper = await mountRegistro()

    await wrapper.find('input[placeholder="Ej: Juan PÇ¸rez GarcÇða"]').setValue("Juan Perez")
    await wrapper.find('input[placeholder="tu.email@ejemplo.com"]').setValue("juan@example.com")
    await wrapper.find('input[placeholder="MÇðnimo 8 caracteres"]').setValue("password123")
    await wrapper.find('input[placeholder="Repite tu contraseÇña"]').setValue("password123")
    await wrapper.find('input[placeholder="Calle, nÇ§mero, piso, puerta"]').setValue("Calle 1")
    await wrapper.find('input[placeholder="Ej: Madrid"]').setValue("Madrid")
    await wrapper.find('input[placeholder="28001"]').setValue("28001")

    await wrapper.find("form").trigger("submit")

    expect(wrapper.text()).toContain("SelecciÇün de tarifa")
  })

  it("bloquea continuar en paso 2 si no se confirma el pago", async () => {
    const wrapper = await mountRegistro()

    await wrapper.find('input[placeholder="Ej: Juan PÇ¸rez GarcÇða"]').setValue("Juan Perez")
    await wrapper.find('input[placeholder="tu.email@ejemplo.com"]').setValue("juan@example.com")
    await wrapper.find('input[placeholder="MÇðnimo 8 caracteres"]').setValue("password123")
    await wrapper.find('input[placeholder="Repite tu contraseÇña"]').setValue("password123")
    await wrapper.find('input[placeholder="Calle, nÇ§mero, piso, puerta"]').setValue("Calle 1")
    await wrapper.find('input[placeholder="Ej: Madrid"]').setValue("Madrid")
    await wrapper.find('input[placeholder="28001"]').setValue("28001")

    await wrapper.find("form").trigger("submit")

    const continuar = wrapper.findAll("button").find((btn) => btn.text() === "Continuar")
    expect(continuar?.attributes("disabled")).toBeDefined()
  })
})

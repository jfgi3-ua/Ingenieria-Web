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
  emailExists: vi.fn().mockResolvedValue(false),
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

    const textInputs = wrapper.findAll('input[type="text"]')
    const passwordInputs = wrapper.findAll('input[type="password"]')

    await textInputs[0].setValue("Juan Perez")
    await wrapper.find('input[type="email"]').setValue("juan@example.com")
    await passwordInputs[0].setValue("password123")
    await passwordInputs[1].setValue("password123")
    await wrapper.find('input[type="tel"]').setValue("+34 600 00 00 00")
    await textInputs[1].setValue("Calle 1")
    await textInputs[2].setValue("Madrid")
    await textInputs[3].setValue("28001")

    await wrapper.find("form").trigger("submit")
    await flushPromises()

    expect(wrapper.text()).toContain("Pago seguro")
  })

  it("bloquea continuar en paso 2 si no se confirma el pago", async () => {
    const wrapper = await mountRegistro()

    const textInputs = wrapper.findAll('input[type="text"]')
    const passwordInputs = wrapper.findAll('input[type="password"]')

    await textInputs[0].setValue("Juan Perez")
    await wrapper.find('input[type="email"]').setValue("juan@example.com")
    await passwordInputs[0].setValue("password123")
    await passwordInputs[1].setValue("password123")
    await wrapper.find('input[type="tel"]').setValue("+34 600 00 00 00")
    await textInputs[1].setValue("Calle 1")
    await textInputs[2].setValue("Madrid")
    await textInputs[3].setValue("28001")

    await wrapper.find("form").trigger("submit")
    await flushPromises()

    const continuar = wrapper.findAll("button").find((btn) => btn.text() === "Continuar")
    expect(continuar?.attributes("disabled")).toBeDefined()
  })

  it("bloquea avanzar si el correo ya existe", async () => {
    const { emailExists } = await import("@/services/socios")
    ;(emailExists as unknown as ReturnType<typeof vi.fn>).mockResolvedValueOnce(true)

    const wrapper = await mountRegistro()

    const textInputs = wrapper.findAll('input[type="text"]')
    const passwordInputs = wrapper.findAll('input[type="password"]')

    await textInputs[0].setValue("Juan Perez")
    await wrapper.find('input[type="email"]').setValue("juan@example.com")
    await passwordInputs[0].setValue("password123")
    await passwordInputs[1].setValue("password123")
    await wrapper.find('input[type="tel"]').setValue("+34 600 00 00 00")
    await textInputs[1].setValue("Calle 1")
    await textInputs[2].setValue("Madrid")
    await textInputs[3].setValue("28001")

    await wrapper.find("form").trigger("submit")
    await flushPromises()

    expect(wrapper.text()).not.toContain("Pago seguro")
    expect(wrapper.text()).toContain("Ese correo ya esta registrado.")
  })

  it("muestra comprobando mientras valida el correo", async () => {
    const { emailExists } = await import("@/services/socios")
    let resolveCheck: (value: boolean) => void
    const deferred = new Promise<boolean>((resolve) => {
      resolveCheck = resolve
    })
    ;(emailExists as unknown as ReturnType<typeof vi.fn>).mockReturnValueOnce(deferred)

    const wrapper = await mountRegistro()
    await wrapper.find('input[type="email"]').setValue("juan@example.com")
    await wrapper.find('input[type="email"]').trigger("blur")

    expect(wrapper.text()).toContain("Comprobando...")

    resolveCheck!(false)
    await flushPromises()
  })
})

import { describe, expect, it, vi } from "vitest"
import { flushPromises, mount } from "@vue/test-utils"
import RegistroView from "@/views/RegistroView.vue"

// Mock API layer to keep tests focused on the wizard flow and validation logic.
vi.mock("@/services/tarifas", () => ({
  listarTarifas: vi.fn().mockResolvedValue([
    { id: 1, nombre: "Basico", cuota: 29.99, descripcion: "Plan básico", clasesGratisMes: 2 },
    { id: 2, nombre: "Premium", cuota: 49.99, descripcion: "Plan premium", clasesGratisMes: 6 },
  ]),
}))

vi.mock("@/services/socios", () => ({
  registrarSocio: vi.fn(),
}))

/**
 * Suite de pruebas para la vista de registro de socios ({@link RegistroView}).
 *
 * Estas pruebas se centran en:
 * - El flujo del asistente de registro (cambio entre pasos).
 * - La validación de los datos personales en el primer paso.
 * - Las restricciones de navegación en el segundo paso (confirmación de pago).
 *
 * Se mockean las dependencias de la capa de servicios para evitar llamadas
 * reales a la API y poder aislar el comportamiento de la interfaz.
 */
describe("RegistroView", () => {
  /**
   * Verifica que, cuando el usuario introduce datos personales válidos
   * en todos los campos obligatorios del formulario del paso 1 y envía
   * el formulario, el asistente avanza correctamente al paso 2
   * (selección de tarifa).
   */
  it("avanza al paso 2 cuando los datos personales son válidos", async () => {
    const wrapper = mount(RegistroView)
    await flushPromises()

    await wrapper.find('input[placeholder="Ej: Juan Pérez García"]').setValue("Juan Perez")
    await wrapper.find('input[placeholder="tu.email@ejemplo.com"]').setValue("juan@example.com")
    await wrapper.find('input[placeholder="Mínimo 8 caracteres"]').setValue("password123")
    await wrapper.find('input[placeholder="Repite tu contraseña"]').setValue("password123")
    await wrapper.find('input[placeholder="Calle, número, piso, puerta"]').setValue("Calle 1")
    await wrapper.find('input[placeholder="Ej: Madrid"]').setValue("Madrid")
    await wrapper.find('input[placeholder="28001"]').setValue("28001")

    await wrapper.find("form").trigger("submit")

    expect(wrapper.text()).toContain("Selección de tarifa")
  })

  /**
   * Verifica que, en el paso 2 del asistente, el botón principal de
   * acción ("Continuar") permanece deshabilitado mientras el usuario
   * no haya confirmado el pago, impidiendo avanzar sin completar
   * correctamente este requisito.
   */
  it("bloquea continuar en paso 2 si no se confirma el pago", async () => {
    const wrapper = mount(RegistroView)
    await flushPromises()

    await wrapper.find('input[placeholder="Ej: Juan Pérez García"]').setValue("Juan Perez")
    await wrapper.find('input[placeholder="tu.email@ejemplo.com"]').setValue("juan@example.com")
    await wrapper.find('input[placeholder="Mínimo 8 caracteres"]').setValue("password123")
    await wrapper.find('input[placeholder="Repite tu contraseña"]').setValue("password123")
    await wrapper.find('input[placeholder="Calle, número, piso, puerta"]').setValue("Calle 1")
    await wrapper.find('input[placeholder="Ej: Madrid"]').setValue("Madrid")
    await wrapper.find('input[placeholder="28001"]').setValue("28001")

    await wrapper.find("form").trigger("submit")

    const continuar = wrapper.findAll("button").find((btn) => btn.text() === "Continuar")
    expect(continuar?.attributes("disabled")).toBeDefined()
  })
})

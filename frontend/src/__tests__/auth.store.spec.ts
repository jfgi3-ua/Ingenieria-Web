import { describe, expect, it, vi, beforeEach } from "vitest"
import { createPinia, setActivePinia } from "pinia"
import { useAuthStore } from "@/stores/auth.store"

vi.mock("@/services/socios", () => ({
  login: vi.fn(),
  logout: vi.fn(),
  me: vi.fn(),
}))

const sociosService = await import("@/services/socios")

describe("auth.store", () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it("login guarda socio y marca autenticado", async () => {
    const store = useAuthStore()
    const socio = { id: 1, nombre: "Test", correoElectronico: "test@fitgym.com" }
    ;(sociosService.login as unknown as ReturnType<typeof vi.fn>).mockResolvedValue(socio)

    const result = await store.login({
      correoElectronico: "test@fitgym.com",
      contrasena: "Password123",
    })

    expect(result).toEqual(socio)
    expect(store.socio).toEqual(socio)
    expect(store.isAuthenticated).toBe(true)
  })

  it("logout limpia el estado local", async () => {
    const store = useAuthStore()
    store.socio = { id: 1, nombre: "Test", correoElectronico: "test@fitgym.com" } as any
    store.isAuthenticated = true

    ;(sociosService.logout as unknown as ReturnType<typeof vi.fn>).mockResolvedValue(undefined)

    await store.logout()

    expect(store.socio).toBeNull()
    expect(store.isAuthenticated).toBe(false)
  })

  it("loadSession restaura sesion si /me responde OK", async () => {
    const store = useAuthStore()
    const socio = { id: 1, nombre: "Test", correoElectronico: "test@fitgym.com" }
    ;(sociosService.me as unknown as ReturnType<typeof vi.fn>).mockResolvedValue(socio)

    const result = await store.loadSession()

    expect(result).toEqual(socio)
    expect(store.socio).toEqual(socio)
    expect(store.isAuthenticated).toBe(true)
  })

  it("loadSession limpia sesion si /me falla", async () => {
    const store = useAuthStore()
    store.socio = { id: 1, nombre: "Test", correoElectronico: "test@fitgym.com" } as any
    store.isAuthenticated = true

    ;(sociosService.me as unknown as ReturnType<typeof vi.fn>).mockRejectedValue(new Error("401"))

    const result = await store.loadSession()

    expect(result).toBeNull()
    expect(store.socio).toBeNull()
    expect(store.isAuthenticated).toBe(false)
  })
})

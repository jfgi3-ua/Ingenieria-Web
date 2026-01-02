import { describe, expect, it, vi } from "vitest"
import { login, logout, me } from "@/services/socios"

describe("services/socios", () => {
  it("login llama a /api/socios/login con payload", async () => {
    const fetchMock = vi.fn().mockResolvedValue(
      new Response(JSON.stringify({ id: 1 }), {
        status: 200,
        headers: { "Content-Type": "application/json" },
      })
    )
    vi.stubGlobal("fetch", fetchMock)

    await login({ correoElectronico: "test@fitgym.com", contrasena: "Password123" })

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/socios/login",
      expect.objectContaining({ method: "POST" })
    )
  })

  it("me llama a /api/socios/me", async () => {
    const fetchMock = vi.fn().mockResolvedValue(
      new Response(JSON.stringify({ id: 1 }), {
        status: 200,
        headers: { "Content-Type": "application/json" },
      })
    )
    vi.stubGlobal("fetch", fetchMock)

    await me()

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/socios/me",
      expect.objectContaining({ method: "GET" })
    )
  })

  it("logout llama a /api/socios/logout", async () => {
    const fetchMock = vi.fn().mockResolvedValue(
      new Response(null, {
        status: 204,
      })
    )
    vi.stubGlobal("fetch", fetchMock)

    await logout()

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/socios/logout",
      expect.objectContaining({ method: "POST" })
    )
  })
})

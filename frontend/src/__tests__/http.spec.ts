import { describe, expect, it, vi } from "vitest"
import { apiGet, apiPost } from "@/services/http"

// Mock fetch to cover JSON/text error parsing and session cookies.

describe("apiPost", () => {
  it("devuelve JSON cuando la respuesta es ok", async () => {
    const payload = { ok: true }
    const fetchMock = vi.fn().mockResolvedValue(
      new Response(JSON.stringify(payload), {
        status: 200,
        headers: { "Content-Type": "application/json" },
      })
    )
    vi.stubGlobal("fetch", fetchMock)

    await expect(apiPost("/api/test", { foo: "bar" })).resolves.toEqual(payload)
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/test",
      expect.objectContaining({ credentials: "include" })
    )
  })

  it("lanza error con mensaje JSON si la respuesta es error", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response(JSON.stringify({ message: "Ese correo ya esta registrado." }), {
          status: 409,
          headers: { "Content-Type": "application/json" },
        })
      )
    )

    await expect(apiPost("/api/test", { foo: "bar" })).rejects.toThrow(
      "HTTP 409: Ese correo ya esta registrado."
    )
  })

  it("lanza error con texto si la respuesta no es JSON", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response("Error en servidor", {
          status: 500,
          headers: { "Content-Type": "text/plain" },
        })
      )
    )

    await expect(apiPost("/api/test", { foo: "bar" })).rejects.toThrow("HTTP 500: Error en servidor")
  })

  it("devuelve undefined si el endpoint responde 204", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response(null, {
          status: 204,
        })
      )
    )

    await expect(apiPost("/api/test", { foo: "bar" })).resolves.toBeUndefined()
  })

  it("devuelve undefined si el content-type no es JSON", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response("", {
          status: 200,
          headers: { "Content-Type": "text/plain" },
        })
      )
    )

    await expect(apiPost("/api/test", { foo: "bar" })).resolves.toBeUndefined()
  })
})

describe("apiGet", () => {
  it("lanza error con mensaje JSON si la respuesta es error", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response(JSON.stringify({ message: "No encontrado" }), {
          status: 404,
          headers: { "Content-Type": "application/json" },
        })
      )
    )

    await expect(apiGet("/api/test")).rejects.toThrow("HTTP 404: No encontrado")
  })

  it("incluye credentials para cookies de sesion", async () => {
    const payload = { ok: true }
    const fetchMock = vi.fn().mockResolvedValue(
      new Response(JSON.stringify(payload), {
        status: 200,
        headers: { "Content-Type": "application/json" },
      })
    )
    vi.stubGlobal("fetch", fetchMock)

    await expect(apiGet("/api/test")).resolves.toEqual(payload)
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/test",
      expect.objectContaining({ credentials: "include" })
    )
  })
})

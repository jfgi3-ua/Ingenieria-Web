import { describe, expect, it, vi } from "vitest"
import { apiGet, apiPost } from "@/services/http"

// Mock fetch to cover JSON and text error parsing.

describe("apiPost", () => {
  it("devuelve JSON cuando la respuesta es ok", async () => {
    const payload = { ok: true }
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response(JSON.stringify(payload), {
          status: 200,
          headers: { "Content-Type": "application/json" },
        })
      )
    )

    await expect(apiPost("/api/test", { foo: "bar" })).resolves.toEqual(payload)
  })

  it("lanza error con mensaje JSON si la respuesta es error", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response(JSON.stringify({ message: "Ese correo ya estÃ¡ registrado." }), {
          status: 409,
          headers: { "Content-Type": "application/json" },
        })
      )
    )

    await expect(apiPost("/api/test", { foo: "bar" })).rejects.toThrow("HTTP 409: Ese correo ya estÃ¡ registrado.")
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
})


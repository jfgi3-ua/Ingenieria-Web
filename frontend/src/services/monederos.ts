import { apiPost } from "./http"
import type { MonederoRecargaRequest, MonederoRecargaResponse, MonederoVerifyResponse } from "@/types/monedero"

export function recargarMonedero(payload: MonederoRecargaRequest) {
  return apiPost<MonederoRecargaResponse, MonederoRecargaRequest>("/api/monedero/recarga", payload)
}

export function verifyRecarga(token: string) {
  return apiPost<MonederoVerifyResponse, Record<string, never>>(
    `/api/monedero/verify/${encodeURIComponent(token)}`,
    {}
  )
}

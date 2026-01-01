import { apiPost } from "./http"

export type PagoInitRequest = {
  idTarifa: number
}

export type PagoInitResponse = {
  paymentUrl: string
  token: string
}

export type PagoVerifyResponse = {
  status: "PENDING" | "COMPLETED" | "FAILED"
  failureReason?: string | null
}

/**
 * Inicia un pago de registro en el backend y devuelve la URL de TPVV.
 */
export function initPago(payload: PagoInitRequest) {
  return apiPost<PagoInitResponse, PagoInitRequest>("/api/pagos/init", payload)
}

/**
 * Verifica el estado del pago en el backend (server-to-server).
 */
export function verifyPago(token: string) {
  return apiPost<PagoVerifyResponse, Record<string, never>>(`/api/pagos/verify/${token}`, {})
}

import { apiPost } from "./http"

export type PagoInitRequest = {
  idTarifa: number
  idSocio?: number
  idActividad?: number
}

export type PagoInitResponse = {
  paymentUrl: string
  token: string
}

export type PagoVerifyResponse = {
  status: "PENDING" | "COMPLETED" | "FAILED"
  failureReason?: string | null
}

export type ReservaPagada = {
  //precio: number
  idSocio: number
  idActividad: number
}

/**
 * Inicia un pago de registro en el backend y devuelve la URL de TPVV.
 */
export function initPago(payload: PagoInitRequest) {
  return apiPost<PagoInitResponse, PagoInitRequest>("/api/pagos/init", payload)
}

/**
 * Hace el pago. Baja el dinero de la cuenta del socio
 */
export function initPagoClase(payload: ReservaPagada) {
  console.log("Llega al pago correcto")
  return apiPost<boolean, ReservaPagada>("/api/reservas", payload)
}

/**
 * Verifica el estado del pago en el backend (server-to-server).
 */
export function verifyPago(token: string) {
  return apiPost<PagoVerifyResponse, Record<string, never>>(`/api/pagos/verify/${token}`, {})
}

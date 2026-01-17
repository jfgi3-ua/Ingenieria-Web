import { apiGet, apiPost } from "./http"

export type ReservaItem = {
  idActividad: number
  actividadNombre: string
  fecha: string
  horaIni: string
  horaFin: string
  estado: string
  precioPagado: number | string
}

export function getMisReservas(limit = 5) {
  return apiGet<ReservaItem[]>(`/api/reservas/me?limit=${limit}`)
}

export function cancelarReserva(idActividad: number) {
  return apiPost<ReservaCancelResponse, Record<string, never>>(
    `/api/reservas/me/${idActividad}/cancel`,
    {}
  )
}

export type ReservaCancelResponse = {
  estado: string
  reembolso: number | string
  saldoMonedero: number | string
}

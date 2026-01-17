import { apiGet } from "./http"

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

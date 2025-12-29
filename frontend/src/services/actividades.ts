import { apiGet } from "./http"

export type Actividad = {
    id: number
    nombre: string
    horaIni: string
    horaFin: string
    precioExtra: number
    fecha: string
    plazas: number
    monitor: string
}

export function listarActividades() {
  return apiGet<Actividad[]>("/api/actividades/servicios")
}
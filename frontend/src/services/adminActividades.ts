import { apiGet, apiPost, apiPut } from "./http"
import type { AdminActividad, AdminActividadRequest } from "@/types/adminActividad"

export function adminListarActividades() {
  return apiGet<AdminActividad[]>("/api/admin/actividades")
}

export function adminCrearActividad(payload: AdminActividadRequest) {
  return apiPost<AdminActividad, AdminActividadRequest>("/api/admin/actividades", payload)
}

export function adminEditarActividad(id: number, payload: AdminActividadRequest) {
  return apiPut<AdminActividad, AdminActividadRequest>(`/api/admin/actividades/${id}`, payload)
}

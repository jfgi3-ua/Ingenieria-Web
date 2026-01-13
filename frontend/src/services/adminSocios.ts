import { apiGet, apiPut, apiPatch } from "./http"
import type { AdminSocio, AdminSocioUpdateRequest, AdminSocioEstadoRequest } from "@/types/adminSocio"

export function adminListarSocios() {
  return apiGet<AdminSocio[]>("/api/admin/socios")
}

export function adminGetSocio(id: number) {
  return apiGet<AdminSocio>(`/api/admin/socios/${id}`)
}

export function adminActualizarSocio(id: number, payload: AdminSocioUpdateRequest) {
  return apiPut<AdminSocio, AdminSocioUpdateRequest>(`/api/admin/socios/${id}`, payload)
}

export function adminCambiarEstadoSocio(id: number, payload: AdminSocioEstadoRequest) {
  return apiPatch<AdminSocio, AdminSocioEstadoRequest>(`/api/admin/socios/${id}/estado`, payload)
}

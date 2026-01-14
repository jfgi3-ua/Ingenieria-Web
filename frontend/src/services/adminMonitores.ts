import { apiGet, apiPost, apiPut } from "./http"
import type { AdminMonitor, AdminMonitorRequest } from "@/types/adminMonitor"

export function adminListarMonitores() {
  return apiGet<AdminMonitor[]>("/api/admin/monitores")
}

export function adminCrearMonitor(payload: AdminMonitorRequest) {
  return apiPost<AdminMonitor, AdminMonitorRequest>("/api/admin/monitores", payload)
}

export function adminEditarMonitor(id: number, payload: AdminMonitorRequest) {
  return apiPut<AdminMonitor, AdminMonitorRequest>(`/api/admin/monitores/${id}`, payload)
}

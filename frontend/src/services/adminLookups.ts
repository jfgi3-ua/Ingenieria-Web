import { apiGet } from "./http"
import type { IdNombre, SalaLookup } from "@/types/adminLookups"

export function adminListarMonitores() {
  return apiGet<IdNombre[]>("/api/admin/lookups/monitores")
}

export function adminListarSalas() {
  return apiGet<SalaLookup[]>("/api/admin/lookups/salas")
}

export function adminListarTiposActividad() {
  return apiGet<IdNombre[]>("/api/admin/lookups/tipos-actividad")
}

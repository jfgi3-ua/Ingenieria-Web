import { apiGet } from "./http"
import type { AdminSocio } from "@/types/socio"

export function listarSociosAdmin() {
  return apiGet<AdminSocio[]>("/api/admin/socios")
}

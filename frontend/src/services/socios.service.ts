import { apiPost } from "./http"
import type { SocioRegistroRequest, SocioResponse } from "@/types/socio"


/**
 * Registro público de un socio (alta).
 * Backend esperado: POST /api/socios
 */
export function registrarSocio(payload: SocioRegistroRequest) {
  console.log("Registrando socio con payload:", payload)
  return apiPost<SocioResponse, SocioRegistroRequest>("/api/socios/registro", payload)
}

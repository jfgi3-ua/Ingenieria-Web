import { apiGet, apiPost } from "./http"
import type { SocioLoginRequest, SocioRegistroRequest, SocioResponse } from "@/types/socio"


/**
 * Registro público de un socio (alta).
 * Backend esperado: POST /api/socios
 */
export function registrarSocio(payload: SocioRegistroRequest) {
  console.log("Registrando socio con payload:", payload)
  return apiPost<SocioResponse, SocioRegistroRequest>("/api/socios/registro", payload)
}

/**
 * Inicia sesion con correo y contraseña.
 * Backend esperado: POST /api/socios/login
 *
 * Devuelve SocioResponse (mismo contrato que login/me).
 */
export function login(payload: SocioLoginRequest) {
  return apiPost<SocioResponse, SocioLoginRequest>("/api/socios/login", payload)
}

/**
 * Recupera la sesion actual si existe.
 * Backend esperado: GET /api/socios/me
 *
 * Devuelve SocioResponse si hay sesión.
 */
export function me() {
  return apiGet<SocioResponse>("/api/socios/me")
}

/**
 * Cierra la sesion actual.
 * Backend esperado: POST /api/socios/logout
 *
 * Se envía body vacío con apiPost para mantener la firma genérica.
 */
export function logout() {
  return apiPost<void, Record<string, never>>("/api/socios/logout", {})
}

import { apiGet, apiPost, apiPut } from "./http"
import type { SocioLoginRequest, SocioRegistroRequest, SocioResponse, SocioUpdateRequest } from "@/types/socio"
import type { MembresiaResponse } from '@/types/membresia.ts'


/**
 * Registro público de un socio (alta).
 * Backend esperado: POST /api/socios
 */
export function registrarSocio(payload: SocioRegistroRequest) {
  return apiPost<SocioResponse, SocioRegistroRequest>("/api/socios/registro", payload)
}

/**
 * Valida si un correo ya existe antes de avanzar en el registro.
 */
export async function emailExists(email: string) {
  const encoded = encodeURIComponent(email)
  const res = await apiGet<{ exists: boolean }>(`/api/socios/email-exists?email=${encoded}`)
  return res.exists
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

export function updateMe(payload: SocioUpdateRequest) {
  return apiPut<SocioResponse, SocioUpdateRequest>("/api/socios/me", payload)
}

export function meMembresia() {
  return apiGet<MembresiaResponse>("/api/socios/me/membresia")
}

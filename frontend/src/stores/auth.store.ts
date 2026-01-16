import { defineStore } from "pinia"
import { login as loginService, logout as logoutService, me as meService, updateMe as updateMeService,
  getPreferencias as getPreferenciasService, updatePreferencias as updatePreferenciasService, } from "@/services/socios"
import type { SocioLoginRequest, SocioResponse, SocioUpdateRequest } from "@/types/socio"
import type { PreferenciasResponse, PreferenciasUpdateRequest } from "@/types/preferencias"
export const ADMIN_EMAIL = "admin@gmail.com"

/**
 * Store de autenticacion.
 *
 * Centraliza el estado de sesion para evitar duplicar logica en las vistas.
 * Aqui guardamos el socio autenticado y una bandera simple de autenticacion.
 */
export const useAuthStore = defineStore("auth", {
  state: () => ({
    // Socio autenticado (null si no hay sesion).
    socio: null as SocioResponse | null,
    // Bandera simple para proteger rutas y condicionar la UI.
    isAuthenticated: false,
    preferencias: null as PreferenciasResponse | null,
  }),
    getters: {
    isAdmin: (state) => state.socio?.correoElectronico?.toLowerCase() === ADMIN_EMAIL,
  },
  actions: {
    /**
     * Inicia sesion con email y contrasena.
     *
     * Llama al backend, y si responde OK, guarda el socio y marca isAuthenticated.
     */
    async login(payload: SocioLoginRequest) {
      const socio = await loginService(payload)
      this.socio = socio
      this.isAuthenticated = true
      return socio
    },
    /**
     * Cierra la sesion en backend y limpia el estado local.
     */
    async logout() {
      await logoutService()
      this.socio = null
      this.isAuthenticated = false
    },
    /**
     * Intenta restaurar sesion al recargar la pagina.
     *
     * Si el backend devuelve 401, dejamos el estado en "no autenticado".
     * No lanzamos error para que el flujo de carga siga normal.
     */
    async loadSession() {
      try {
        const socio = await meService()
        this.socio = socio
        this.isAuthenticated = true
        return socio
      } catch (error) {
        // Log controlado para depurar sesiones perdidas sin romper el flujo.
        const message = error instanceof Error ? error.message : String(error)
        console.warn("[auth] sesion no restaurada:", message)
        this.socio = null
        this.isAuthenticated = false
        return null
      }
    },

    async updateMe(payload: SocioUpdateRequest) {
      const socio = await updateMeService(payload)
      this.socio = socio
      this.isAuthenticated = true
      return socio
    },

    async loadPreferencias() {
      const prefs = await getPreferenciasService()
      this.preferencias = prefs
      return prefs
    },

    async updatePreferencias(payload: PreferenciasUpdateRequest) {
      const prefs = await updatePreferenciasService(payload)
      this.preferencias = prefs
      return prefs
    },
  },
})

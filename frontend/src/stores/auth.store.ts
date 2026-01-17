import { defineStore } from "pinia"
import {
  login as loginService,
  logout as logoutService,
  me as meService,
  cambiarContrasena as cambiarContrasenaService,
  deleteMe as deleteMeService,
  updateMe as updateMeService,
} from "@/services/socios"
import type { SocioLoginRequest, SocioResponse, SocioUpdateRequest } from "@/types/socio"
import {
  recargarMonedero as recargarMonederoService,
  verifyRecarga as verifyRecargaMonederoService,
} from "@/services/monederos"

export const ADMIN_EMAIL = "admin@gmail.com"

export const useAuthStore = defineStore("auth", {
  state: () => ({
    socio: null as SocioResponse | null,
    isAuthenticated: false,
  }),
  getters: {
    isAdmin: (state) => state.socio?.correoElectronico?.toLowerCase() === ADMIN_EMAIL,
  },
  actions: {
    async login(payload: SocioLoginRequest) {
      const socio = await loginService(payload)
      this.socio = socio
      this.isAuthenticated = true
      return socio
    },

    async logout() {
      await logoutService()
      this.socio = null
      this.isAuthenticated = false
    },

    async loadSession() {
      try {
        const socio = await meService()
        this.socio = socio
        this.isAuthenticated = true
        return socio
      } catch (error) {
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

    async recargarMonedero(importe: number) {
      return recargarMonederoService({ importe })
    },

    async verificarRecargaMonedero(token: string) {
      const res = await verifyRecargaMonederoService(token)
      await this.loadSession()
      return res
    },

    async cambiarContrasena(contrasenaActual: string, nuevaContrasena: string) {
      await cambiarContrasenaService({
        contrasenaActual,
        nuevaContrasena,
      })

      // backend invalidates session -> clear local store too
      this.socio = null
      this.isAuthenticated = false
    },

    async deleteMe() {
      await deleteMeService()
      this.socio = null
      this.isAuthenticated = false
    },
  },
})

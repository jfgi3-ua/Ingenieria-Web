import { apiGet } from "./http"

export type Tarifa = {
  id: number
  nombre: string
  cuota: number
  descripcion?: string | null
  clasesGratisMes: number
}

export function listarTarifas() {
  return apiGet<Tarifa[]>("/api/tarifas")
}

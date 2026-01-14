export type AdminSocio = {
  id: number
  nombre: string
  correoElectronico: string
  telefono?: string | null
  estado: "ACTIVO" | "INACTIVO" | string
  idTarifa: number
  tarifaNombre: string
  pagoDomiciliado?: boolean | null
  saldoMonedero: number
  clasesGratis: number
  direccion?: string | null
  ciudad?: string | null
  codigoPostal?: string | null
}

export type AdminSocioUpdateRequest = {
  nombre: string
  correoElectronico: string
  telefono?: string | null
  idTarifa: number
  direccion?: string | null
  ciudad?: string | null
  codigoPostal?: string | null
  pagoDomiciliado?: boolean | null
}

export type AdminSocioEstadoRequest = {
  estado: "ACTIVO" | "INACTIVO"
}

export type SocioRegistroRequest = {
  nombre: string
  correoElectronico: string
  contrasena: string
  telefono?: string
  idTarifa: number
  direccion?: string
  ciudad?: string
  codigoPostal?: string
}

export type SocioResponse = {
  id: number
  nombre: string
  correoElectronico: string
  estado: string
  idTarifa: number
  tarifaNombre: string
}

export type AdminSocio = {
  id: number
  nombre: string
  correoElectronico: string
  telefono: string | null
  estado: string
  tarifaNombre: string
  pagoDomiciliado: boolean
  saldoMonedero: number
}

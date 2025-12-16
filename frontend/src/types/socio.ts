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

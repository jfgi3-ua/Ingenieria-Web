export type SocioRegistroRequest = {
  nombre: string
  correoElectronico: string
  contrasena: string
  telefono?: string
  idTarifa: number
  direccion?: string
  ciudad?: string
  codigoPostal?: string
  paymentToken: string
}

// Payload enviado al backend para iniciar sesion.
export type SocioLoginRequest = {
  correoElectronico: string
  contrasena: string
}

// Datos devueltos al iniciar sesion o recuperar sesion.
export type SocioResponse = {
  id: number
  nombre: string
  correoElectronico: string
  estado: string
  idTarifa: number
  tarifaNombre: string
  saldoMonedero: number | string
  telefono?: string | null
  direccion?: string | null
  ciudad?: string | null
  codigoPostal?: string | null
}

export type SocioUpdateRequest = {
  nombre: string
  telefono?: string | null
  direccion: string
  ciudad: string
  codigoPostal: string
}

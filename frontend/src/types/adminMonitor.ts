export type AdminMonitor = {
  id: number
  nombre: string
  dni: string
  correoElectronico: string
  telefono: string
  ciudad: string
  direccion: string
  codigoPostal: string
}

export type AdminMonitorRequest = {
  nombre: string
  dni: string
  correoElectronico: string
  contrasena: string
  telefono: string
  ciudad: string
  direccion: string
  codigoPostal: string
}

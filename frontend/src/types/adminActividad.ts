export type AdminActividad = {
  id: number
  nombre: string
  horaIni: string
  horaFin: string
  precioExtra: number
  fecha: string
  plazas: number
  disponibles: number

  idMonitor: number
  monitorNombre: string

  idSala: number
  salaDescripcion: string
  salaFoto?: string | null

  idTipoActividad: number
  tipoActividadNombre: string
}

export type AdminActividadRequest = {
  nombre: string
  horaIni: string
  horaFin: string
  precioExtra: number
  fecha: string
  plazas: number
  idMonitor: number
  idSala: number
  idTipoActividad: number
}

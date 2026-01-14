export type Actividad = {
    id: number
    nombre: string
    horaIni: string
    horaFin: string
    precioExtra: number
    fecha: string
    plazas: number
    disponibles: number
    monitor: string

    sala?: string
    salaFoto?: string | null
    tipoActividad?: string
}

export type Reserva = {
    idActividad: number
    idSocio: number
}
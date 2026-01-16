export type MembresiaResponse = {
  idTarifa: number
  tarifaNombre: string
  precioMensual: number
  estadoPago: "AL_DIA" | "PENDIENTE" | "IMPAGO" | "SIN_DATOS" | string
  fechaInicio: string | null
  proximaRenovacion: string | null
  ultimoPago: string | null
}

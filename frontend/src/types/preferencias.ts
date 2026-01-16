// src/types/preferencias.ts

export type PreferenciasResponse = {
  notificaciones: boolean
  recordatorios: boolean
  comunicaciones: boolean
}

export type PreferenciasUpdateRequest = {
  notificaciones: boolean
  recordatorios: boolean
  comunicaciones: boolean
}

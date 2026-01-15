import type { Actividad, Reserva } from "@/types/actividad"
import { apiGet, apiPost } from "./http"

export function listarActividades() {
    return apiGet<Actividad[]>("/api/actividades/servicios")
}

export function reservar(payload: Reserva) {
    return apiPost<boolean, Reserva>("/api/reservas", payload);
}
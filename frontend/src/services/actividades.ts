import type { Actividad } from "@/types/actividad"
import { apiGet } from "./http"



export function listarActividades() {
    return apiGet<Actividad[]>("/api/actividades/servicios")
}
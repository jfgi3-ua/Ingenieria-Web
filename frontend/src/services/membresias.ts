import { apiGet } from "./http"
import type { MembresiaResponse } from "@/types/membresia"

export function getMembresiaMe() {
  return apiGet<MembresiaResponse>("/api/socios/me/membresia")
}

/**
 * Realiza una petición GET a la API y parsea la respuesta como JSON.
 */
async function readErrorDetail(res: Response): Promise<string> {
  const contentType = res.headers.get("content-type") ?? ""
  if (contentType.includes("application/json")) {
    try {
      const data = (await res.json()) as { message?: string; error?: string }
      return data.message ?? data.error ?? "Error de servidor"
    } catch {
      return "Error de servidor"
    }
  }

  const text = await res.text().catch(() => "")
  return text || "Error de servidor"
}

export async function apiGet<T>(path: string): Promise<T> {
  const res = await fetch(path, {
    method: "GET",
    headers: { "Accept": "application/json" },
    // Necesario para enviar/recibir cookies de sesion (HttpSession) en el backend.
    // Sin esto, el navegador no adjunta la cookie y la sesion no se mantiene.
    credentials: "include",
  })

  if (!res.ok) {
    const detail = await readErrorDetail(res)
    throw new Error(`HTTP ${res.status}: ${detail}`)
  }

  return res.json() as Promise<T>
}

/**
 * Realiza una petición POST con JSON y parsea la respuesta como JSON.
 */
export async function apiPost<T, B>(path: string, body: B): Promise<T> {
  const res = await fetch(path, {
    method: "POST",
    headers: { Accept: "application/json", "Content-Type": "application/json" },
    // Necesario para enviar/recibir cookies de sesion (HttpSession) en el backend.
    // Sin esto, el navegador no adjunta la cookie y la sesion no se mantiene.
    credentials: "include",
    body: JSON.stringify(body),
  })

  if (!res.ok) {
    const detail = await readErrorDetail(res)
    throw new Error(`HTTP ${res.status}: ${detail}`)
  }

  // Algunos endpoints (como logout) responden 204 No Content.
  // En ese caso no hay JSON que parsear y res.json() lanzaria un error.
  if (res.status === 204) {
    return undefined as T
  }

  const contentType = res.headers.get("content-type") ?? ""
  if (!contentType.includes("application/json")) {
    return undefined as T
  }

  return res.json() as Promise<T>
}

//////////////

/**
 * Realiza una petición PUT con JSON y parsea la respuesta como JSON.
 */
export async function apiPut<T, B>(path: string, body: B): Promise<T> {
  const res = await fetch(path, {
    method: "PUT",
    headers: { Accept: "application/json", "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(body),
  })

  if (!res.ok) {
    const detail = await readErrorDetail(res)
    throw new Error(`HTTP ${res.status}: ${detail}`)
  }

  if (res.status === 204) {
    return undefined as T
  }

  const contentType = res.headers.get("content-type") ?? ""
  if (!contentType.includes("application/json")) {
    return undefined as T
  }

  return res.json() as Promise<T>
}

/**
 * Realiza una petición PATCH con JSON y parsea la respuesta como JSON.
 */
export async function apiPatch<T, B>(path: string, body: B): Promise<T> {
  const res = await fetch(path, {
    method: "PATCH",
    headers: { Accept: "application/json", "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(body),
  })

  if (!res.ok) {
    const detail = await readErrorDetail(res)
    throw new Error(`HTTP ${res.status}: ${detail}`)
  }

  if (res.status === 204) {
    return undefined as T
  }

  const contentType = res.headers.get("content-type") ?? ""
  if (!contentType.includes("application/json")) {
    return undefined as T
  }

  return res.json() as Promise<T>
}


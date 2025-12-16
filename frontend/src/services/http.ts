/**
 * Realiza una petición GET a la API y parsea la respuesta como JSON.
 */
export async function apiGet<T>(path: string): Promise<T> {
  const res = await fetch(path, {
    method: "GET",
    headers: { "Accept": "application/json" },
  })

  if (!res.ok) {
    const text = await res.text().catch(() => "")
    throw new Error(`GET ${path} failed: ${res.status} ${text}`)
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
    body: JSON.stringify(body),
  })

  if (!res.ok) {
    const text = await res.text().catch(() => "")
    throw new Error(`POST ${path} failed: ${res.status} ${text}`)
  }

  return res.json() as Promise<T>
}

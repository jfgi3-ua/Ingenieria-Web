export type MonederoRecargaRequest = {
  importe: number
}

export type MonederoRecargaResponse = {
  paymentUrl: string
  token: string
}

export type MonederoVerifyResponse = {
  estado: "PENDING" | "COMPLETED" | "FAILED" | string
  failureReason?: string | null
}

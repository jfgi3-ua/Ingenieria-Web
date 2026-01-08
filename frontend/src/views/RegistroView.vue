<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import { listarTarifas, type Tarifa } from "@/services/tarifas"
import { initPago, verifyPago } from "@/services/pagos"
import { emailExists, registrarSocio } from "@/services/socios"
import { type SocioRegistroRequest, type SocioResponse } from "@/types/socio"

const route = useRoute()
const router = useRouter()
const REGISTRO_STORAGE_KEY = "fitgym_registro_draft"

const tarifas = ref<Tarifa[]>([])
const loadingTarifas = ref(false)
const tarifasError = ref<string | null>(null)

const loadingSubmit = ref(false)
const error = ref<string | null>(null)
const ok = ref<SocioResponse | null>(null)
const checkingEmail = ref(false)

const step = ref<1 | 2 | 3>(1)
const stepErrors = ref<string[]>([])
const fieldErrors = ref<Record<string, string>>({})
const termsAccepted = ref(false)
const pagoEstado = ref<"PENDING" | "COMPLETED" | "FAILED" | null>(null)
const pagoError = ref<string | null>(null)
const loadingPago = ref(false)
const loadingVerify = ref(false)

const showPassword = ref(false)
const showConfirmPassword = ref(false)
const confirmPassword = ref("")

const steps = [
  { id: 1, label: "Datos personales" },
  { id: 2, label: "Tarifa y pago" },
  { id: 3, label: "Confirmación" },
]

/**
 * Estado local del formulario de registro.
 * (No lo subimos a store porque solo lo usa esta vista DE MOMENTO)
 */
const form = ref<SocioRegistroRequest>({
  nombre: "",
  correoElectronico: "",
  contrasena: "",
  telefono: "",
  direccion: "",
  ciudad: "",
  codigoPostal: "",
  idTarifa: 0,
  paymentToken: "",
})

const tarifaSeleccionada = computed(() => tarifas.value.find((t) => t.id === form.value.idTarifa) ?? null)
const tarifaPopularId = computed(() => tarifas.value.find((t) => t.nombre.toLowerCase().includes("premium"))?.id ?? null)

const pagoConfirmado = computed(() => pagoEstado.value === "COMPLETED")
const canContinueStep2 = computed(
  () => Boolean(tarifaSeleccionada.value) && pagoConfirmado.value && !loadingVerify.value
)
const canSubmit = computed(() => termsAccepted.value && !loadingSubmit.value)

onMounted(async () => {
  restoreDraft()
  loadingTarifas.value = true
  tarifasError.value = null
  try {
    tarifas.value = await listarTarifas()
    if (tarifas.value.length > 0 && form.value.idTarifa === 0) {
      form.value.idTarifa = tarifas.value[0]?.id ?? 0
    }
  } catch (e) {
    tarifasError.value = e instanceof Error ? e.message : String(e)
  } finally {
    loadingTarifas.value = false
  }
  await aplicarEstadoDesdeRoute()
})

watch(
  () => form.value.idTarifa,
  () => {
    pagoEstado.value = null
    pagoError.value = null
    form.value.paymentToken = ""
  }
)

function featuresForTarifa(tarifa: Tarifa) {
  const features: string[] = ["Acceso al gimnasio", "Zona de pesas y cardio"]
  if ((tarifa.clasesGratisMes ?? 0) > 0) {
    const unlimited = tarifa.clasesGratisMes >= 999 || tarifa.nombre.toLowerCase().includes("elite")
    features.push(`Clases gratis/mes: ${unlimited ? "ilimitado" : tarifa.clasesGratisMes}`)
  }
  if (tarifa.nombre.toLowerCase().includes("premium")) {
    features.push("Acceso 24/7", "Casillero personal")
  }
  if (tarifa.nombre.toLowerCase().includes("elite")) {
    features.push("Entrenamiento personal", "Plan nutricional personalizado")
  }
  return features
}

function applyStepFromRoute() {
  const stepParam = route.query.step
  if (stepParam === "2") {
    step.value = 2
  }
}

function readTokenFromRoute(): string | null {
  const token = route.query.token
  if (typeof token === "string" && token.trim().length > 0) {
    return token
  }
  return null
}

async function aplicarEstadoDesdeRoute() {
  applyStepFromRoute()
  const token = readTokenFromRoute()
  if (!token) return

  if (step.value !== 2) step.value = 2
  await verificarPagoDesdeBackend(token)
}

function normalizeErrorMessage(msg: string) {
  const lower = msg.toLowerCase()
  if (lower.includes("pago")) return "El pago no está confirmado. Vuelve al paso de pago y verifícalo."
  if (lower.includes("token")) return "No se pudo validar el pago. Inicia el pago de nuevo."
  if (msg.includes("409")) return "Ese correo ya está registrado."
  if (msg.includes("404")) return "La tarifa seleccionada no existe."
  if (msg.includes("400")) return "Revisa los campos del formulario."
  return msg
}

function normalizePagoError(msg: string) {
  if (msg.includes("502")) return "No se pudo conectar con la pasarela de pago."
  if (msg.includes("404")) return "No se encontró la transacción de pago."
  if (msg.includes("409")) return "El pago aún no está completado."
  return msg
}

function setFieldError(field: string, message: string) {
  fieldErrors.value = { ...fieldErrors.value, [field]: message }
}

function clearFieldError(field: string) {
  const { [field]: _removed, ...rest } = fieldErrors.value
  fieldErrors.value = rest
}

function clearFieldErrors() {
  fieldErrors.value = {}
}

async function validarCorreoBlur() {
  const correo = form.value.correoElectronico.trim()
  if (!correo) return
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo)) return

  clearFieldError("correoElectronico")
  checkingEmail.value = true
  try {
    const exists = await emailExists(correo)
    if (exists) {
      stepErrors.value = ["Correo ya registrado."]
      setFieldError("correoElectronico", "Correo ya registrado.")
    }
  } catch {
    stepErrors.value = ["No se pudo validar el correo. Intenta de nuevo."]
    setFieldError("correoElectronico", "No se pudo validar el correo. Intenta de nuevo.")
  } finally {
    checkingEmail.value = false
  }
}

function validarPaso1(): boolean {
  const errs: string[] = []
  clearFieldErrors()
  if (!form.value.nombre.trim()) errs.push("El nombre es obligatorio.")
  if (!form.value.correoElectronico.trim()) errs.push("El correo electrónico es obligatorio.")
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.correoElectronico)) {
    errs.push("El correo electrónico no es válido.")
  }
  if (!form.value.contrasena.trim()) errs.push("La contraseña es obligatoria.")
  if (form.value.contrasena && form.value.contrasena.length < 8) {
    errs.push("La contraseña debe tener al menos 8 caracteres.")
  }
  if (!confirmPassword.value.trim()) errs.push("Debes confirmar la contraseña.")
  if (confirmPassword.value !== form.value.contrasena) errs.push("Las contraseñas no coinciden.")
  if (!(form.value.direccion ?? "").trim()) errs.push("La dirección es obligatoria.")
  if (!(form.value.ciudad ?? "").trim()) errs.push("La ciudad es obligatoria.")
  if (!(form.value.codigoPostal ?? "").trim()) errs.push("El código postal es obligatorio.")

  if (!form.value.nombre.trim()) setFieldError("nombre", "El nombre es obligatorio.")
  if (!form.value.correoElectronico.trim()) {
    setFieldError("correoElectronico", "El correo electrónico es obligatorio.")
  }
  if (
    form.value.correoElectronico.trim() &&
    !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.correoElectronico)
  ) {
    setFieldError("correoElectronico", "El correo electrónico no es válido.")
  }
  if (!form.value.contrasena.trim()) setFieldError("contrasena", "La contraseña es obligatoria.")
  if (form.value.contrasena && form.value.contrasena.length < 8) {
    setFieldError("contrasena", "La contraseña debe tener al menos 8 caracteres.")
  }
  if (!confirmPassword.value.trim()) setFieldError("confirmPassword", "Debes confirmar la contraseña.")
  if (confirmPassword.value && confirmPassword.value !== form.value.contrasena) {
    setFieldError("confirmPassword", "Las contraseñas no coinciden.")
  }
  if (!(form.value.direccion ?? "").trim()) setFieldError("direccion", "La dirección es obligatoria.")
  if (!(form.value.ciudad ?? "").trim()) setFieldError("ciudad", "La ciudad es obligatoria.")
  if (!(form.value.codigoPostal ?? "").trim()) setFieldError("codigoPostal", "El código postal es obligatorio.")
  stepErrors.value = errs
  return errs.length === 0
}

async function validarPaso1Async(): Promise<boolean> {
  if (!validarPaso1()) return false

  checkingEmail.value = true
  try {
    const exists = await emailExists(form.value.correoElectronico)
    if (exists) {
      stepErrors.value = ["Ese correo ya esta registrado."]
      setFieldError("correoElectronico", "Ese correo ya esta registrado.")
      return false
    }
  } catch {
    stepErrors.value = ["No se pudo validar el correo. Intentelo de nuevo."]
    setFieldError("correoElectronico", "No se pudo validar el correo. Intentelo de nuevo.")
    return false
  } finally {
    checkingEmail.value = false
  }

  return true
}

function validarPaso2(): boolean {
  const errs: string[] = []
  if (!tarifaSeleccionada.value) errs.push("Debes seleccionar una tarifa.")
  if (!pagoConfirmado.value) errs.push("Debes completar la verificación de pago.")
  stepErrors.value = errs
  return errs.length === 0
}

function validarPaso3(): boolean {
  const errs: string[] = []
  if (!termsAccepted.value) errs.push("Debes aceptar los términos y condiciones.")
  stepErrors.value = errs
  return errs.length === 0
}

async function irSiguiente() {
  stepErrors.value = []
  if (step.value === 1 && !(await validarPaso1Async())) return
  if (step.value === 2 && !validarPaso2()) return
  if (step.value < 3) step.value = (step.value + 1) as 2 | 3
}

function irAnterior() {
  stepErrors.value = []
  if (step.value > 1) step.value = (step.value - 1) as 1 | 2
}

function persistDraft() {
  const snapshot = {
    form: form.value,
    confirmPassword: confirmPassword.value,
    step: step.value,
  }
  sessionStorage.setItem(REGISTRO_STORAGE_KEY, JSON.stringify(snapshot))
}

function restoreDraft() {
  const raw = sessionStorage.getItem(REGISTRO_STORAGE_KEY)
  if (!raw) return
  try {
    const data = JSON.parse(raw) as {
      form?: SocioRegistroRequest
      confirmPassword?: string
      step?: 1 | 2 | 3
    }
    if (data.form) {
      form.value = { ...form.value, ...data.form }
    }
    if (typeof data.confirmPassword === "string") {
      confirmPassword.value = data.confirmPassword
    }
    if (data.step) {
      step.value = data.step
    }
  } catch {
    sessionStorage.removeItem(REGISTRO_STORAGE_KEY)
  }
}

function clearDraft() {
  sessionStorage.removeItem(REGISTRO_STORAGE_KEY)
}

async function iniciarPago() {
  if (!tarifaSeleccionada.value) {
    stepErrors.value = ["Selecciona una tarifa antes de continuar con el pago."]
    return
  }
  loadingPago.value = true
  pagoError.value = null
  try {
    persistDraft()
    const res = await initPago({ idTarifa: form.value.idTarifa })
    window.location.assign(res.paymentUrl)
  } catch (e) {
    const message = e instanceof Error ? e.message : String(e)
    pagoError.value = normalizePagoError(message)
  } finally {
    loadingPago.value = false
  }
}

async function verificarPagoDesdeBackend(token: string) {
  loadingVerify.value = true
  pagoError.value = null
  try {
    const res = await verifyPago(token)
    pagoEstado.value = res.status
    if (res.status === "COMPLETED") {
      form.value.paymentToken = token
    } else {
      form.value.paymentToken = ""
      if (res.status === "FAILED") {
        pagoError.value = res.failureReason ?? "El pago no se completo."
      }
    }
  } catch (e) {
    const message = e instanceof Error ? e.message : String(e)
    pagoError.value = normalizePagoError(message)
  } finally {
    loadingVerify.value = false
  }
}

async function onSubmit() {
  ok.value = null
  error.value = null

  if (!validarPaso3()) return

  loadingSubmit.value = true
  try {
    ok.value = await registrarSocio({ ...form.value })
    clearDraft()
    await router.push("/login")
  } catch (e) {
    const message = e instanceof Error ? e.message : String(e)
    error.value = normalizeErrorMessage(message)
  } finally {
    loadingSubmit.value = false
  }
}
</script>

<template>
  <section class="registro">
    <div class="registro__inner">
      <header class="registro__header">
        <h1>Registro</h1>
        <p>Crea tu cuenta en FitGym y completa el proceso en 3 pasos.</p>
        <p class="registro__privacy">Usaremos tus datos solo para gestionar tu cuenta y reservas.</p>
      </header>

      <div class="stepper">
        <div
          v-for="(s, idx) in steps"
          :key="s.id"
          class="stepper__item"
          :class="{
            'stepper__item--active': step === s.id,
            'stepper__item--done': step > s.id,
          }"
        >
          <span class="stepper__index">
            <span v-if="step > s.id">✓</span>
            <span v-else>{{ s.id }}</span>
          </span>
          <span class="stepper__label">{{ s.label }}</span>
          <span v-if="idx < steps.length - 1" class="stepper__line"></span>
        </div>
      </div>

      <div class="card" :class="{ 'card--narrow': step === 3 }">
        <div v-if="stepErrors.length" class="alert alert--warning">
          <strong>Revisa lo siguiente:</strong>
          <ul>
            <li v-for="(msg, idx) in stepErrors" :key="idx">{{ msg }}</li>
          </ul>
        </div>

        <div v-if="error" class="alert alert--error">
          <strong>Ha ocurrido un error:</strong>
          <p>{{ error }}</p>
        </div>

        <div v-if="ok" class="alert alert--success">
          <strong>Registro enviado correctamente.</strong>
          <p>Tu solicitud ha sido registrada con ID {{ ok.id }}.</p>
        </div>

        <form v-if="step === 1" class="panel" @submit.prevent="irSiguiente">
          <div class="panel__heading">
            <h2>Datos personales</h2>
            <p>Crea tu cuenta en FitGym para comenzar tu transformación.</p>
            <p class="form-legend"><span class="req">*</span> Obligatorio</p>
          </div>

          <fieldset class="form-group">
            <legend>Datos personales</legend>
            <div class="form-grid">
              <label class="field" :class="{ 'field--error': fieldErrors.nombre }">
                <span>Nombre completo <span class="req">*</span></span>
                <input
                  v-model="form.nombre"
                  type="text"
                  placeholder="Ej: Juan Pérez Garcia"
                  :aria-invalid="Boolean(fieldErrors.nombre)"
                />
                <small v-if="fieldErrors.nombre" class="field__error">{{ fieldErrors.nombre }}</small>
              </label>

              <label class="field" :class="{ 'field--error': fieldErrors.correoElectronico }">
                <span>Correo electrónico <span class="req">*</span></span>
                <input
                  v-model="form.correoElectronico"
                  type="email"
                  placeholder="tu.email@ejemplo.com"
                  @blur="validarCorreoBlur"
                  :aria-invalid="Boolean(fieldErrors.correoElectronico)"
                />
                <small v-if="fieldErrors.correoElectronico" class="field__error">{{ fieldErrors.correoElectronico }}</small>
                <small v-if="checkingEmail">Comprobando...</small>
                <small>Usaremos este correo para enviarte confirmaciones y notificaciones.</small>
              </label>

              <label class="field" :class="{ 'field--error': fieldErrors.contrasena }">
                <span>Contraseña <span class="req">*</span></span>
                <div class="field__input">
                  <input
                    v-model="form.contrasena"
                    :type="showPassword ? 'text' : 'password'"
                    placeholder="Mínimo 8 caracteres"
                    :aria-invalid="Boolean(fieldErrors.contrasena)"
                  />
                  <button type="button" class="icon-btn" @click="showPassword = !showPassword">
                    {{ showPassword ? "Ocultar contraseña" : "Ver contraseña" }}
                  </button>
                </div>
                <small v-if="fieldErrors.contrasena" class="field__error">{{ fieldErrors.contrasena }}</small>
                <small>Debe contener al menos 8 caracteres.</small>
              </label>

              <label class="field" :class="{ 'field--error': fieldErrors.confirmPassword }">
                <span>Confirmar contraseña <span class="req">*</span></span>
                <div class="field__input">
                  <input
                    v-model="confirmPassword"
                    :type="showConfirmPassword ? 'text' : 'password'"
                    placeholder="Repite tu contraseña"
                    :aria-invalid="Boolean(fieldErrors.confirmPassword)"
                  />
                  <button
                    type="button"
                    class="icon-btn"
                    @click="showConfirmPassword = !showConfirmPassword"
                  >
                    {{ showConfirmPassword ? "Ocultar confirmación" : "Ver confirmación" }}
                  </button>
                </div>
                <small v-if="fieldErrors.confirmPassword" class="field__error">{{ fieldErrors.confirmPassword }}</small>
              </label>

              <label class="field">
                <span>Teléfono</span>
                <input v-model="form.telefono" type="tel" placeholder="+34 600 00 00 00" />
                <small>Opcional: para notificaciones y recordatorios por SMS.</small>
              </label>
            </div>
          </fieldset>

          <fieldset class="form-group">
            <legend>Dirección</legend>
            <div class="form-grid">
              <label class="field" :class="{ 'field--error': fieldErrors.direccion }">
                <span>Dirección <span class="req">*</span></span>
                <input
                  v-model="form.direccion"
                  type="text"
                  placeholder="Calle, número, piso, puerta"
                  :aria-invalid="Boolean(fieldErrors.direccion)"
                />
                <small v-if="fieldErrors.direccion" class="field__error">{{ fieldErrors.direccion }}</small>
              </label>

              <div class="field field--split">
                <label :class="{ 'field--error': fieldErrors.ciudad }">
                  <span>Ciudad <span class="req">*</span></span>
                  <input v-model="form.ciudad" type="text" placeholder="Ej: Madrid" :aria-invalid="Boolean(fieldErrors.ciudad)" />
                  <small v-if="fieldErrors.ciudad" class="field__error">{{ fieldErrors.ciudad }}</small>
                </label>
                <label :class="{ 'field--error': fieldErrors.codigoPostal }">
                  <span>Código postal <span class="req">*</span></span>
                  <input
                    v-model="form.codigoPostal"
                    type="text"
                    placeholder="28001"
                    :aria-invalid="Boolean(fieldErrors.codigoPostal)"
                  />
                  <small v-if="fieldErrors.codigoPostal" class="field__error">{{ fieldErrors.codigoPostal }}</small>
                </label>
              </div>
            </div>
          </fieldset>

          <div class="panel__footer">
            <RouterLink class="hint hint--accent" to="/login">¿Ya tienes cuenta? Inicia sesión</RouterLink>
            <button type="submit" class="btn btn--primary" :disabled="checkingEmail">
              {{ checkingEmail ? "Comprobando..." : "Siguiente" }}
            </button>
          </div>
        </form>

        <div v-else-if="step === 2" class="panel">
          <div class="panel__heading">
            <h2>Selección de tarifa</h2>
            <p>Elige el plan que mejor se adapte a tus objetivos.</p>
          </div>

          <p v-if="loadingTarifas" class="loading">Cargando tarifas...</p>
          <p v-else-if="tarifasError" class="error-text">{{ tarifasError }}</p>

          <div v-else class="tarifas-grid">
            <label
              v-for="tarifa in tarifas"
              :key="tarifa.id"
              class="tarifa-card"
              :class="{ active: form.idTarifa === tarifa.id }"
            >
              <input v-model.number="form.idTarifa" type="radio" name="tarifa" :value="tarifa.id" />
              <span class="tarifa-card__check" aria-hidden="true"></span>
              <div class="tarifa-card__header">
                <span class="tarifa-card__title">{{ tarifa.nombre }}</span>
                <span v-if="tarifaPopularId === tarifa.id" class="badge">Popular</span>
              </div>
              <p class="tarifa-card__desc">{{ tarifa.descripcion ?? "Plan flexible para tu estilo de vida." }}</p>
              <div class="tarifa-card__price">
                <span class="tarifa-card__amount">€{{ tarifa.cuota }}</span>
                <span class="tarifa-card__period">/mes</span>
              </div>
              <ul class="tarifa-card__features">
                <li v-for="item in featuresForTarifa(tarifa)" :key="item">{{ item }}</li>
              </ul>
            </label>
          </div>

          <div class="pay-box">
            <div class="pay-box__header">
              <span>Pago seguro</span>
              <small>El pago se gestiona mediante una pasarela externa segura. Tus datos están protegidos.</small>
            </div>

            <div class="pay-box__summary">
              <div class="pay-box__row">
                <span>Plan seleccionado</span>
                <strong>{{ tarifaSeleccionada?.nombre ?? "—" }}</strong>
              </div>
              <div class="pay-box__row">
                <span>Precio mensual</span>
                <strong>€{{ tarifaSeleccionada?.cuota ?? "-" }}</strong>
              </div>
              <div class="pay-box__row">
                <span>Total primer mes</span>
                <strong>€{{ tarifaSeleccionada?.cuota ?? "-" }}</strong>
              </div>
            </div>

            <button
              class="btn btn--primary btn--full"
              type="button"
              :disabled="loadingPago || loadingVerify || pagoConfirmado"
              @click="iniciarPago"
            >
              {{ loadingPago ? "Redirigiendo..." : "Continuar al pago seguro" }}
            </button>

            <div v-if="loadingVerify" class="pay-box__status pay-box__status--pending">
              <strong>Verificando pago con TPVV...</strong>
              <span>Estamos comprobando el estado de la transacción.</span>
            </div>
            <div v-else-if="pagoConfirmado" class="pay-box__status pay-box__status--ok">
              <strong>Pago procesado correctamente</strong>
              <span>Tu membresía está lista para activarse.</span>
            </div>
            <div v-else-if="pagoEstado === 'FAILED'" class="pay-box__status pay-box__status--error">
              <strong>Pago fallido</strong>
              <span>Puedes reintentar el proceso desde este paso.</span>
            </div>
            <div v-else-if="pagoEstado === 'PENDING'" class="pay-box__status pay-box__status--pending">
              <strong>Pago pendiente</strong>
              <span>Revisa el estado en unos segundos.</span>
            </div>
            <p v-if="pagoError" class="pay-box__error-text">{{ pagoError }}</p>
            <small class="muted">Conexión segura SSL 256-bit</small>
          </div>

          <div class="panel__footer">
            <button type="button" class="btn btn--ghost" @click="irAnterior">Anterior</button>
            <button type="button" class="btn btn--primary" :disabled="!canContinueStep2" @click="irSiguiente">
              Continuar
            </button>
          </div>
        </div>

        <div v-else class="panel panel--confirm">
          <div class="panel__heading">
            <h2>Confirmación</h2>
            <p>Revisa tu información antes de finalizar el registro.</p>
          </div>

          <div class="confirm-grid">
            <section class="confirm-card">
              <h3>Datos personales</h3>
              <div class="confirm-row">
                <span>Nombre:</span>
                <strong>{{ form.nombre }}</strong>
              </div>
              <div class="confirm-row">
                <span>Email:</span>
                <strong>{{ form.correoElectronico }}</strong>
              </div>
              <div class="confirm-row" v-if="form.telefono">
                <span>Teléfono:</span>
                <strong>{{ form.telefono }}</strong>
              </div>
            </section>

            <section class="confirm-card confirm-card--accent">
              <div class="confirm-card__header">
                <h3>Tarifa seleccionada</h3>
                <span class="confirm-price">€{{ tarifaSeleccionada?.cuota ?? "-" }}/mes</span>
              </div>
              <p class="confirm-plan">{{ tarifaSeleccionada?.nombre ?? "—" }}</p>
              <p class="confirm-desc">{{ tarifaSeleccionada?.descripcion ?? "Plan seleccionado" }}</p>
              <p class="confirm-subtitle">Beneficios incluidos:</p>
              <ul class="confirm-list">
                <li v-for="item in (tarifaSeleccionada ? featuresForTarifa(tarifaSeleccionada) : [])" :key="item">
                  {{ item }}
                </li>
              </ul>
            </section>

            <section class="confirm-card confirm-card--ok">
              <h3>Estado del pago</h3>
              <div v-if="pagoConfirmado" class="confirm-status confirm-status--ok">
                <span class="confirm-status__icon" aria-hidden="true"></span>
                <div class="confirm-status__content">
                  <strong>Pago realizado</strong>
                  <span>Transacción completada con éxito.</span>
                </div>
              </div>
              <div v-else-if="pagoEstado === 'FAILED'" class="confirm-status confirm-status--error">
                <span class="confirm-status__icon" aria-hidden="true"></span>
                <div class="confirm-status__content">
                  <strong>Pago fallido</strong>
                  <span>Debes reintentar el proceso.</span>
                </div>
              </div>
              <div v-else class="confirm-status confirm-status--pending">
                <span class="confirm-status__icon" aria-hidden="true"></span>
                <div class="confirm-status__content">
                  <strong>Pago pendiente</strong>
                  <span>Verificación en curso.</span>
                </div>
              </div>
            </section>
          </div>

          <div class="terms-card">
            <label class="terms">
              <input v-model="termsAccepted" type="checkbox" />
              <span>
                Acepto los términos y condiciones y la política de privacidad.
                <small>Al registrarte, aceptas nuestros términos de servicio. Tus datos están protegidos.</small>
              </span>
            </label>
          </div>

          <div class="confirm-badges">
            <div class="confirm-badge">
              <span class="confirm-badge__icon" aria-hidden="true"></span>
              <div class="confirm-badge__text">
                <strong>Datos protegidos</strong>
                <span>Encriptación SSL</span>
              </div>
            </div>
            <div class="confirm-badge">
              <span class="confirm-badge__icon" aria-hidden="true"></span>
              <div class="confirm-badge__text">
                <strong>Pago seguro</strong>
                <span>Pasarela certificada</span>
              </div>
            </div>
            <div class="confirm-badge">
              <span class="confirm-badge__icon" aria-hidden="true"></span>
              <div class="confirm-badge__text">
                <strong>Acceso inmediato</strong>
                <span>Activa hoy mismo</span>
              </div>
            </div>
          </div>

          <div class="panel__footer">
            <button type="button" class="btn btn--ghost" @click="irAnterior">Anterior</button>
            <button type="button" class="btn btn--primary" :disabled="!canSubmit" @click="onSubmit">
              {{ loadingSubmit ? "Creando cuenta..." : "Crear cuenta y finalizar" }}
            </button>
          </div>
        </div>
      </div>

      <p class="support">
        ¿Necesitas ayuda? Contacta con nuestro equipo en <strong>soporte@fitgym.com</strong>
      </p>
    </div>
  </section>
</template>

<style scoped>
.registro {
  --brand: #0a8fb2;
  --brand-dark: #097a98;
  --bg: #f6f8fb;
  --card: #ffffff;
  --text: #1f2933;
  --muted: #66788a;
  --border: #e2e8f0;
  font-family: "Manrope", "Segoe UI", sans-serif;
  color: var(--text);
  background: linear-gradient(180deg, #f8fafc 0%, #eef3f7 100%);
  padding: 40px 16px 56px;
}

.registro__inner {
  max-width: 920px;
  margin: 0 auto;
}

.registro__header {
  text-align: center;
  margin-bottom: 24px;
}

.registro__header h1 {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 700;
}

.registro__header p {
  margin: 0;
  color: var(--muted);
}

.registro__privacy {
  margin-top: 8px;
  color: var(--muted);
  font-size: 13px;
}

.stepper {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.stepper__item {
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
  color: var(--muted);
}

.stepper__item--active {
  color: var(--text);
  font-weight: 600;
}

.stepper__item--done {
  color: var(--brand);
}

.stepper__index {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  border: 1px solid var(--border);
  display: grid;
  place-items: center;
  font-weight: 600;
  background: #fff;
}

.stepper__item--active .stepper__index,
.stepper__item--done .stepper__index {
  border-color: var(--brand);
  background: var(--brand);
  color: #fff;
}

.stepper__line {
  width: 80px;
  height: 2px;
  background: var(--border);
}

.stepper__item--done .stepper__line {
  background: var(--brand);
}

.card {
  background: var(--card);
  border-radius: 14px;
  box-shadow: 0 16px 30px rgba(15, 23, 42, 0.08);
  padding: 24px;
}

.card--narrow {
  max-width: 720px;
  margin: 0 auto;
}

.panel__heading h2 {
  margin: 0 0 6px;
  font-size: 18px;
}

.panel__heading p {
  margin: 0 0 16px;
  color: var(--muted);
}

.form-legend {
  margin: 6px 0 0;
  color: var(--muted);
  font-size: 12px;
}

.form-group {
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 12px;
  margin: 0 0 16px;
}

.form-group legend {
  padding: 0 6px;
  font-size: 13px;
  font-weight: 600;
}

.form-grid {
  display: grid;
  gap: 16px;
  min-width: 0;
}

.field span {
  font-weight: 600;
  font-size: 13px;
}

.field {
  min-width: 0;
}

.field input {
  width: 100%;
  margin-top: 6px;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid var(--border);
  font-size: 14px;
  box-sizing: border-box;
  min-width: 0;
}

.field--error input,
.field--error .field__input input {
  border-color: #ef4444;
  box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.15);
}

.field__error {
  display: block;
  color: #b91c1c;
  margin-top: 6px;
  font-size: 12px;
}

.field small {
  display: block;
  color: var(--muted);
  margin-top: 6px;
  font-size: 12px;
}

.field--split {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 16px;
}

.field--split label {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.field--split input {
  width: 100%;
  box-sizing: border-box;
}

.field__input {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
  min-width: 0;
}

.field__input input {
  flex: 1;
  margin-top: 0;
  min-width: 0;
}

.icon-btn {
  border: 1px solid var(--border);
  background: #fff;
  border-radius: 6px;
  padding: 6px 10px;
  cursor: pointer;
  font-size: 12px;
  white-space: nowrap;
}

.req {
  color: #e11d48;
}

.panel__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20px;
  gap: 12px;
  flex-wrap: wrap;
}

.hint {
  all: unset;
  color: var(--muted);
  font-size: 13px;
  cursor: pointer;
}

.hint--accent {
  color: var(--brand);
  font-weight: 600;
  text-decoration: underline;
}

.btn {
  border-radius: 8px;
  padding: 10px 16px;
  border: 1px solid transparent;
  cursor: pointer;
  font-weight: 600;
}

.btn--primary {
  background: var(--brand);
  color: #fff;
  border-color: var(--brand);
}

.btn--primary:disabled {
  background: #cbd5e1;
  border-color: #cbd5e1;
  cursor: not-allowed;
}

.btn--ghost {
  border-color: var(--border);
  background: #fff;
  color: var(--text);
}

.btn--full {
  width: 100%;
}

.alert {
  border-radius: 10px;
  padding: 12px 14px;
  margin-bottom: 16px;
}

.alert ul {
  margin: 8px 0 0;
  padding-left: 18px;
}

.alert--warning {
  background: #fff7ed;
  border: 1px solid #fed7aa;
}

.alert--error {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #991b1b;
}

.alert--success {
  background: #ecfdf5;
  border: 1px solid #bbf7d0;
  color: #065f46;
}

.tarifas-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.tarifa-card {
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 16px;
  display: grid;
  gap: 10px;
  cursor: pointer;
  position: relative;
  background: #fff;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.tarifa-card input {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}

.tarifa-card__check {
  width: 18px;
  height: 18px;
  border: 1px solid var(--border);
  border-radius: 999px;
  display: grid;
  place-items: center;
  font-size: 12px;
  color: transparent;
  background: #fff;
}

.tarifa-card.active {
  border-color: var(--brand);
  box-shadow: 0 14px 30px rgba(10, 143, 178, 0.18);
  background: linear-gradient(180deg, #f0fbff 0%, #ffffff 100%);
  transform: translateY(-2px);
}

.tarifa-card.active .tarifa-card__check {
  background: var(--brand);
  border-color: var(--brand);
  color: #fff;
}

.tarifa-card.active .tarifa-card__check::after {
  content: "\2713";
}

.tarifa-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tarifa-card__title {
  font-weight: 700;
}

.badge {
  background: var(--brand);
  color: #fff;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 999px;
  box-shadow: 0 6px 12px rgba(10, 143, 178, 0.25);
}

.tarifa-card__desc {
  color: var(--muted);
  font-size: 13px;
}

.tarifa-card__price {
  display: flex;
  align-items: baseline;
  gap: 4px;
  font-weight: 700;
}

.tarifa-card__amount {
  font-size: 22px;
}

.tarifa-card__period {
  font-size: 12px;
  color: var(--muted);
}

.tarifa-card__features {
  margin: 0;
  padding-left: 0;
  list-style: none;
  color: var(--muted);
  font-size: 13px;
  display: grid;
  gap: 6px;
}

.tarifa-card__features li::before {
  content: "\2713";
  color: var(--brand);
  margin-right: 6px;
}

.pay-box {
  margin-top: 20px;
  border-radius: 12px;
  border: 1px solid var(--border);
  padding: 18px;
  background: linear-gradient(180deg, #f8fbff 0%, #f5f9ff 100%);
  display: grid;
  gap: 12px;
}

.pay-box__header span {
  font-weight: 700;
  display: block;
}

.pay-box__header small {
  color: var(--muted);
}

.pay-box__summary {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 12px;
  display: grid;
  gap: 6px;
  box-shadow: inset 0 1px 0 rgba(148, 163, 184, 0.15);
}

.pay-box__row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.pay-box__status {
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 13px;
  display: grid;
  gap: 4px;
  border-left: 4px solid transparent;
}

.pay-box__status--ok {
  background: #ecfdf5;
  border: 1px solid #bbf7d0;
  color: #047857;
  border-left-color: #10b981;
}

.pay-box__status--pending {
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  color: #1d4ed8;
  border-left-color: #60a5fa;
}

.pay-box__status--error {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #b91c1c;
  border-left-color: #f87171;
}

.pay-box__error-text {
  margin: 0;
  color: #b91c1c;
  font-size: 12px;
}

.muted {
  color: var(--muted);
  font-size: 12px;
}

.confirm-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: 1fr;
  max-width: 640px;
  margin: 0 auto;
}

.confirm-card {
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 10px 14px 14px;
  background: #fff;
  display: grid;
  gap: 8px;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
}

.confirm-card h3 {
  margin: 0 0 4px;
}

.confirm-card--accent {
  background: #eefcff;
  border-color: #b6e7f2;
}

.confirm-card--ok {
  background: #ecfdf5;
  border-color: #bbf7d0;
}

.confirm-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.confirm-price {
  font-weight: 700;
  color: var(--brand-dark);
  background: rgba(10, 143, 178, 0.08);
  padding: 4px 10px;
  border-radius: 999px;
}

.confirm-plan {
  font-weight: 600;
  margin: 0;
}

.confirm-desc {
  color: var(--muted);
  font-size: 13px;
  margin: 0;
}

.confirm-subtitle {
  font-size: 12px;
  color: var(--muted);
  margin: 6px 0 0;
}

.confirm-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.confirm-list {
  list-style: none;
  padding-left: 0;
  margin: 0;
  color: var(--muted);
  font-size: 13px;
  display: grid;
  gap: 6px;
}

.confirm-list li::before {
  content: "\2713";
  color: var(--brand);
  margin-right: 6px;
}

.confirm-status {
  display: flex;
  gap: 10px;
  font-size: 13px;
  padding: 10px 12px;
  border-radius: 10px;
  align-items: center;
  background: rgba(255, 255, 255, 0.8);
}

.confirm-status--ok {
  color: #047857;
  border: 1px solid #bbf7d0;
}

.confirm-status--pending {
  color: #1d4ed8;
  border: 1px solid #bfdbfe;
}

.confirm-status--error {
  color: #b91c1c;
  border: 1px solid #fecaca;
}

.confirm-status__icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: grid;
  place-items: center;
  background: currentColor;
  color: #fff;
  font-size: 14px;
  flex: 0 0 28px;
}

.confirm-status--ok .confirm-status__icon {
  background: #16a34a;
}

.confirm-status--pending .confirm-status__icon {
  background: #60a5fa;
}

.confirm-status--error .confirm-status__icon {
  background: #f87171;
}

.confirm-status__icon::before {
  content: "\2713";
}

.confirm-status--pending .confirm-status__icon::before {
  content: "...";
}

.confirm-status--error .confirm-status__icon::before {
  content: "!";
}

.confirm-status__content {
  display: grid;
  gap: 2px;
}

.terms-card {
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 12px;
  background: #f8fafc;
  margin: 14px auto 0;
  max-width: 640px;
  box-shadow: inset 0 1px 0 rgba(148, 163, 184, 0.12);
}

.terms {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  margin: 0;
  font-size: 13px;
  color: var(--muted);
}

.terms span {
  display: grid;
  gap: 4px;
}

.terms small {
  color: var(--muted);
  font-size: 12px;
}

.confirm-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 20px;
  margin: 12px auto 0;
  font-size: 12px;
  color: var(--muted);
  max-width: 640px;
  justify-content: center;
  text-align: center;
}

.confirm-badge {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.confirm-badge__icon {
  width: 18px;
  height: 18px;
  border-radius: 999px;
  border: 1px solid var(--brand);
  display: grid;
  place-items: center;
  color: var(--brand);
  font-size: 11px;
}

.confirm-badge__icon::before {
  content: "\2713";
}

.confirm-badge__text {
  display: grid;
  gap: 2px;
}

.confirm-badge__text strong {
  color: var(--text);
  font-weight: 600;
  font-size: 12px;
}

.confirm-badge__text span {
  font-size: 11px;
}

.panel--confirm .panel__heading {
  text-align: left;
}

.panel--confirm .panel__heading p {
  max-width: 520px;
  margin: 0 0 16px;
}

.panel--confirm .panel__footer {
  justify-content: space-between;
  gap: 16px;
}

.support {
  margin: 24px 0 0;
  text-align: center;
  color: var(--muted);
  font-size: 13px;
}

.loading {
  color: var(--muted);
}

.error-text {
  color: #b91c1c;
}

@media (max-width: 720px) {
  .stepper__line {
    display: none;
  }

  .panel__footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .field--split {
    grid-template-columns: 1fr;
  }
}
</style>

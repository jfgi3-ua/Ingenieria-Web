<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue"
import { listarTarifas, type Tarifa } from "@/services/tarifas"
import { registrarSocio } from "@/services/socios"
import { type SocioRegistroRequest, type SocioResponse } from "@/types/socio"

const tarifas = ref<Tarifa[]>([])
const loadingTarifas = ref(false)
const tarifasError = ref<string | null>(null)

const loadingSubmit = ref(false)
const error = ref<string | null>(null)
const ok = ref<SocioResponse | null>(null)

const step = ref<1 | 2 | 3>(1)
const stepErrors = ref<string[]>([])
const termsAccepted = ref(false)
const pagoConfirmado = ref(false)

const showPassword = ref(false)
const showConfirmPassword = ref(false)
const confirmPassword = ref("")

const steps = [
  { id: 1, label: "Datos personales" },
  { id: 2, label: "Tarifas y pago" },
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
})

const tarifaSeleccionada = computed(() => tarifas.value.find((t) => t.id === form.value.idTarifa) ?? null)
const tarifaPopularId = computed(() => tarifas.value.find((t) => t.nombre.toLowerCase().includes("premium"))?.id ?? null)

const canContinueStep2 = computed(() => Boolean(tarifaSeleccionada.value) && pagoConfirmado.value)
const canSubmit = computed(() => termsAccepted.value && !loadingSubmit.value)

onMounted(async () => {
  loadingTarifas.value = true
  tarifasError.value = null
  try {
    tarifas.value = await listarTarifas()
    if (tarifas.value.length > 0) form.value.idTarifa = tarifas.value[0]?.id ?? 0
  } catch (e) {
    tarifasError.value = e instanceof Error ? e.message : String(e)
  } finally {
    loadingTarifas.value = false
  }
})

watch(
  () => form.value.idTarifa,
  () => {
    pagoConfirmado.value = false
  }
)

function featuresForTarifa(tarifa: Tarifa) {
  const features: string[] = ["Acceso al gimnasio", "Zona de pesas y cardio"]
  if ((tarifa.clasesGratisMes ?? 0) > 0) {
    features.push(`Clases gratis/mes: ${tarifa.clasesGratisMes}`)
  }
  if (tarifa.nombre.toLowerCase().includes("premium")) {
    features.push("Acceso 24/7", "Casillero personal")
  }
  if (tarifa.nombre.toLowerCase().includes("elite")) {
    features.push("Entrenamiento personal", "Plan nutricional personalizado")
  }
  return features
}

function normalizeErrorMessage(msg: string) {
  if (msg.includes("409")) return "Ese correo ya está registrado."
  if (msg.includes("404")) return "La tarifa seleccionada no existe."
  if (msg.includes("400")) return "Revisa los campos del formulario."
  return msg
}

function validarPaso1(): boolean {
  const errs: string[] = []
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

  stepErrors.value = errs
  return errs.length === 0
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

function irSiguiente() {
  stepErrors.value = []
  if (step.value === 1 && !validarPaso1()) return
  if (step.value === 2 && !validarPaso2()) return
  if (step.value < 3) step.value = (step.value + 1) as 2 | 3
}

function irAnterior() {
  stepErrors.value = []
  if (step.value > 1) step.value = (step.value - 1) as 1 | 2
}

function confirmarPago() {
  if (!tarifaSeleccionada.value) {
    stepErrors.value = ["Selecciona una tarifa antes de continuar con el pago."]
    return
  }
  pagoConfirmado.value = true
  stepErrors.value = []
}

async function onSubmit() {
  ok.value = null
  error.value = null

  if (!validarPaso3()) return

  loadingSubmit.value = true
  try {
    ok.value = await registrarSocio({ ...form.value })
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

      <div class="card">
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
          </div>

          <div class="form-grid">
            <label class="field">
              <span>Nombre completo <span class="req">*</span></span>
              <input v-model="form.nombre" type="text" placeholder="Ej: Juan Pérez García" />
            </label>

            <label class="field">
              <span>Correo electrónico <span class="req">*</span></span>
              <input v-model="form.correoElectronico" type="email" placeholder="tu.email@ejemplo.com" />
              <small>Usaremos este correo para enviarte confirmaciones y notificaciones.</small>
            </label>

            <label class="field">
              <span>Contraseña <span class="req">*</span></span>
              <div class="field__input">
                <input
                  v-model="form.contrasena"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="Mínimo 8 caracteres"
                />
                <button type="button" class="icon-btn" @click="showPassword = !showPassword">
                  {{ showPassword ? "Ocultar contraseña" : "Ver contraseña" }}
                </button>
              </div>
              <small>Debe contener al menos 8 caracteres.</small>
            </label>

            <label class="field">
              <span>Confirmar contraseña <span class="req">*</span></span>
              <div class="field__input">
                <input
                  v-model="confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  placeholder="Repite tu contraseña"
                />
                <button
                  type="button"
                  class="icon-btn"
                  @click="showConfirmPassword = !showConfirmPassword"
                >
                  {{ showConfirmPassword ? "Ocultar confirmación" : "Ver confirmación" }}
                </button>
              </div>
            </label>

            <label class="field">
              <span>Teléfono</span>
              <input v-model="form.telefono" type="tel" placeholder="+34 600 00 00 00" />
              <small>Opcional: para notificaciones y recordatorios por SMS.</small>
            </label>

            <label class="field">
              <span>Dirección <span class="req">*</span></span>
              <input v-model="form.direccion" type="text" placeholder="Calle, número, piso, puerta" />
            </label>

            <div class="field field--split">
              <label>
                <span>Ciudad <span class="req">*</span></span>
                <input v-model="form.ciudad" type="text" placeholder="Ej: Madrid" />
              </label>
              <label>
                <span>Código postal <span class="req">*</span></span>
                <input v-model="form.codigoPostal" type="text" placeholder="28001" />
              </label>
            </div>
          </div>

          <div class="panel__footer">
            <span class="hint">¿Ya tienes cuenta? Inicia sesión</span>
            <button type="submit" class="btn btn--primary">Siguiente</button>
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
            <label v-for="tarifa in tarifas" :key="tarifa.id" class="tarifa-card" :class="{ active: form.idTarifa === tarifa.id }">
              <input v-model.number="form.idTarifa" type="radio" name="tarifa" :value="tarifa.id" />
              <div class="tarifa-card__header">
                <span class="tarifa-card__title">{{ tarifa.nombre }}</span>
                <span v-if="tarifaPopularId === tarifa.id" class="badge">Popular</span>
              </div>
              <p class="tarifa-card__desc">{{ tarifa.descripcion ?? "Plan flexible para tu estilo de vida." }}</p>
              <p class="tarifa-card__price">€{{ tarifa.cuota }}<span>/mes</span></p>
              <ul>
                <li v-for="item in featuresForTarifa(tarifa)" :key="item">✓ {{ item }}</li>
              </ul>
            </label>
          </div>

          <div class="pay-box">
            <div class="pay-box__header">
              <span>Pago seguro</span>
              <small>Pasarela externa segura. Datos protegidos y encriptados.</small>
            </div>

            <div class="pay-box__summary">
              <div>
                <strong>Plan seleccionado:</strong>
                <span>{{ tarifaSeleccionada?.nombre ?? "—" }}</span>
              </div>
              <div>
                <strong>Precio mensual:</strong>
                <span>€{{ tarifaSeleccionada?.cuota ?? "-" }}</span>
              </div>
              <div>
                <strong>Total primer mes:</strong>
                <span>€{{ tarifaSeleccionada?.cuota ?? "-" }}</span>
              </div>
            </div>

            <button class="btn btn--primary btn--full" type="button" @click="confirmarPago">
              Continuar al pago seguro
            </button>

            <div v-if="pagoConfirmado" class="pay-box__ok">
              ✓ Pago procesado correctamente. Tu membresía está lista para activarse.
            </div>
            <small class="muted">Conexión segura SSL 256-bit</small>
          </div>

          <div class="panel__footer">
            <button type="button" class="btn btn--ghost" @click="irAnterior">Anterior</button>
            <button type="button" class="btn btn--primary" :disabled="!canContinueStep2" @click="irSiguiente">
              Continuar
            </button>
          </div>
        </div>

        <div v-else class="panel">
          <div class="panel__heading">
            <h2>Confirmación</h2>
            <p>Revisa tu información antes de finalizar el registro.</p>
          </div>

          <div class="summary">
            <div class="summary__section">
              <h3>Datos personales</h3>
              <div class="summary__row">
                <span>Nombre:</span>
                <strong>{{ form.nombre }}</strong>
              </div>
              <div class="summary__row">
                <span>Email:</span>
                <strong>{{ form.correoElectronico }}</strong>
              </div>
              <div class="summary__row" v-if="form.telefono">
                <span>Teléfono:</span>
                <strong>{{ form.telefono }}</strong>
              </div>
            </div>

            <div class="summary__section summary__section--accent">
              <h3>Tarifa seleccionada</h3>
              <div class="summary__row">
                <span>{{ tarifaSeleccionada?.nombre ?? "—" }}</span>
                <strong>€{{ tarifaSeleccionada?.cuota ?? "-" }}/mes</strong>
              </div>
              <p class="summary__desc">{{ tarifaSeleccionada?.descripcion ?? "Plan seleccionado" }}</p>
              <ul>
                <li v-for="item in (tarifaSeleccionada ? featuresForTarifa(tarifaSeleccionada) : [])" :key="item">
                  ✓ {{ item }}
                </li>
              </ul>
            </div>

            <div class="summary__section summary__section--ok">
              <h3>Estado del pago</h3>
              <p>✓ Pago realizado. Transacción completada con éxito.</p>
            </div>
          </div>

          <label class="terms">
            <input v-model="termsAccepted" type="checkbox" />
            Acepto los términos y condiciones y la política de privacidad.
          </label>

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

.panel__heading h2 {
  margin: 0 0 6px;
  font-size: 18px;
}

.panel__heading p {
  margin: 0 0 16px;
  color: var(--muted);
}

.form-grid {
  display: grid;
  gap: 16px;
}

.field span {
  font-weight: 600;
  font-size: 13px;
}

.field input {
  width: 100%;
  margin-top: 6px;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid var(--border);
  font-size: 14px;
}

.field small {
  display: block;
  color: var(--muted);
  margin-top: 6px;
  font-size: 12px;
}

.field--split {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 16px;
}

.field__input {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}

.field__input input {
  flex: 1;
  margin-top: 0;
}

.icon-btn {
  border: 1px solid var(--border);
  background: #fff;
  border-radius: 6px;
  padding: 6px 10px;
  cursor: pointer;
  font-size: 12px;
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
  color: var(--muted);
  font-size: 13px;
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
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
}

.tarifa-card {
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 14px;
  display: grid;
  gap: 10px;
  cursor: pointer;
  position: relative;
  background: #fff;
}

.tarifa-card input {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}

.tarifa-card.active {
  border-color: var(--brand);
  box-shadow: 0 0 0 2px rgba(10, 143, 178, 0.15);
  background: #f0fbff;
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
}

.tarifa-card__desc {
  color: var(--muted);
  font-size: 13px;
}

.tarifa-card__price {
  font-size: 20px;
  font-weight: 700;
}

.tarifa-card__price span {
  font-size: 12px;
  color: var(--muted);
  margin-left: 2px;
}

.tarifa-card ul {
  margin: 0;
  padding-left: 0;
  list-style: none;
  color: var(--muted);
  font-size: 13px;
  display: grid;
  gap: 6px;
}

.pay-box {
  margin-top: 20px;
  border-radius: 12px;
  border: 1px solid var(--border);
  padding: 16px;
  background: #f8fbff;
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
}

.pay-box__summary div {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.pay-box__ok {
  background: #ecfdf5;
  border: 1px solid #bbf7d0;
  color: #047857;
  padding: 8px 10px;
  border-radius: 8px;
  font-size: 13px;
}

.muted {
  color: var(--muted);
  font-size: 12px;
}

.summary {
  display: grid;
  gap: 16px;
}

.summary__section {
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 14px;
  background: #fff;
}

.summary__section--accent {
  background: #eefcff;
  border-color: #b6e7f2;
}

.summary__section--ok {
  background: #ecfdf5;
  border-color: #bbf7d0;
}

.summary__row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-top: 6px;
}

.summary__desc {
  color: var(--muted);
  font-size: 13px;
  margin-top: 8px;
}

.summary ul {
  list-style: none;
  padding-left: 0;
  margin: 8px 0 0;
  color: var(--muted);
  font-size: 13px;
  display: grid;
  gap: 6px;
}

.terms {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  margin-top: 14px;
  font-size: 13px;
  color: var(--muted);
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
}
</style>

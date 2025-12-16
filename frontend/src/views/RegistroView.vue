<script setup lang="ts">
import { computed, onMounted, ref } from "vue"
import { listarTarifas, type Tarifa } from "@/services/tarifas"
import { registrarSocio } from "@/services/socios.service"
import { type SocioRegistroRequest, type SocioResponse } from "@/types/socio.ts"

const tarifas = ref<Tarifa[]>([])
const loadingTarifas = ref(false)

const loadingSubmit = ref(false)
const error = ref<string | null>(null)
const ok = ref<SocioResponse | null>(null)

/**
 * Estado local del formulario de registro.
 * (No lo subimos a store porque solo lo usa esta vista) :contentReference[oaicite:6]{index=6}
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

const formErrors = ref<string[]>([])

onMounted(async () => {
  loadingTarifas.value = true
  error.value = null
  try {
    tarifas.value = await listarTarifas()
    // Selección por defecto: primera tarifa si existe
    if (tarifas.value.length > 0) form.value.idTarifa = tarifas.value[0]?.id ?? 0
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  } finally {
    loadingTarifas.value = false
  }
})

const tarifaSeleccionada = computed(() => tarifas.value.find((t) => t.id === form.value.idTarifa) ?? null)

function validarFormulario(): boolean {
  const errs: string[] = []

  if (!form.value.nombre.trim()) errs.push("El nombre es obligatorio.")
  if (!form.value.correoElectronico.trim()) errs.push("El correo electrónico es obligatorio.")
  if (!form.value.contrasena.trim()) errs.push("La contraseña es obligatoria.")
  if (!(form.value.telefono ?? "").trim()) errs.push("El teléfono es obligatorio.")
  if (!(form.value.direccion ?? "").trim()) errs.push("La dirección es obligatoria.")
  if (!(form.value.ciudad ?? "").trim()) errs.push("La ciudad es obligatoria.")
  if (!(form.value.codigoPostal ?? "").trim()) errs.push("El código postal es obligatorio.")
  if (!form.value.idTarifa) errs.push("Debes seleccionar una tarifa.")

  // Validación mínima de formato (sin librerías, por ahora)
  if (form.value.contrasena && form.value.contrasena.length < 8) {
    errs.push("La contraseña debe tener al menos 8 caracteres.")
  }

  formErrors.value = errs
  return errs.length === 0
}

async function onSubmit() {
  ok.value = null
  error.value = null

  if (!validarFormulario()) return

  loadingSubmit.value = true
  try {
    ok.value = await registrarSocio({ ...form.value })
    // Si quieres, aquí podrías hacer router.push("/login") en el futuro.
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  } finally {
    loadingSubmit.value = false
  }
}
</script>

<template>
  <section style="max-width: 720px; margin: 0 auto; padding: 16px">
    <h1>Registro</h1>
    <p style="margin-top: 4px; color: #444">
      Completa tus datos y selecciona una tarifa para solicitar el alta.
    </p>

    <p v-if="error" style="color: red; margin-top: 12px">{{ error }}</p>

    <div v-if="ok" style="margin-top: 12px; padding: 12px; border: 1px solid #cfc; background: #f6fff6">
      <strong>Registro enviado correctamente.</strong>
      <div style="margin-top: 6px; font-size: 14px">
        <div><strong>ID:</strong> {{ ok.id }}</div>
        <div><strong>Nombre:</strong> {{ ok.nombre }}</div>
        <div><strong>Email:</strong> {{ ok.correoElectronico }}</div>
        <div><strong>Estado:</strong> {{ ok.estado }}</div>
      </div>
    </div>

    <form @submit.prevent="onSubmit" style="margin-top: 16px">
      <fieldset style="border: 1px solid #ddd; padding: 16px">
        <legend style="padding: 0 8px">Datos personales</legend>

        <div style="display: grid; gap: 12px">
          <label>
            Nombre
            <input v-model="form.nombre" type="text" autocomplete="name" style="display: block; width: 100%; padding: 8px" />
          </label>

          <label>
            Correo electrónico
            <input
              v-model="form.correoElectronico"
              type="email"
              autocomplete="email"
              style="display: block; width: 100%; padding: 8px"
            />
          </label>

          <label>
            Contraseña
            <input
              v-model="form.contrasena"
              type="password"
              minlength="8"
              autocomplete="new-password"
              style="display: block; width: 100%; padding: 8px"
            />
          </label>

          <label>
            Teléfono
            <input v-model="form.telefono" type="tel" autocomplete="tel" style="display: block; width: 100%; padding: 8px" />
          </label>

          <label>
            Dirección
            <input
              v-model="form.direccion"
              type="text"
              autocomplete="street-address"
              style="display: block; width: 100%; padding: 8px"
            />
          </label>

          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px">
            <label>
              Ciudad
              <input v-model="form.ciudad" type="text" autocomplete="address-level2" style="display: block; width: 100%; padding: 8px" />
            </label>

            <label>
              Código postal
              <input
                v-model="form.codigoPostal"
                type="text"
                autocomplete="postal-code"
                style="display: block; width: 100%; padding: 8px"
              />
            </label>
          </div>
        </div>
      </fieldset>

      <fieldset style="border: 1px solid #ddd; padding: 16px; margin-top: 12px">
        <legend style="padding: 0 8px">Tarifa</legend>

        <p v-if="loadingTarifas" style="margin: 0">Cargando tarifas…</p>

        <div v-else style="display: grid; gap: 10px">
          <label>
            Selecciona tarifa
            <select v-model.number="form.idTarifa" style="display: block; width: 100%; padding: 8px">
              <option v-for="t in tarifas" :key="t.id" :value="t.id">
                {{ t.nombre }} — {{ t.cuota }}€
              </option>
            </select>
          </label>

          <div v-if="tarifaSeleccionada" style="font-size: 14px; color: #444">
            <div><strong>Descripción:</strong> {{ tarifaSeleccionada.descripcion ?? "—" }}</div>
            <div><strong>Clases gratis/mes:</strong> {{ tarifaSeleccionada.clasesGratisMes }}</div>
          </div>

          <p style="font-size: 13px; color: #666; margin: 0">
            El pago por TPVV se integrará en un hito posterior. De momento enviamos la solicitud de alta.
          </p>
        </div>
      </fieldset>

      <div v-if="formErrors.length" style="margin-top: 12px; padding: 12px; border: 1px solid #f3c; background: #fff6fb">
        <strong>Revisa el formulario:</strong>
        <ul style="margin: 8px 0 0 18px">
          <li v-for="(msg, idx) in formErrors" :key="idx">{{ msg }}</li>
        </ul>
      </div>

      <div style="margin-top: 12px; display: flex; gap: 10px">
        <button type="submit" :disabled="loadingSubmit" style="padding: 10px 14px; cursor: pointer">
          {{ loadingSubmit ? "Enviando…" : "Enviar registro" }}
        </button>

        <button type="button" @click="() => (formErrors = [])" style="padding: 10px 14px; cursor: pointer">
          Limpiar errores
        </button>
      </div>
    </form>
  </section>
</template>

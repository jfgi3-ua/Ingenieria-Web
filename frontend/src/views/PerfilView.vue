<!--<script setup lang="ts">-->
<!--import { computed } from "vue"-->
<!--import { useAuthStore } from "@/stores/auth.store"-->

<!--const auth = useAuthStore()-->

<!--// placeholder hasta que se configure GET /api/perfil/me correctamente - siguiente historia-->
<!--const perfil = computed(() => {-->
<!--  const socio = auth.socio-->
<!--  if (!socio) return null-->

<!--  const idSocio = `FG-${String(socio.id).padStart(4, "0")}` // e.g. FG-0001-->
<!--  const membresiaActiva = socio.estado?.toUpperCase() === "ACTIVO"-->

<!--  return {-->
<!--    nombre: socio.nombre,-->
<!--    idSocio,-->
<!--    email: socio.correoElectronico,-->
<!--    telefono: socio.telefono?.trim() ? socio.telefono : "—",-->
<!--    direccion: socio.direccion?.trim() ? socio.direccion : "—",-->
<!--    ciudad: socio.ciudad ?? "—",-->
<!--    codigoPostal: socio.codigoPostal ?? "—",-->
<!--    membresiaActiva,-->
<!--    plan: socio.tarifaNombre ?? `Tarifa #${socio.idTarifa}`,-->
<!--    precioMensual: "—",-->
<!--    fechaInicio: "—",-->
<!--    proximaRenovacion: "—",-->
<!--    estadoPago: "—",-->
<!--    proximoCargo: "—",-->
<!--    monedero: "—",-->
<!--    preferencias: {-->
<!--      notificaciones: true,-->
<!--      recordatorios: true,-->
<!--      comunicaciones: false,-->
<!--    },-->
<!--    progreso: {-->
<!--      clasesCompletadas: 0,-->
<!--      horasEntrenamiento: 0,-->
<!--      caloriasQuemadas: 0,-->
<!--    },-->
<!--    reservas: [] as Array<{-->
<!--      nombre: string-->
<!--      instructor: string-->
<!--      fecha: string-->
<!--      hora: string-->
<!--      estado: "Confirmada" | "Completada" | "Cancelada"-->
<!--    }>,-->
<!--  }-->
<!--})-->
<!--</script>-->

<!--<template>-->
<!--  &lt;!&ndash; con sesión iniciada &ndash;&gt;-->
<!--  <div v-if="perfil" class="perfil-page">-->
<!--    &lt;!&ndash; Banner &ndash;&gt;-->
<!--    <section class="perfil-banner">-->
<!--      <div class="perfil-banner-inner">-->
<!--        <div class="avatar" aria-hidden="true"></div>-->

<!--        <div class="perfil-banner-info">-->
<!--          <div class="name-row">-->
<!--            <h2 class="name">{{ perfil.nombre }}</h2>-->
<!--            <span class="id">ID de Socio: {{ perfil.idSocio }}</span>-->
<!--          </div>-->

<!--          <div class="badges">-->
<!--            <span class="badge ok" v-if="perfil.membresiaActiva">Membresía Activa</span>-->
<!--            <span class="badge plan">{{ perfil.plan }}</span>-->
<!--          </div>-->
<!--        </div>-->

<!--        <button class="btn-outline" type="button">Editar perfil</button>-->
<!--      </div>-->
<!--    </section>-->

<!--    &lt;!&ndash; contenido &ndash;&gt;-->
<!--    <section class="perfil-content">-->
<!--      <div class="grid">-->
<!--        &lt;!&ndash; columna izquierda &ndash;&gt;-->
<!--        <div class="col-left">-->
<!--          <div class="card">-->
<!--            <div class="card-header">-->
<!--              <h3>Información Personal</h3>-->
<!--              <button class="link-btn" type="button">Editar</button>-->
<!--            </div>-->

<!--            <div class="info-grid">-->
<!--              <div class="info-item">-->
<!--                <span class="label">Nombre completo:</span>-->
<!--                <span class="value">{{ perfil.nombre }}</span>-->
<!--              </div>-->
<!--              <div class="info-item">-->
<!--                <span class="label">Correo electrónico:</span>-->
<!--                <span class="value">{{ perfil.email }}</span>-->
<!--              </div>-->
<!--              <div class="info-item">-->
<!--                <span class="label">Teléfono:</span>-->
<!--                <span class="value">{{ perfil.telefono }}</span>-->
<!--              </div>-->
<!--              <div class="info-item">-->
<!--                <span class="label">Dirección:</span>-->
<!--                <span class="value">{{ perfil.direccion }}</span>-->
<!--              </div>-->
<!--              <div class="info-item">-->
<!--                <span class="label">Ciudad:</span>-->
<!--                <span class="value">{{ perfil.ciudad }}</span>-->
<!--              </div>-->
<!--              <div class="info-item">-->
<!--                <span class="label">Código postal:</span>-->
<!--                <span class="value">{{ perfil.codigoPostal }}</span>-->
<!--              </div>-->
<!--            </div>-->
<!--          </div>-->

<!--          <div class="card">-->
<!--            <div class="card-header">-->
<!--              <h3>Membresía y Facturación</h3>-->
<!--            </div>-->

<!--            <div class="billing-box">-->
<!--              <div class="row">-->
<!--                <div>-->
<!--                  <span class="label">Plan Actual:</span>-->
<!--                  <div class="value">{{ perfil.plan }}</div>-->
<!--                </div>-->
<!--                <div class="right">-->
<!--                  <span class="label">Precio mensual:</span>-->
<!--                  <div class="value">{{ perfil.precioMensual }}</div>-->
<!--                </div>-->
<!--              </div>-->

<!--              <div class="row">-->
<!--                <div>-->
<!--                  <span class="label">Fecha de inicio:</span>-->
<!--                  <div class="value">{{ perfil.fechaInicio }}</div>-->
<!--                </div>-->
<!--                <div class="right">-->
<!--                  <span class="label">Próxima renovación:</span>-->
<!--                  <div class="value">{{ perfil.proximaRenovacion }}</div>-->
<!--                </div>-->
<!--              </div>-->
<!--            </div>-->

<!--            <div class="pill ok-pill">-->
<!--              <strong>Estado del pago</strong> — {{ perfil.estadoPago }} · Próximo cargo: {{ perfil.proximoCargo }}-->
<!--            </div>-->

<!--            <div class="pill wallet-pill">-->
<!--              <strong>Monedero FitGym</strong> — Saldo disponible: {{ perfil.monedero }}-->
<!--            </div>-->

<!--            <div class="actions-row">-->
<!--              <button class="btn-outline" type="button">Cambiar tarifa</button>-->
<!--              <button class="btn-outline" type="button">Ver facturas</button>-->
<!--            </div>-->
<!--          </div>-->

<!--          <div class="card">-->
<!--            <div class="card-header">-->
<!--              <h3>Actividades y Reservas</h3>-->
<!--              <button class="link-btn" type="button">Ver todas</button>-->
<!--            </div>-->

<!--            <div v-if="perfil.reservas.length" class="reserva-list">-->
<!--              <div v-for="(r, idx) in perfil.reservas" :key="idx" class="reserva-item">-->
<!--                <div class="reserva-main">-->
<!--                  <div class="reserva-title">{{ r.nombre }}</div>-->
<!--                  <div class="reserva-sub">Instructor: {{ r.instructor }}</div>-->
<!--                  <div class="reserva-sub">{{ r.fecha }} · {{ r.hora }}</div>-->
<!--                </div>-->
<!--                <span class="status" :class="r.estado === 'Completada' ? 'done' : 'ok'">{{ r.estado }}</span>-->
<!--              </div>-->
<!--            </div>-->

<!--            <p v-else class="empty-text">Aún no tienes reservas para mostrar.</p>-->

<!--            <button class="btn-primary full" type="button">Reservar nueva clase</button>-->
<!--          </div>-->
<!--        </div>-->

<!--        &lt;!&ndash; columna derecha &ndash;&gt;-->
<!--        <div class="col-right">-->
<!--          <div class="card">-->
<!--            <div class="card-header">-->
<!--              <h3>Preferencias</h3>-->
<!--            </div>-->

<!--            <div class="toggle-list">-->
<!--              <label class="toggle-row">-->
<!--                <span>-->
<!--                  <div class="toggle-title">Notificaciones</div>-->
<!--                  <div class="toggle-sub">Avisos generales</div>-->
<!--                </span>-->
<!--                <input type="checkbox" :checked="perfil.preferencias.notificaciones" disabled />-->
<!--              </label>-->

<!--              <label class="toggle-row">-->
<!--                <span>-->
<!--                  <div class="toggle-title">Recordatorios</div>-->
<!--                  <div class="toggle-sub">Clases reservadas</div>-->
<!--                </span>-->
<!--                <input type="checkbox" :checked="perfil.preferencias.recordatorios" disabled />-->
<!--              </label>-->

<!--              <label class="toggle-row">-->
<!--                <span>-->
<!--                  <div class="toggle-title">Comunicaciones</div>-->
<!--                  <div class="toggle-sub">Noticias y ofertas</div>-->
<!--                </span>-->
<!--                <input type="checkbox" :checked="perfil.preferencias.comunicaciones" disabled />-->
<!--              </label>-->
<!--            </div>-->
<!--          </div>-->

<!--          <div class="card">-->
<!--            <div class="card-header">-->
<!--              <h3>Acciones de cuenta</h3>-->
<!--            </div>-->

<!--            <div class="stack">-->
<!--              <button class="btn-outline full" type="button">Cambiar contraseña</button>-->
<!--              <button class="btn-outline full" type="button">Cerrar sesión</button>-->
<!--              <button class="btn-danger full" type="button">Darse de baja</button>-->
<!--            </div>-->
<!--          </div>-->

<!--          <div class="card progress-card">-->
<!--            <div class="card-header">-->
<!--              <h3>Tu progreso este mes</h3>-->
<!--            </div>-->
<!--            <div class="progress-grid">-->
<!--              <div class="progress-item">-->
<!--                <div class="label">Clases completadas</div>-->
<!--                <div class="value">{{ perfil.progreso.clasesCompletadas }}</div>-->
<!--              </div>-->
<!--              <div class="progress-item">-->
<!--                <div class="label">Horas de entrenamiento</div>-->
<!--                <div class="value">{{ perfil.progreso.horasEntrenamiento }}h</div>-->
<!--              </div>-->
<!--              <div class="progress-item">-->
<!--                <div class="label">Calorías quemadas</div>-->
<!--                <div class="value">{{ perfil.progreso.caloriasQuemadas }}</div>-->
<!--              </div>-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->
<!--      </div>-->
<!--    </section>-->
<!--  </div>-->

<!--  &lt;!&ndash; sin iniciar sesión &ndash;&gt;-->
<!--  <div v-else class="perfil-page">-->
<!--    <section class="perfil-content">-->
<!--      <div class="card">-->
<!--        <h3>Sesión requerida</h3>-->
<!--        <p class="empty-text">Inicia sesión para acceder a tu perfil.</p>-->
<!--      </div>-->
<!--    </section>-->
<!--  </div>-->
<!--</template>-->

<!--<style scoped>-->
<!--.perfil-page {-->
<!--  background: #f4f5f6;-->
<!--  min-height: calc(100vh - 70px);-->
<!--}-->

<!--/* banner */-->
<!--.perfil-banner {-->
<!--  background: linear-gradient(90deg, #0b7ea0, #0092b8);-->
<!--  padding: 24px 0;-->
<!--}-->
<!--.perfil-banner-inner {-->
<!--  max-width: 1100px;-->
<!--  margin: 0 auto;-->
<!--  padding: 0 18px;-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  gap: 16px;-->
<!--}-->
<!--.avatar {-->
<!--  width: 64px;-->
<!--  height: 64px;-->
<!--  border-radius: 14px;-->
<!--  background: rgba(255, 255, 255, 0.3);-->
<!--}-->
<!--.perfil-banner-info {-->
<!--  flex: 1;-->
<!--  color: white;-->
<!--}-->
<!--.name-row {-->
<!--  display: flex;-->
<!--  flex-wrap: wrap;-->
<!--  gap: 10px;-->
<!--  align-items: baseline;-->
<!--}-->
<!--.name {-->
<!--  font-weight: 600;-->
<!--}-->
<!--.id {-->
<!--  opacity: 0.9;-->
<!--  font-size: 13px;-->
<!--}-->
<!--.badges {-->
<!--  display: flex;-->
<!--  gap: 10px;-->
<!--  margin-top: 8px;-->
<!--}-->
<!--.badge {-->
<!--  font-size: 12px;-->
<!--  padding: 6px 10px;-->
<!--  border-radius: 999px;-->
<!--  background: rgba(255, 255, 255, 0.2);-->
<!--}-->
<!--.badge.ok {-->
<!--  background: rgba(0, 0, 0, 0.15);-->
<!--}-->
<!--.badge.plan {-->
<!--  background: rgba(255, 255, 255, 0.2);-->
<!--}-->

<!--/* contenido */-->
<!--.perfil-content {-->
<!--  max-width: 1100px;-->
<!--  margin: 0 auto;-->
<!--  padding: 18px;-->
<!--}-->
<!--.grid {-->
<!--  display: grid;-->
<!--  grid-template-columns: 2fr 1fr;-->
<!--  gap: 18px;-->
<!--}-->
<!--@media (max-width: 900px) {-->
<!--  .grid {-->
<!--    grid-template-columns: 1fr;-->
<!--  }-->
<!--}-->

<!--/* secciones */-->
<!--.card {-->
<!--  background: white;-->
<!--  border-radius: 12px;-->
<!--  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.08);-->
<!--  padding: 16px;-->
<!--  margin-bottom: 18px;-->
<!--}-->
<!--.card-header {-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  justify-content: space-between;-->
<!--  margin-bottom: 12px;-->
<!--}-->
<!--.card-header h3 {-->
<!--  margin: 0;-->
<!--  font-weight: 500;-->
<!--  font-size: 16px;-->
<!--}-->

<!--/* botones */-->
<!--.btn-primary {-->
<!--  background: #0092b8;-->
<!--  color: white;-->
<!--  border-radius: 10px;-->
<!--  height: 42px;-->
<!--  cursor: pointer;-->
<!--  border: 0;-->
<!--}-->
<!--.btn-outline {-->
<!--  background: transparent;-->
<!--  border: 1px solid #0092b8;-->
<!--  color: #0092b8;-->
<!--  border-radius: 10px;-->
<!--  height: 38px;-->
<!--  cursor: pointer;-->
<!--  padding: 0 12px;-->
<!--}-->
<!--.btn-danger {-->
<!--  background: transparent;-->
<!--  border: 1px solid #e24444;-->
<!--  color: #e24444;-->
<!--  border-radius: 10px;-->
<!--  height: 38px;-->
<!--  cursor: pointer;-->
<!--}-->
<!--.full {-->
<!--  width: 100%;-->
<!--}-->
<!--.link-btn {-->
<!--  all: unset;-->
<!--  cursor: pointer;-->
<!--  color: #0092b8;-->
<!--  font-size: 13px;-->
<!--}-->

<!--/* info */-->
<!--.info-grid {-->
<!--  display: grid;-->
<!--  grid-template-columns: 1fr 1fr;-->
<!--  gap: 12px 18px;-->
<!--}-->
<!--@media (max-width: 650px) {-->
<!--  .info-grid {-->
<!--    grid-template-columns: 1fr;-->
<!--  }-->
<!--}-->
<!--.info-item {-->
<!--  display: flex;-->
<!--  gap: 6px;-->
<!--  align-items: baseline;-->
<!--}-->
<!--.label {-->
<!--  color: grey;-->
<!--  font-size: 12px;-->
<!--  font-weight: 600;-->
<!--}-->
<!--.value {-->
<!--  font-size: 14px;-->
<!--}-->

<!--/* monedero/cobros */-->
<!--.billing-box {-->
<!--  background: #e9f7fb;-->
<!--  border-radius: 12px;-->
<!--  padding: 12px;-->
<!--  margin-bottom: 10px;-->
<!--}-->
<!--.row {-->
<!--  display: flex;-->
<!--  justify-content: space-between;-->
<!--  gap: 12px;-->
<!--  margin-bottom: 10px;-->
<!--}-->
<!--.right {-->
<!--  text-align: right;-->
<!--}-->
<!--.pill {-->
<!--  border-radius: 12px;-->
<!--  padding: 10px 12px;-->
<!--  font-size: 13px;-->
<!--  margin-top: 10px;-->
<!--}-->
<!--.ok-pill {-->
<!--  background: #e9f8ee;-->
<!--}-->
<!--.wallet-pill {-->
<!--  background: #f3efff;-->
<!--}-->
<!--.actions-row {-->
<!--  display: flex;-->
<!--  gap: 10px;-->
<!--  margin-top: 12px;-->
<!--}-->

<!--/* reservaciones */-->
<!--.reserva-list {-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  gap: 10px;-->
<!--  margin-bottom: 12px;-->
<!--}-->
<!--.reserva-item {-->
<!--  display: flex;-->
<!--  justify-content: space-between;-->
<!--  gap: 10px;-->
<!--  padding: 10px;-->
<!--  border: 1px solid #eee;-->
<!--  border-radius: 10px;-->
<!--}-->
<!--.reserva-title {-->
<!--  font-weight: 500;-->
<!--}-->
<!--.reserva-sub {-->
<!--  color: grey;-->
<!--  font-size: 12px;-->
<!--  margin-top: 2px;-->
<!--}-->
<!--.status {-->
<!--  align-self: start;-->
<!--  font-size: 12px;-->
<!--  padding: 6px 10px;-->
<!--  border-radius: 999px;-->
<!--}-->
<!--.status.ok {-->
<!--  background: #e9f7fb;-->
<!--  color: #0b7ea0;-->
<!--}-->
<!--.status.done {-->
<!--  background: #eef4ff;-->
<!--  color: #2f5aa6;-->
<!--}-->
<!--.empty-text {-->
<!--  color: grey;-->
<!--  font-size: 13px;-->
<!--  margin: 8px 0 12px;-->
<!--}-->


<!--.toggle-list {-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  gap: 12px;-->
<!--}-->
<!--.toggle-row {-->
<!--  display: flex;-->
<!--  justify-content: space-between;-->
<!--  align-items: center;-->
<!--  gap: 12px;-->
<!--  padding: 10px;-->
<!--  border: 1px solid #eee;-->
<!--  border-radius: 10px;-->
<!--}-->
<!--.toggle-title {-->
<!--  font-weight: 500;-->
<!--  font-size: 14px;-->
<!--}-->
<!--.toggle-sub {-->
<!--  font-size: 12px;-->
<!--  color: grey;-->
<!--}-->

<!--/* acciones de cuenta */-->
<!--.stack {-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  gap: 10px;-->
<!--}-->

<!--/* progreso */-->
<!--.progress-card {-->
<!--  background: #0b7ea0;-->
<!--  color: white;-->
<!--}-->
<!--.progress-card .label {-->
<!--  color: rgba(255, 255, 255, 0.8);-->
<!--}-->
<!--.progress-grid {-->
<!--  display: grid;-->
<!--  grid-template-columns: 1fr;-->
<!--  gap: 10px;-->
<!--}-->
<!--.progress-item .value {-->
<!--  font-size: 18px;-->
<!--  font-weight: 600;-->
<!--}-->
<!--</style>-->




<script setup lang="ts">
import { computed, reactive, ref, watch } from "vue"
import { useAuthStore } from "@/stores/auth.store"

const auth = useAuthStore()

const showEdit = ref(false)
const saving = ref(false)
const saveError = ref<string | null>(null)

function displayValue(v?: string | null) {
  return v && v.trim() ? v : "—"
}

// perfil basado en usuario logueado
const perfil = computed(() => {
  const socio = auth.socio
  if (!socio) return null

  const idSocio = `FG-${String(socio.id).padStart(4, "0")}` // e.g. FG-0001
  const membresiaActiva = socio.estado?.toUpperCase() === "ACTIVO"

  return {
    nombre: socio.nombre,
    idSocio,
    email: socio.correoElectronico,
    telefono: displayValue(socio.telefono),
    direccion: displayValue(socio.direccion),
    ciudad: displayValue(socio.ciudad),
    codigoPostal: displayValue(socio.codigoPostal),
    membresiaActiva,
    plan: socio.tarifaNombre ?? `Tarifa #${socio.idTarifa}`,
    // placeholders de otras secciones (se completarán en historias futuras)
    precioMensual: "—",
    fechaInicio: "—",
    proximaRenovacion: "—",
    estadoPago: "—",
    proximoCargo: "—",
    monedero: "—",
    preferencias: {
      notificaciones: true,
      recordatorios: true,
      comunicaciones: false,
    },
    progreso: {
      clasesCompletadas: 0,
      horasEntrenamiento: 0,
      caloriasQuemadas: 0,
    },
    reservas: [] as Array<{
      nombre: string
      instructor: string
      fecha: string
      hora: string
      estado: "Confirmada" | "Completada" | "Cancelada"
    }>,
  }
})

const form = reactive({
  nombre: "",
  telefono: "",
  direccion: "",
  ciudad: "",
  codigoPostal: "",
})

watch(
  () => showEdit.value,
  (open) => {
    if (!open || !auth.socio) return
    // precargar formulario con los datos actuales
    form.nombre = auth.socio.nombre ?? ""
    form.telefono = auth.socio.telefono ?? ""
    form.direccion = auth.socio.direccion ?? ""
    form.ciudad = auth.socio.ciudad ?? ""
    form.codigoPostal = auth.socio.codigoPostal ?? ""
    saveError.value = null
  }
)

function validateForm(): string | null {
  if (!form.nombre.trim()) return "El nombre es obligatorio."
  if (!form.direccion.trim()) return "La dirección es obligatoria."
  if (!form.ciudad.trim()) return "La ciudad es obligatoria."
  if (!form.codigoPostal.trim()) return "El código postal es obligatorio."
  return null
}

async function onSave() {
  const err = validateForm()
  if (err) {
    saveError.value = err
    return
  }

  saving.value = true
  saveError.value = null
  try {
    // requiere que implementes auth.updateMe en Pinia + endpoint backend
    await auth.updateMe({
      nombre: form.nombre.trim(),
      telefono: form.telefono.trim() ? form.telefono.trim() : null,
      direccion: form.direccion.trim(),
      ciudad: form.ciudad.trim(),
      codigoPostal: form.codigoPostal.trim(),
    })
    showEdit.value = false
  } catch (e) {
    saveError.value = e instanceof Error ? e.message : String(e)
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <!-- con sesión iniciada -->
  <div v-if="perfil" class="perfil-page">
    <!-- Banner -->
    <section class="perfil-banner">
      <div class="perfil-banner-inner">
        <div class="avatar" aria-hidden="true"></div>

        <div class="perfil-banner-info">
          <div class="name-row">
            <h2 class="name">{{ perfil.nombre }}</h2>
            <span class="id">ID de Socio: {{ perfil.idSocio }}</span>
          </div>

          <div class="badges">
            <span class="badge ok" v-if="perfil.membresiaActiva">Membresía Activa</span>
            <span class="badge plan">{{ perfil.plan }}</span>
          </div>
        </div>

        <button class="btn-outline" type="button" @click="showEdit = true">Editar perfil</button>
      </div>
    </section>

    <!-- contenido -->
    <section class="perfil-content">
      <div class="grid">
        <!-- columna izquierda -->
        <div class="col-left">
          <div class="card">
            <div class="card-header">
              <h3>Información Personal</h3>
              <button class="link-btn" type="button" @click="showEdit = true">Editar</button>
            </div>

            <div class="info-grid">
              <div class="info-item">
                <span class="label">Nombre completo:</span>
                <span class="value">{{ perfil.nombre }}</span>
              </div>
              <div class="info-item">
                <span class="label">Correo electrónico:</span>
                <span class="value">{{ perfil.email }}</span>
              </div>
              <div class="info-item">
                <span class="label">Teléfono:</span>
                <span class="value">{{ perfil.telefono }}</span>
              </div>
              <div class="info-item">
                <span class="label">Dirección:</span>
                <span class="value">{{ perfil.direccion }}</span>
              </div>
              <div class="info-item">
                <span class="label">Ciudad:</span>
                <span class="value">{{ perfil.ciudad }}</span>
              </div>
              <div class="info-item">
                <span class="label">Código postal:</span>
                <span class="value">{{ perfil.codigoPostal }}</span>
              </div>
            </div>
          </div>

          <!-- el resto lo mantengo igual que tu versión -->
          <div class="card">
            <div class="card-header">
              <h3>Membresía y Facturación</h3>
            </div>

            <div class="billing-box">
              <div class="row">
                <div>
                  <span class="label">Plan Actual:</span>
                  <div class="value">{{ perfil.plan }}</div>
                </div>
                <div class="right">
                  <span class="label">Precio mensual:</span>
                  <div class="value">{{ perfil.precioMensual }}</div>
                </div>
              </div>

              <div class="row">
                <div>
                  <span class="label">Fecha de inicio:</span>
                  <div class="value">{{ perfil.fechaInicio }}</div>
                </div>
                <div class="right">
                  <span class="label">Próxima renovación:</span>
                  <div class="value">{{ perfil.proximaRenovacion }}</div>
                </div>
              </div>
            </div>

            <div class="pill ok-pill">
              <strong>Estado del pago</strong> — {{ perfil.estadoPago }} · Próximo cargo: {{ perfil.proximoCargo }}
            </div>

            <div class="pill wallet-pill">
              <strong>Monedero FitGym</strong> — Saldo disponible: {{ perfil.monedero }}
            </div>

            <div class="actions-row">
              <button class="btn-outline" type="button">Cambiar tarifa</button>
              <button class="btn-outline" type="button">Ver facturas</button>
            </div>
          </div>

          <div class="card">
            <div class="card-header">
              <h3>Actividades y Reservas</h3>
              <button class="link-btn" type="button">Ver todas</button>
            </div>

            <div v-if="perfil.reservas.length" class="reserva-list">
              <div v-for="(r, idx) in perfil.reservas" :key="idx" class="reserva-item">
                <div class="reserva-main">
                  <div class="reserva-title">{{ r.nombre }}</div>
                  <div class="reserva-sub">Instructor: {{ r.instructor }}</div>
                  <div class="reserva-sub">{{ r.fecha }} · {{ r.hora }}</div>
                </div>
                <span class="status" :class="r.estado === 'Completada' ? 'done' : 'ok'">{{ r.estado }}</span>
              </div>
            </div>

            <p v-else class="empty-text">Aún no tienes reservas para mostrar.</p>

            <button class="btn-primary full" type="button">Reservar nueva clase</button>
          </div>
        </div>

        <!-- columna derecha -->
        <div class="col-right">
          <div class="card">
            <div class="card-header">
              <h3>Preferencias</h3>
            </div>

            <div class="toggle-list">
              <label class="toggle-row">
                <span>
                  <div class="toggle-title">Notificaciones</div>
                  <div class="toggle-sub">Avisos generales</div>
                </span>
                <input type="checkbox" :checked="perfil.preferencias.notificaciones" disabled />
              </label>

              <label class="toggle-row">
                <span>
                  <div class="toggle-title">Recordatorios</div>
                  <div class="toggle-sub">Clases reservadas</div>
                </span>
                <input type="checkbox" :checked="perfil.preferencias.recordatorios" disabled />
              </label>

              <label class="toggle-row">
                <span>
                  <div class="toggle-title">Comunicaciones</div>
                  <div class="toggle-sub">Noticias y ofertas</div>
                </span>
                <input type="checkbox" :checked="perfil.preferencias.comunicaciones" disabled />
              </label>
            </div>
          </div>

          <div class="card">
            <div class="card-header">
              <h3>Acciones de cuenta</h3>
            </div>

            <div class="stack">
              <button class="btn-outline full" type="button">Cambiar contraseña</button>
              <button class="btn-outline full" type="button">Cerrar sesión</button>
              <button class="btn-danger full" type="button">Darse de baja</button>
            </div>
          </div>

          <div class="card progress-card">
            <div class="card-header">
              <h3>Tu progreso este mes</h3>
            </div>
            <div class="progress-grid">
              <div class="progress-item">
                <div class="label">Clases completadas</div>
                <div class="value">{{ perfil.progreso.clasesCompletadas }}</div>
              </div>
              <div class="progress-item">
                <div class="label">Horas de entrenamiento</div>
                <div class="value">{{ perfil.progreso.horasEntrenamiento }}h</div>
              </div>
              <div class="progress-item">
                <div class="label">Calorías quemadas</div>
                <div class="value">{{ perfil.progreso.caloriasQuemadas }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- MODAL EDITAR -->
    <div v-if="showEdit" class="modal-backdrop" @click.self="showEdit = false">
      <div class="modal">
        <h3>Editar información personal</h3>

        <p v-if="saveError" class="modal-error">{{ saveError }}</p>

        <div class="modal-grid">
          <label>
            <span>Nombre *</span>
            <input v-model="form.nombre" type="text" />
          </label>

          <label>
            <span>Teléfono</span>
            <input v-model="form.telefono" type="text" />
          </label>

          <label class="full">
            <span>Dirección *</span>
            <input v-model="form.direccion" type="text" />
          </label>

          <label>
            <span>Ciudad *</span>
            <input v-model="form.ciudad" type="text" />
          </label>

          <label>
            <span>Código postal *</span>
            <input v-model="form.codigoPostal" type="text" />
          </label>
        </div>

        <div class="modal-actions">
          <button class="btn-outline" type="button" @click="showEdit = false" :disabled="saving">
            Cancelar
          </button>
          <button class="btn-primary" type="button" @click="onSave" :disabled="saving">
            {{ saving ? "Guardando..." : "Guardar cambios" }}
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- sin iniciar sesión -->
  <div v-else class="perfil-page">
    <section class="perfil-content">
      <div class="card">
        <h3>Sesión requerida</h3>
        <p class="empty-text">Inicia sesión para acceder a tu perfil.</p>
      </div>
    </section>
  </div>
</template>

<style scoped>
.perfil-page {
  background: #f4f5f6;
  min-height: calc(100vh - 70px);
}

/* banner */
.perfil-banner {
  background: linear-gradient(90deg, #0b7ea0, #0092b8);
  padding: 24px 0;
}
.perfil-banner-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 18px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.avatar {
  width: 64px;
  height: 64px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.3);
}
.perfil-banner-info {
  flex: 1;
  color: white;
}
.name-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: baseline;
}
.name {
  font-weight: 600;
}
.id {
  opacity: 0.9;
  font-size: 13px;
}
.badges {
  display: flex;
  gap: 10px;
  margin-top: 8px;
}
.badge {
  font-size: 12px;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.2);
}
.badge.ok {
  background: rgba(0, 0, 0, 0.15);
}
.badge.plan {
  background: rgba(255, 255, 255, 0.2);
}

/* contenido */
.perfil-content {
  max-width: 1100px;
  margin: 0 auto;
  padding: 18px;
}
.grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 18px;
}
@media (max-width: 900px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

/* secciones */
.card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.08);
  padding: 16px;
  margin-bottom: 18px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.card-header h3 {
  margin: 0;
  font-weight: 500;
  font-size: 16px;
}

/* botones */
.btn-primary {
  background: #0092b8;
  color: white;
  border-radius: 10px;
  height: 42px;
  cursor: pointer;
  border: 0;
  padding: 0 14px;
}
.btn-outline {
  background: transparent;
  border: 1px solid #0092b8;
  color: #0092b8;
  border-radius: 10px;
  height: 38px;
  cursor: pointer;
  padding: 0 12px;
}
.btn-danger {
  background: transparent;
  border: 1px solid #e24444;
  color: #e24444;
  border-radius: 10px;
  height: 38px;
  cursor: pointer;
}
.full {
  width: 100%;
}
.link-btn {
  all: unset;
  cursor: pointer;
  color: #0092b8;
  font-size: 13px;
}

/* info */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 18px;
}
@media (max-width: 650px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}
.info-item {
  display: flex;
  gap: 6px;
  align-items: baseline;
}
.label {
  color: grey;
  font-size: 12px;
  font-weight: 600;
}
.value {
  font-size: 14px;
}

/* monedero/cobros */
.billing-box {
  background: #e9f7fb;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 10px;
}
.row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}
.right {
  text-align: right;
}
.pill {
  border-radius: 12px;
  padding: 10px 12px;
  font-size: 13px;
  margin-top: 10px;
}
.ok-pill {
  background: #e9f8ee;
}
.wallet-pill {
  background: #f3efff;
}
.actions-row {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}

/* reservaciones */
.reserva-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 12px;
}
.reserva-item {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 10px;
}
.reserva-title {
  font-weight: 500;
}
.reserva-sub {
  color: grey;
  font-size: 12px;
  margin-top: 2px;
}
.status {
  align-self: start;
  font-size: 12px;
  padding: 6px 10px;
  border-radius: 999px;
}
.status.ok {
  background: #e9f7fb;
  color: #0b7ea0;
}
.status.done {
  background: #eef4ff;
  color: #2f5aa6;
}
.empty-text {
  color: grey;
  font-size: 13px;
  margin: 8px 0 12px;
}

/* toggles */
.toggle-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.toggle-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 10px;
}
.toggle-title {
  font-weight: 500;
  font-size: 14px;
}
.toggle-sub {
  font-size: 12px;
  color: grey;
}

/* acciones de cuenta */
.stack {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* progreso */
.progress-card {
  background: #0b7ea0;
  color: white;
}
.progress-card .label {
  color: rgba(255, 255, 255, 0.8);
}
.progress-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}
.progress-item .value {
  font-size: 18px;
  font-weight: 600;
}

/* MODAL */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: grid;
  place-items: center;
  padding: 16px;
  z-index: 50;
}

.modal {
  width: min(680px, 100%);
  background: white;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.25);
}

.modal h3 {
  margin: 0 0 10px;
}

.modal-error {
  color: #b91c1c;
  font-size: 13px;
  margin: 6px 0 10px;
}

.modal-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.modal-grid label span {
  display: block;
  font-size: 12px;
  color: grey;
  margin-bottom: 6px;
  font-weight: 600;
}

.modal-grid input {
  width: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px 12px;
  box-sizing: border-box;
}

.modal-grid .full {
  grid-column: 1 / -1;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 14px;
}
</style>

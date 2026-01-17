<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue"
import { getMisReservas, cancelarReserva, type ReservaItem, type ReservaCancelResponse } from "@/services/reservas"
import { useAuthStore } from "@/stores/auth.store"

const auth = useAuthStore()

const reservas = ref<ReservaItem[]>([])
const error = ref<string | null>(null)
const loading = ref(false)

const paginaActual = ref(1)
const reservasPagina = 9

const diaFiltrado = ref<string>("Todos")
const estadoFiltrado = ref<string>("Todos")

const dias = ["Todos", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"]
const estados = ["Todos", "CONFIRMADA", "CANCELADA"]

const cancelandoId = ref<number | null>(null)
const cancelError = ref<string | null>(null)
const cancelOk = ref<string | null>(null)

function obtenerDiaSemana(fechaStr: string): string {
  const fecha = new Date(fechaStr)
  const dia = fecha.toLocaleDateString("es-ES", { weekday: "long" })
  return dia.charAt(0).toUpperCase() + dia.slice(1).toLowerCase()
}

async function cargar() {
  loading.value = true
  try {
    error.value = null
    // "todas" por ahora
    reservas.value = await getMisReservas(200)
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  } finally {
    loading.value = false
  }
}

const reservasFiltradas = computed(() => {
  let list = reservas.value

  if (diaFiltrado.value !== "Todos") {
    list = list.filter((r) => obtenerDiaSemana(r.fecha) === diaFiltrado.value)
  }

  if (estadoFiltrado.value !== "Todos") {
    list = list.filter((r) => r.estado?.toUpperCase() === estadoFiltrado.value)
  }

  // orden por fecha/hora (si vienen como string)
  return [...list].sort((a, b) => {
    const da = new Date(`${a.fecha}T${a.horaIni}`)
    const db = new Date(`${b.fecha}T${b.horaIni}`)
    return da.getTime() - db.getTime()
  })
})

const totalPaginas = computed(() => Math.ceil(reservasFiltradas.value.length / reservasPagina))

const reservasPaginadas = computed(() => {
  const inicio = (paginaActual.value - 1) * reservasPagina
  const fin = inicio + reservasPagina
  return reservasFiltradas.value.slice(inicio, fin)
})

watch([diaFiltrado, estadoFiltrado], () => {
  paginaActual.value = 1
})

function paginaSiguiente() {
  if (paginaActual.value < totalPaginas.value) paginaActual.value++
}
function paginaAnterior() {
  if (paginaActual.value > 1) paginaActual.value--
}

function seleccionarDia(dia: string) {
  diaFiltrado.value = dia
}

function seleccionarEstado(e: string) {
  estadoFiltrado.value = e
}

function formatFecha(fecha: string) {
  const d = new Date(fecha)
  if (Number.isNaN(d.getTime())) return fecha
  return d.toLocaleDateString("es-ES", { year: "numeric", month: "2-digit", day: "2-digit" })
}

async function onCancelar(idActividad: number) {
  cancelError.value = null
  cancelOk.value = null
  cancelandoId.value = idActividad

  try {
    const res = await cancelarReserva(idActividad)

    // refrescar sesión (si el backend devuelve saldo actualizado)
    await auth.loadSession()

    // refrescar lista
    await cargar()

    cancelOk.value =
      res?.reembolso != null
        ? `Reserva cancelada. Reembolso: €${Number(res.reembolso).toFixed(2)}`
        : "Reserva cancelada."
  } catch (e) {
    cancelError.value = e instanceof Error ? e.message : String(e)
  } finally {
    cancelandoId.value = null
    setTimeout(() => (cancelOk.value = null), 3000)
  }
}

onMounted(cargar)
</script>

<template>
  <!-- HERO -->
  <section class="first-section">
    <div class="first-section-static-info">
      <p>Mis reservas</p>
      <p>Consulta tus reservas, filtra por día/estado y cancela si lo necesitas.</p>
    </div>
  </section>

  <!-- FILTERS -->
  <section class="filters-section">
    <div class="filters-grid">
      <div class="day-container">
        <p>Día de la semana</p>

        <div class="day-selection-container">
          <label
            class="day-option"
            v-for="dia in dias"
            :key="dia"
            :class="{ active: diaFiltrado === dia }"
          >
            <input type="radio" name="day" :value="dia" v-model="diaFiltrado" hidden />
            <span @click="seleccionarDia(dia)">{{ dia }}</span>
          </label>
        </div>
      </div>

      <div class="status-container">
        <p>Estado</p>

        <div class="day-selection-container">
          <label
            class="day-option"
            v-for="e in estados"
            :key="e"
            :class="{ active: estadoFiltrado === e }"
          >
            <input type="radio" name="estado" :value="e" v-model="estadoFiltrado" hidden />
            <span @click="seleccionarEstado(e)">{{ e === "Todos" ? "Todos" : e }}</span>
          </label>
        </div>
      </div>
    </div>
  </section>

  <!-- CONTENT -->
  <section class="activity-cards-section">
    <div class="activity-cards">
      <div v-if="loading" class="empty-box">Cargando reservas...</div>
      <div v-else-if="error" class="empty-box error-box">{{ error }}</div>

      <div v-else-if="reservasPaginadas.length === 0" class="empty-box">
        No tienes reservas para estos filtros.
      </div>

      <div v-else v-for="r in reservasPaginadas" :key="`${r.idActividad}-${r.fecha}-${r.horaIni}`" class="reserva-card">
        <div class="card-top">
          <div class="card-title">{{ r.actividadNombre }}</div>
          <span class="badge" :class="r.estado === 'CANCELADA' ? 'badge-cancel' : 'badge-ok'">
            {{ r.estado }}
          </span>
        </div>

        <div class="card-sub">
          {{ formatFecha(r.fecha) }} · {{ r.horaIni }} - {{ r.horaFin }}
        </div>

        <div class="card-sub" v-if="r.precioPagado != null">
          Precio pagado: €
          {{
            typeof r.precioPagado === "string"
              ? Number(r.precioPagado).toFixed(2)
              : Number(r.precioPagado).toFixed(2)
          }}
        </div>

        <div class="card-actions">
          <button
            class="btn-cancel"
            type="button"
            v-if="r.estado !== 'CANCELADA'"
            :disabled="cancelandoId === r.idActividad"
            @click="onCancelar(r.idActividad)"
          >
            {{ cancelandoId === r.idActividad ? "Cancelando..." : "Cancelar reserva" }}
          </button>
        </div>
      </div>
    </div>
  </section>

  <!-- FEEDBACK -->
  <div class="feedback" v-if="cancelError || cancelOk">
    <p v-if="cancelError" class="feedback-error">{{ cancelError }}</p>
    <p v-else class="feedback-ok">{{ cancelOk }}</p>
  </div>

  <!-- PAGINATION -->
  <div class="pagination">
    <button class="pagination-btn" @click="paginaAnterior" :disabled="paginaActual === 1">
      ← Anterior
    </button>

    <span>Página {{ reservasFiltradas.length ? paginaActual : 0 }} de {{ reservasFiltradas.length ? totalPaginas : 0 }}</span>

    <button class="pagination-btn" @click="paginaSiguiente" :disabled="paginaActual === totalPaginas || totalPaginas === 0">
      Siguiente →
    </button>
  </div>
</template>

<style>
html {
  background-color: #f4f5f6;
}

.first-section {
  height: 200px;
  background-color: #0092b8;
  color: white;
  display: flex;
  flex-direction: column;
}

.first-section-static-info {
  width: 570px;
  margin-left: 200px;
  margin-top: 60px;
  display: flex;
  background-color: #0092b8;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

.first-section-static-info > p {
  background-color: #0092b8;
}

.filters-section {
  background-color: #f4f5f6;
  display: flex;
  flex-direction: column;
  height: auto;
  padding-bottom: 16px;
}

.filters-grid {
  margin-left: 200px;
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 18px;
  color: #364153;
}

.day-container,
.status-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.day-selection-container {
  width: 700px;
  height: auto;
  display: flex;
  flex-direction: row;
  gap: 10px;
  flex-wrap: wrap;
}

.day-option {
  cursor: pointer;
}

.day-option.active span {
  background-color: #0092b8;
  color: white;
}

.day-option input {
  display: none;
}

.day-option span {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  min-width: 70px;
  padding: 0 10px;
  background-color: #e7e9ed;
  border-radius: 5px;
  cursor: pointer;
  font-size: 15px;
}

.activity-cards-section {
  background-color: #f4f5f6;
  height: auto;
  display: flex;
  justify-content: center;
  align-self: center;
  padding-bottom: 30px;
}

.activity-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}

.reserva-card {
  width: 320px;
  background: white;
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.card-title {
  font-weight: 700;
  color: #111827;
}

.card-sub {
  color: #6b7280;
  font-size: 13px;
}

.badge {
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  border: 1px solid #e5e7eb;
}

.badge-ok {
  background: #e9f7fb;
  color: #0b7ea0;
}

.badge-cancel {
  background: #fce8e8;
  color: #b91c1c;
}

.card-actions {
  margin-top: 6px;
  display: flex;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  background: transparent;
  border: 1px solid #e24444;
  color: #e24444;
  font-weight: 600;
}

.btn-cancel:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.empty-box {
  grid-column: 1 / -1;
  padding: 16px;
  border-radius: 10px;
  background: white;
  border: 1px solid #eee;
  color: #364153;
}

.error-box {
  color: #b91c1c;
}

.feedback {
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}

.feedback-ok {
  color: #0b7ea0;
  background: #e9f7fb;
  border: 1px solid #cfeef6;
  padding: 10px 12px;
  border-radius: 8px;
}

.feedback-error {
  color: #b91c1c;
  background: #fce8e8;
  border: 1px solid #f7c2c2;
  padding: 10px 12px;
  border-radius: 8px;
}

.pagination {
  margin: 40px 0;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.pagination-btn {
  padding: 10px 20px;
  background-color: #0092b8;
  color: white;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
}

.pagination-btn:disabled {
  background-color: #c7cbd1;
  cursor: not-allowed;
}
</style>

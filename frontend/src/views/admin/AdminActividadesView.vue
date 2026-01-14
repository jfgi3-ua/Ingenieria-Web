<script setup lang="ts">
import { computed, onMounted, ref } from "vue"
import type { AdminActividad, AdminActividadRequest } from "@/types/adminActividad"
import { adminCrearActividad, adminEditarActividad, adminListarActividades } from "@/services/adminActividades"
import type { IdNombre, SalaLookup } from "@/types/adminLookups"
import { adminListarMonitores, adminListarSalas, adminListarTiposActividad } from "@/services/adminLookups"

const monitores = ref<IdNombre[]>([])
const salas = ref<SalaLookup[]>([])
const tipos = ref<IdNombre[]>([])

const actividades = ref<AdminActividad[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const query = ref("")

const showModal = ref(false)
const editing = ref<AdminActividad | null>(null)

const form = ref<AdminActividadRequest>({
  nombre: "",
  horaIni: "09:00:00",
  horaFin: "10:00:00",
  precioExtra: 0,
  fecha: "",
  plazas: 20,
  idMonitor: 1,
  idSala: 1,
  idTipoActividad: 1,
})

const filtered = computed(() => {
  const q = query.value.trim().toLowerCase()
  if (!q) return actividades.value
  return actividades.value.filter(a =>
    `${a.id} ${a.nombre} ${a.monitorNombre} ${a.salaDescripcion} ${a.tipoActividadNombre} ${a.fecha}`.toLowerCase().includes(q)
  )
})

async function load() {
  loading.value = true
  error.value = null
  try {
    actividades.value = await adminListarActividades()
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = null
  form.value = {
    nombre: "",
    horaIni: "09:00:00",
    horaFin: "10:00:00",
    precioExtra: 0,
    fecha: "",
    plazas: 20,
    idMonitor: 1,
    idSala: 1,
    idTipoActividad: 1,
  }
  showModal.value = true
}

function openEdit(a: AdminActividad) {
  editing.value = a
  form.value = {
    nombre: a.nombre,
    horaIni: a.horaIni,
    horaFin: a.horaFin,
    precioExtra: a.precioExtra,
    fecha: a.fecha,
    plazas: a.plazas,
    idMonitor: a.idMonitor,
    idSala: a.idSala,
    idTipoActividad: a.idTipoActividad,
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

async function save() {
  loading.value = true
  error.value = null
  try {
    if (!editing.value) {
      const created = await adminCrearActividad(form.value)
      actividades.value = [created, ...actividades.value]
    } else {
      const updated = await adminEditarActividad(editing.value.id, form.value)
      actividades.value = actividades.value.map(x => (x.id === updated.id ? updated : x))
    }
    closeModal()
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await Promise.all([load(), loadLookups()])
})

async function loadLookups() {
  try {
    const [m, s, t] = await Promise.all([
      adminListarMonitores(),
      adminListarSalas(),
      adminListarTiposActividad(),
    ])
    monitores.value = m
    salas.value = s
    tipos.value = t
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  }
}
</script>

<template>
  <div class="page">
    <header class="header">
      <div>
        <h1>Administración · Actividades</h1>
        <p class="subtitle">Crea y edita actividades (monitor, sala, tipo, plazas, fecha y horarios).</p>
      </div>

      <div class="controls">
        <input v-model="query" class="search" placeholder="Buscar por nombre, monitor, sala, tipo, fecha..." />
        <button class="btn ghost" @click="load" :disabled="loading">Recargar</button>
        <button class="btn" @click="openCreate">Nueva actividad</button>
      </div>
    </header>

    <div v-if="error" class="alert">{{ error }}</div>

    <div class="card">
      <div v-if="loading" class="muted">Cargando…</div>

      <table v-else class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Fecha</th>
            <th>Hora</th>
            <th>Monitor</th>
            <th>Sala</th>
            <th>Tipo</th>
            <th>Plazas</th>
            <th class="right">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="a in filtered" :key="a.id">
            <td>{{ a.id }}</td>
            <td class="strong">{{ a.nombre }}</td>
            <td>{{ a.fecha }}</td>
            <td>{{ a.horaIni.slice(0,5) }} - {{ a.horaFin.slice(0,5) }}</td>
            <td>{{ a.monitorNombre }}</td>
            <td>{{ a.salaDescripcion }}</td>
            <td>{{ a.tipoActividadNombre }}</td>
            <td>{{ a.disponibles }}/{{ a.plazas }}</td>
            <td class="right">
              <button class="btn ghost" @click="openEdit(a)">Editar</button>
            </td>
          </tr>
          <tr v-if="filtered.length === 0">
            <td colspan="9" class="muted center">No hay resultados.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="modalOverlay" @click.self="closeModal">
      <div class="modal">
        <div class="modalHeader">
          <h2>{{ editing ? "Editar actividad" : "Nueva actividad" }}</h2>
          <button class="btn ghost" @click="closeModal">Cerrar</button>
        </div>

        <div class="grid">
          <label>
            <span>Nombre</span>
            <input v-model="form.nombre" />
          </label>

          <label>
            <span>Fecha (YYYY-MM-DD)</span>
            <input v-model="form.fecha" placeholder="2026-01-20" />
          </label>

          <label>
            <span>Hora inicio</span>
            <input v-model="form.horaIni" placeholder="18:00:00" />
          </label>

          <label>
            <span>Hora fin</span>
            <input v-model="form.horaFin" placeholder="19:00:00" />
          </label>

          <label>
            <span>Precio extra</span>
            <input v-model.number="form.precioExtra" type="number" step="0.5" min="0" />
          </label>

          <label>
            <span>Plazas</span>
            <input v-model.number="form.plazas" type="number" min="1" />
          </label>

          <label>
            <span>Monitor</span>
            <select v-model.number="form.idMonitor">
                <option v-for="m in monitores" :key="m.id" :value="m.id">{{ m.nombre }}</option>
            </select>
         </label>

          <label>
            <span>Sala</span>
            <select v-model.number="form.idSala">
                <option v-for="s in salas" :key="s.id" :value="s.id">{{ s.descripcion }}</option>
            </select>
         </label>


         <label class="full">
            <span>Tipo de actividad</span>
            <select v-model.number="form.idTipoActividad">
                <option v-for="t in tipos" :key="t.id" :value="t.id">{{ t.nombre }}</option>
            </select>
         </label>
        </div>

        <div class="modalActions">
          <button class="btn ghost" @click="closeModal">Cancelar</button>
          <button class="btn" @click="save" :disabled="loading">
            {{ loading ? "Guardando..." : "Guardar" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; padding: 24px; }
.header { display:flex; justify-content: space-between; gap: 16px; align-items: flex-end; margin-bottom: 16px; }
.subtitle { margin: 6px 0 0; opacity: .75; }
.controls { display:flex; gap: 10px; align-items: center; flex-wrap: wrap; }
.search { width: 360px; padding: 10px 12px; border-radius: 10px; border: 1px solid rgba(0,0,0,.15); }
.card { background: white; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,.06); padding: 14px; }
.table { width: 100%; border-collapse: collapse; }
th, td { padding: 10px 8px; border-bottom: 1px solid rgba(0,0,0,.06); font-size: 14px; }
th { text-align: left; opacity: .7; font-weight: 600; }
.right { text-align: right; }
.center { text-align: center; }
.strong { font-weight: 700; }
.muted { opacity: .7; }
.alert { background: #fff3f3; border: 1px solid #ffd1d1; color: #8a1f1f; padding: 10px 12px; border-radius: 12px; margin-bottom: 12px; }

.btn { padding: 9px 12px; border-radius: 10px; border: 1px solid rgba(0,0,0,.12); background: #111827; color: white; cursor: pointer; }
.btn:disabled { opacity: .6; cursor: not-allowed; }
.btn.ghost { background: transparent; color: #111827; }
.modalOverlay { position: fixed; inset: 0; background: rgba(0,0,0,.35); display:flex; align-items: center; justify-content: center; padding: 18px; }
.modal { width: min(980px, 96vw); background: white; border-radius: 18px; box-shadow: 0 20px 60px rgba(0,0,0,.20); padding: 20px;}
.modalHeader { display:flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; align-items: end; }
.full { grid-column: 1 / -1; }
label span { display:block; font-size: 12px; opacity: .75; margin-bottom: 6px; }
input { width: 100%; padding: 10px 12px; border-radius: 10px; border: 1px solid rgba(0,0,0,.15); }
select { width: 100%; padding: 10px 12px; border-radius: 10px; border: 1px solid rgba(0,0,0,.15); background: white; }
.modalActions { display:flex; justify-content: flex-end; gap: 10px; margin-top: 14px; }
input, select { width: 100%; box-sizing: border-box; }
.modal { max-height: 90vh; overflow: auto; }
</style>

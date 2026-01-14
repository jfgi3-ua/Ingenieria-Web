<script setup lang="ts">
import { ref, computed, onMounted } from "vue"
import type { AdminMonitor, AdminMonitorRequest } from "@/types/adminMonitor"
import { adminListarMonitores, adminCrearMonitor, adminEditarMonitor } from "@/services/adminMonitores"

const monitores = ref<AdminMonitor[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const query = ref("")

const showModal = ref(false)
const editing = ref<AdminMonitor | null>(null)

const form = ref<AdminMonitorRequest>({
  nombre: "",
  dni: "",
  correoElectronico: "",
  contrasena: "",
  telefono: "",
  ciudad: "",
  direccion: "",
  codigoPostal: "",
})

const filtered = computed(() => {
  const q = query.value.trim().toLowerCase()
  if (!q) return monitores.value
  return monitores.value.filter(m =>
    `${m.id} ${m.nombre} ${m.dni} ${m.correoElectronico} ${m.telefono} ${m.ciudad}`.toLowerCase().includes(q)
  )
})

async function load() {
  loading.value = true
  error.value = null
  try {
    monitores.value = await adminListarMonitores()
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
    dni: "",
    correoElectronico: "",
    contrasena: "",
    telefono: "",
    ciudad: "",
    direccion: "",
    codigoPostal: "",
  }
  showModal.value = true
}

function openEdit(m: AdminMonitor) {
  editing.value = m
  // contraseña: por seguridad pedimos una nueva (no podemos mostrar hash)
  form.value = {
    nombre: m.nombre,
    dni: m.dni,
    correoElectronico: m.correoElectronico,
    contrasena: "",
    telefono: m.telefono,
    ciudad: m.ciudad,
    direccion: m.direccion,
    codigoPostal: m.codigoPostal,
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editing.value = null
}

async function save() {
  loading.value = true
  error.value = null
  try {
    // obligamos a que meta contraseña al crear y al editar (porque el backend la requiere)
    if (!form.value.contrasena.trim()) {
      error.value = "Introduce una contraseña para el monitor."
      loading.value = false
      return
    }

    if (!editing.value) {
      const created = await adminCrearMonitor(form.value)
      monitores.value = [created, ...monitores.value]
    } else {
      const updated = await adminEditarMonitor(editing.value.id, form.value)
      monitores.value = monitores.value.map(x => (x.id === updated.id ? updated : x))
    }
    closeModal()
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <header class="header">
      <div>
        <h1>Administración · Monitores</h1>
        <p class="subtitle">Crea y edita monitores del gimnasio.</p>
      </div>

      <div class="controls">
        <input v-model="query" class="search" placeholder="Buscar por nombre, DNI, email, ciudad..." />
        <button class="btn ghost" @click="load" :disabled="loading">Recargar</button>
        <button class="btn" @click="openCreate">Nuevo monitor</button>
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
            <th>DNI</th>
            <th>Email</th>
            <th>Teléfono</th>
            <th>Ciudad</th>
            <th class="right">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="m in filtered" :key="m.id">
            <td>{{ m.id }}</td>
            <td class="strong">{{ m.nombre }}</td>
            <td>{{ m.dni }}</td>
            <td>{{ m.correoElectronico }}</td>
            <td>{{ m.telefono }}</td>
            <td>{{ m.ciudad }}</td>
            <td class="right">
              <button class="btn ghost" @click="openEdit(m)">Editar</button>
            </td>
          </tr>
          <tr v-if="filtered.length === 0">
            <td colspan="7" class="muted center">No hay resultados.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="modalOverlay" @click.self="closeModal">
      <div class="modal">
        <div class="modalHeader">
          <h2>{{ editing ? "Editar monitor" : "Nuevo monitor" }}</h2>
          <button class="btn ghost" @click="closeModal">Cerrar</button>
        </div>

        <div class="grid">
          <label>
            <span>Nombre</span>
            <input v-model="form.nombre" />
          </label>

          <label>
            <span>DNI</span>
            <input v-model="form.dni" />
          </label>

          <label>
            <span>Email</span>
            <input v-model="form.correoElectronico" type="email" />
          </label>

          <label>
            <span>Contraseña</span>
            <input v-model="form.contrasena" type="password" placeholder="Nueva contraseña" />
          </label>

          <label>
            <span>Teléfono</span>
            <input v-model="form.telefono" />
          </label>

          <label>
            <span>Ciudad</span>
            <input v-model="form.ciudad" />
          </label>

          <label class="full">
            <span>Dirección</span>
            <input v-model="form.direccion" />
          </label>

          <label>
            <span>Código Postal</span>
            <input v-model="form.codigoPostal" />
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
.modal { width: min(980px, 96vw); background: white; border-radius: 18px; box-shadow: 0 20px 60px rgba(0,0,0,.20); padding: 20px; max-height: 90vh; overflow: auto; }
.modalHeader { display:flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.grid { display:grid; grid-template-columns: 1fr 1fr; gap: 16px; align-items: end; }
.full { grid-column: 1 / -1; }
label span { display:block; font-size: 12px; opacity: .75; margin-bottom: 6px; }
input { width: 100%; box-sizing: border-box; padding: 10px 12px; border-radius: 10px; border: 1px solid rgba(0,0,0,.15); }
.modalActions { display:flex; justify-content: flex-end; gap: 10px; margin-top: 14px; }
</style>

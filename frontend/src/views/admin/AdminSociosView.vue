<script setup lang="ts">
import { onMounted, ref, computed } from "vue"
import type { AdminSocio, AdminSocioUpdateRequest } from "@/types/adminSocio"
import { adminListarSocios, adminActualizarSocio, adminCambiarEstadoSocio } from "@/services/adminSocios"

const socios = ref<AdminSocio[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const query = ref("")
const selected = ref<AdminSocio | null>(null)
const showModal = ref(false)

const form = ref<AdminSocioUpdateRequest>({
  nombre: "",
  correoElectronico: "",
  telefono: "",
  idTarifa: 0,
  direccion: "",
  ciudad: "",
  codigoPostal: "",
  pagoDomiciliado: false,
})

const filtered = computed(() => {
  const q = query.value.trim().toLowerCase()
  if (!q) return socios.value
  return socios.value.filter(s =>
    `${s.id} ${s.nombre} ${s.correoElectronico} ${s.tarifaNombre} ${s.estado} ${s.ciudad ?? ""}`.toLowerCase().includes(q)
  )
})

async function load() {
  loading.value = true
  error.value = null
  try {
    socios.value = await adminListarSocios()
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  } finally {
    loading.value = false
  }
}

function openEdit(s: AdminSocio) {
  selected.value = s
  form.value = {
    nombre: s.nombre,
    correoElectronico: s.correoElectronico,
    telefono: s.telefono ?? "",
    idTarifa: s.idTarifa,
    direccion: s.direccion ?? "",
    ciudad: s.ciudad ?? "",
    codigoPostal: s.codigoPostal ?? "",
    pagoDomiciliado: !!s.pagoDomiciliado,
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  selected.value = null
}

async function save() {
  if (!selected.value) return
  loading.value = true
  error.value = null
  try {
    const updated = await adminActualizarSocio(selected.value.id, form.value)
    socios.value = socios.value.map(s => (s.id === updated.id ? updated : s))
    closeModal()
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  } finally {
    loading.value = false
  }
}

async function toggleEstado(s: AdminSocio) {
  const next = s.estado === "ACTIVO" ? "INACTIVO" : "ACTIVO"
  const ok = confirm(`¿Seguro que quieres cambiar el estado de "${s.nombre}" a ${next}?`)
  if (!ok) return

  loading.value = true
  error.value = null
  try {
    const updated = await adminCambiarEstadoSocio(s.id, { estado: next as any })
    socios.value = socios.value.map(x => (x.id === updated.id ? updated : x))
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
        <h1>Administración · Socios</h1>
        <p class="subtitle">Gestiona usuarios: editar datos, bloquear y desbloquear acceso.</p>
      </div>

      <div class="controls">
        <input v-model="query" class="search" placeholder="Buscar por nombre, email, tarifa, ciudad..." />
        <button class="btn" @click="load" :disabled="loading">Recargar</button>
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
            <th>Email</th>
            <th>Tarifa</th>
            <th>Estado</th>
            <th>Ciudad</th>
            <th class="right">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in filtered" :key="s.id">
            <td>{{ s.id }}</td>
            <td class="strong">{{ s.nombre }}</td>
            <td>{{ s.correoElectronico }}</td>
            <td>{{ s.tarifaNombre }}</td>
            <td>
              <span class="pill" :class="s.estado === 'ACTIVO' ? 'ok' : 'bad'">{{ s.estado }}</span>
            </td>
            <td>{{ s.ciudad ?? "-" }}</td>
            <td class="right">
              <button class="btn ghost" @click="openEdit(s)">Editar</button>
              <button class="btn" :class="s.estado === 'ACTIVO' ? 'danger' : 'success'" @click="toggleEstado(s)">
                {{ s.estado === "ACTIVO" ? "Bloquear" : "Desbloquear" }}
              </button>
            </td>
          </tr>
          <tr v-if="filtered.length === 0">
            <td colspan="7" class="muted center">No hay resultados.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal edición -->
    <div v-if="showModal" class="modalOverlay" @click.self="closeModal">
      <div class="modal">
        <div class="modalHeader">
          <h2>Editar socio</h2>
          <button class="btn ghost" @click="closeModal">Cerrar</button>
        </div>

        <div class="grid">
          <label>
            <span>Nombre</span>
            <input v-model="form.nombre" />
          </label>

          <label>
            <span>Email</span>
            <input v-model="form.correoElectronico" />
          </label>

          <label>
            <span>Teléfono</span>
            <input v-model="form.telefono" />
          </label>

          <label>
            <span>ID Tarifa</span>
            <input v-model.number="form.idTarifa" type="number" min="1" />
          </label>

          <label>
            <span>Dirección</span>
            <input v-model="form.direccion" />
          </label>

          <label>
            <span>Ciudad</span>
            <input v-model="form.ciudad" />
          </label>

          <label>
            <span>Código Postal</span>
            <input v-model="form.codigoPostal" />
          </label>

          <label class="switch">
            <input v-model="form.pagoDomiciliado" type="checkbox" />
            <span>Pago domiciliado</span>
          </label>
        </div>

        <div class="modalActions">
          <button class="btn ghost" @click="closeModal">Cancelar</button>
          <button class="btn" @click="save" :disabled="loading">Guardar cambios</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; padding: 24px; }
.header { display:flex; justify-content: space-between; gap: 16px; align-items: flex-end; margin-bottom: 16px; }
.subtitle { margin: 6px 0 0; opacity: .75; }
.controls { display:flex; gap: 10px; align-items: center; }
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
.btn.danger { background: #b91c1c; }
.btn.success { background: #166534; }

.pill { display:inline-block; padding: 4px 10px; border-radius: 999px; font-size: 12px; border: 1px solid rgba(0,0,0,.12); }
.pill.ok { background: rgba(22,101,52,.10); }
.pill.bad { background: rgba(185,28,28,.10); }

.modalOverlay { position: fixed; inset: 0; background: rgba(0,0,0,.35); display:flex; align-items: center; justify-content: center; padding: 18px; }
.modal { width: min(980px, 96vw); background: white; border-radius: 18px; box-shadow: 0 20px 60px rgba(0,0,0,.20); padding: 20px; }
.modalHeader { display:flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.grid { display: grid;  grid-template-columns: 1fr 1fr; gap: 16px; align-items: end; }
label span { display:block; font-size: 12px; opacity: .75; margin-bottom: 6px; }
input { width: 100%; padding: 10px 12px; border-radius: 10px; border: 1px solid rgba(0,0,0,.15); }
.switch { grid-column: 1 / -1; display:flex; gap: 10px; align-items: center; padding-top: 6px; }
.modalActions { display:flex; justify-content: flex-end; gap: 10px; margin-top: 14px; }
input, select {
  width: 100%;
  box-sizing: border-box;
}
.modal {
  max-height: 90vh;
  overflow: auto;
}
</style>

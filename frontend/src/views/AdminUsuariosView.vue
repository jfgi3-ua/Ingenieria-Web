<script setup lang="ts">
import { onMounted, ref } from "vue"
import { listarSociosAdmin } from "@/services/admin.socios.service"
import type { AdminSocio } from "@/types/socio"

const socios = ref<AdminSocio[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

onMounted(async () => {
  try {
    socios.value = await listarSociosAdmin()
  } catch (e) {
    error.value = "Error cargando los socios"
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="page-container">
    <!-- Card principal -->
    <div class="card">
      <div class="card-header">
        <div>
          <h1>Usuarios (Socios)</h1>
          <p>Gestión de socios del gimnasio</p>
        </div>
      </div>

      <div v-if="loading" class="status-text">Cargando socios...</div>
      <div v-if="error" class="status-text error">{{ error }}</div>

      <div v-if="!loading && socios.length" class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Email</th>
              <th>Teléfono</th>
              <th>Tarifa</th>
              <th>Estado</th>
              <th class="actions-col">Acciones</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="s in socios" :key="s.id">
              <td>{{ s.nombre }}</td>
              <td class="muted">{{ s.correoElectronico }}</td>
              <td>{{ s.telefono ?? "-" }}</td>

              <td>
                <span class="badge badge-info">
                  {{ s.tarifaNombre }}
                </span>
              </td>

              <td>
                <span
                  class="badge"
                  :class="s.estado === 'ACTIVO' ? 'badge-success' : 'badge-danger'"
                >
                  {{ s.estado }}
                </span>
              </td>

              <td class="actions">
                <button class="btn btn-secondary">Editar</button>
                <button
                  class="btn"
                  :class="s.estado === 'ACTIVO' ? 'btn-danger' : 'btn-success'"
                >
                  {{ s.estado === 'ACTIVO' ? 'Bloquear' : 'Activar' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="!loading && socios.length === 0" class="status-text">
        No hay socios registrados.
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Layout general */
.page-container {
  padding: 32px;
  background-color: #f4f6f8;
  min-height: 100vh;
}

/* Card */
.card {
  background: #ffffff;
  border-radius: 14px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);
  padding: 24px;
}

/* Header */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.card-header h1 {
  margin: 0;
  font-size: 1.6rem;
}

.card-header p {
  margin: 4px 0 0;
  color: #6b7280;
}

/* Tabla */
.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background-color: #f1f5f9;
}

th {
  text-align: left;
  padding: 14px;
  font-size: 0.85rem;
  color: #374151;
}

td {
  padding: 14px;
  border-bottom: 1px solid #e5e7eb;
  font-size: 0.9rem;
}

.muted {
  color: #6b7280;
}

/* Badges */
.badge {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
  display: inline-block;
}

.badge-success {
  background-color: #dcfce7;
  color: #166534;
}

.badge-danger {
  background-color: #fee2e2;
  color: #991b1b;
}

.badge-info {
  background-color: #e0f2fe;
  color: #075985;
}

/* Acciones */
.actions {
  display: flex;
  gap: 8px;
}

.actions-col {
  text-align: right;
}

.btn {
  padding: 6px 12px;
  border-radius: 8px;
  border: none;
  font-size: 0.75rem;
  cursor: pointer;
}

.btn-secondary {
  background-color: #e5e7eb;
  color: #111827;
}

.btn-danger {
  background-color: #ef4444;
  color: white;
}

.btn-success {
  background-color: #22c55e;
  color: white;
}

/* Estados */
.status-text {
  padding: 24px;
  text-align: center;
  color: #6b7280;
}

.status-text.error {
  color: #b91c1c;
}
</style>

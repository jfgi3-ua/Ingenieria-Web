<script setup lang="ts">
import { ref, onMounted } from "vue"
import { getMisReservas, type ReservaItem } from "@/services/reservas"

const reservas = ref<ReservaItem[]>([])
const error = ref<string | null>(null)

async function cargar() {
  try {
    error.value = null
    reservas.value = await getMisReservas(100) // "todas" por ahora
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  }
}

onMounted(cargar)
</script>

<template>
  <div style="max-width: 1100px; margin: 0 auto; padding: 18px;">
    <h2>Mis reservas</h2>

    <p v-if="error">{{ error }}</p>

    <div v-if="reservas.length">
      <div v-for="r in reservas" :key="r.idActividad" style="border: 1px solid #eee; border-radius: 10px; padding: 12px; margin: 10px 0;">
        <strong>{{ r.actividadNombre }}</strong>
        <div>{{ r.fecha }} · {{ r.horaIni }} - {{ r.horaFin }}</div>
        <div>Estado: {{ r.estado }}</div>
      </div>
    </div>

    <p v-else>No tienes reservas.</p>
  </div>
</template>

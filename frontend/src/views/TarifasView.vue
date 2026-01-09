<script setup lang="ts">
import { onMounted, ref } from "vue"
import { listarTarifas, type Tarifa } from "@/services/tarifas"

const tarifas = ref<Tarifa[]>([])
const error = ref<string | null>(null)

onMounted(async () => {
  try {
    tarifas.value = await listarTarifas()
  } catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  }
})


function clasesGratisLabel(tarifa: Tarifa) {
  if (tarifa.clasesGratisMes >= 999 || tarifa.nombre.toLowerCase().includes("elite")) {
    return "ilimitado"
  }
  return String(tarifa.clasesGratisMes)
}
</script>

<template>
  <main style="padding: 16px">
    <h1>Tarifas</h1>
    <p v-if="error" style="color: red">{{ error }}</p>

    <ul v-else>
      <li v-for="t in tarifas" :key="t.id">
        <strong>{{ t.nombre }}</strong> - {{ t.cuota }} EUR (gratis/mes: {{ clasesGratisLabel(t) }})
      </li>
    </ul>
  </main>
</template>

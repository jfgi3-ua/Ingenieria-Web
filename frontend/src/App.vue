<script setup lang="ts">
import { RouterLink, RouterView } from "vue-router"
</script>

<template>
  <header style="padding: 16px; border-bottom: 1px solid #ddd">
    <nav style="display: flex; gap: 12px">
      <RouterLink to="/tarifas">Tarifas</RouterLink>
      <RouterLink to="/registro">Registro</RouterLink>
      <RouterLink to="/inicio">Pagina de inicio</RouterLink>
    </nav>
  </header>


  <main >
    <RouterView />
  </main>
</template>

<style scoped>
a {
  text-decoration: none;
}
a.router-link-active {
  font-weight: 600;
}
</style>

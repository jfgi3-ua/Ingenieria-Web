<script setup lang="ts">
import { RouterLink, RouterView, useRouter } from "vue-router"
import { useAuthStore } from "@/stores/auth.store"

const auth = useAuthStore()
const router = useRouter()

async function onLogout() {
  await auth.logout()
  await router.replace("/login")
}
</script>

<template>
  <header style="padding: 16px; border-bottom: 1px solid #ddd">
    <nav style="display: flex; gap: 12px">
      <RouterLink to="/registro">Registro</RouterLink>
      <RouterLink to="/inicio">Pagina de inicio</RouterLink>
      <RouterLink v-if="!auth.isAuthenticated" to="/login">Login</RouterLink>
      <button v-else type="button" @click="onLogout">Logout</button>
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

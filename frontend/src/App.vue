<script setup lang="ts">
import { RouterLink, RouterView, useRouter } from "vue-router"
import { useAuthStore } from "@/stores/auth.store"
import Navbar from "./components/navbar.vue"
import AdminNavbar from "./components/AdminNavbar.vue"


const auth = useAuthStore()
const router = useRouter()

async function onLogout() {
  await auth.logout()
  await router.replace("/login")
}
</script>

<template>
  <header style="padding: 16px; border-bottom: 1px solid #ddd">
    <AdminNavbar v-if="auth.isAuthenticated && auth.isAdmin" />
    <Navbar v-else />
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

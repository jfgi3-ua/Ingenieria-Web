<script setup lang="ts">
import { RouterView, useRouter } from "vue-router"
import { onMounted } from "vue"
import { useAuthStore } from "@/stores/auth.store"
import Navbar from "./components/navbar.vue"
import AdminNavbar from "./components/AdminNavbar.vue"

const auth = useAuthStore()
const router = useRouter()

onMounted(async () => {
  await auth.loadSession()
})

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

  <main>
    <RouterView />
  </main>
</template>

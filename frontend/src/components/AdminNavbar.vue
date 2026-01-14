<script setup lang="ts">
import logoFitgymTransparente from "@/assets/logoFitgymTransparente.png"
import { useAuthStore } from "@/stores/auth.store"
import { useRouter } from "vue-router"

const auth = useAuthStore()
const router = useRouter()

async function onLogout() {
  await auth.logout()
  await router.replace("/login")
}
</script>

<template>
  <div class="general-container-navbar">
    <div class="fitgym-logo-container">
      <img :src="logoFitgymTransparente" alt="Imagen del logo de la marca" />
      <p>FitGym</p>
    </div>

    <div class="buttons-container-navbar">
      <RouterLink class="nav-link" active-class="active-a" to="/admin/socios">Socios</RouterLink>
      <RouterLink class="nav-link" active-class="active-a" to="/admin/actividades">Actividades</RouterLink>
      <RouterLink class="nav-link" active-class="active-a" to="/admin/monitores">Monitores</RouterLink>
    </div>

    <div class="user-buttons-container">
      <span>Admin</span>
      <button class="logout-navbar-button" @click="onLogout">Cerrar sesión</button>
    </div>
  </div>
</template>

<style scoped>
.general-container-navbar{
  height: 30px;
  display: flex;
  flex-direction: row;
  position: relative;
  justify-content: space-around;
}

.fitgym-logo-container {
  width: 150px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.fitgym-logo-container > img {
  height: 40px;
}

.buttons-container-navbar {
  width: 500px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-around;
}

.nav-link {
  text-decoration: none;
  color: grey;
}

.nav-link.active-a {
  color: white;
  background-color: #0092B8;
  padding: 10px;
  border-radius: 5px;
}

.nav-link.disabled {
  pointer-events: none;
  opacity: 0.5;
}

.user-buttons-container {
  display: flex;
  flex-direction: row;
  gap: 20px;
  align-items: center;
}

.logout-navbar-button {
  all: unset;
  border-radius: 5px;
  height: 38px;
  width: 100px;
  background-color: rgb(244, 14, 14);
  color: white;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
}

.logout-navbar-button:hover {
  background-color: rgb(184, 5, 5);
}
</style>

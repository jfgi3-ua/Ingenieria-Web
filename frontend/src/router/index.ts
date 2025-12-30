import { createRouter, createWebHistory } from "vue-router"
import { useAuthStore } from "@/stores/auth.store"
import RegistroView from "@/views/RegistroView.vue"
import InicioView from "@/views/InicioView.vue"
import LoginView from "@/views/LoginView.vue"
import ServiciosView from "@/views/ServiciosView.vue"

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/", redirect: "/inicio" },
    { path: "/registro", name: "registro", component: RegistroView },
    { path: "/login", name: "login", component: LoginView },
    { path: "/inicio", name: "inicio", component: InicioView },
    { path: "/servicios", name: "servicios", component: ServiciosView, meta: { requiresAuth: true } },
  ],
})

let sessionLoaded = false

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (!sessionLoaded) {
    await auth.loadSession()
    sessionLoaded = true
  }

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { path: "/login", query: { redirect: to.fullPath, reason: "auth" } }
  }

  if (to.path === "/login" && auth.isAuthenticated) {
    return { path: "/inicio" }
  }

  return true
})

export default router

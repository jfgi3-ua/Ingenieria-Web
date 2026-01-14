import { createRouter, createWebHistory } from "vue-router"
import { useAuthStore } from "@/stores/auth.store"
import RegistroView from "@/views/RegistroView.vue"
import InicioView from "@/views/InicioView.vue"
import LoginView from "@/views/LoginView.vue"
import ServiciosView from "@/views/ServiciosView.vue"
import DetallesActividad from '@/views/DetallesActividad.vue'
import AdminSociosView from "@/views/admin/AdminSociosView.vue"
import AdminActividadesView from "@/views/admin/AdminActividadesView.vue"
import PerfilView from "@/views/PerfilView.vue"

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/", redirect: "/inicio" },
    { path: "/registro", name: "registro", component: RegistroView },
    { path: "/login", name: "login", component: LoginView },
    { path: "/inicio", name: "inicio", component: InicioView },
    { path: "/actividad/:id", name: "detallesActividad", component: DetallesActividad, props: true, meta: { requiresAuth: true } },
    { path: "/servicios", name: "servicios", component: ServiciosView, meta: { requiresAuth: true } },
    { path: "/perfil", name: "perfil", component: PerfilView, meta: { requiresAuth: true } },
    { path: "/servicios", name: "servicios", component: ServiciosView, meta: { requiresAuth: true } },
    { path: "/admin/socios", name: "adminSocios", component: AdminSociosView, meta: { requiresAuth: true, requiresAdmin: true } },
    { path: "/admin/actividades", name: "adminActividades", component: AdminActividadesView , meta: { requiresAuth: true, requiresAdmin: true } },
    { path: "/admin/monitores", name: "adminMonitores", component: () => import("@/views/admin/AdminMonitoresView.vue"), meta: { requiresAuth: true, requiresAdmin: true } }

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

  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return { path: "/inicio" }
  }

  if (to.path === "/login" && auth.isAuthenticated) {
    return { path: "/inicio" }
  }

  return true
})

export default router

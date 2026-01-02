import { createRouter, createWebHistory } from 'vue-router'
import TarifasView from "@/views/TarifasView.vue"
import RegistroView from "@/views/RegistroView.vue"
import InicioView from "@/views/InicioView.vue"
import ServiciosView from "@/views/ServiciosView.vue"
import DetallesActividad from '@/views/DetallesActividad.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/tarifas", name: "tarifas", component: TarifasView },
    { path: "/registro", name: "registro", component: RegistroView },
    { path: "/inicio", name: "inicio", component: InicioView },
    { path: "/servicios", name: "servicios", component: ServiciosView},
    { path: "/actividad/:id", name: "detallesActividad", component: DetallesActividad, props: true }
  ],
})

export default router

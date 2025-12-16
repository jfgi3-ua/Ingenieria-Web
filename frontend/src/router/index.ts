import { createRouter, createWebHistory } from 'vue-router'
import TarifasView from "@/views/TarifasView.vue"
import RegistroView from "@/views/RegistroView.vue"

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/tarifas", name: "tarifas", component: TarifasView },
    { path: "/registro", name: "registro", component: RegistroView },
  ],
})

export default router

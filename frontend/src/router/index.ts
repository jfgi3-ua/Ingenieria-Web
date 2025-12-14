import { createRouter, createWebHistory } from 'vue-router'
import TarifasView from "@/views/TarifasView.vue"

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/tarifas", name: "tarifas", component: TarifasView },
  ],
})

export default router

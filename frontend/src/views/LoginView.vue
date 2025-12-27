<script setup lang="ts">
import { computed, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useAuthStore } from "@/stores/auth.store"

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const form = ref({
  correoElectronico: "",
  contrasena: "",
})

const loading = ref(false)
const error = ref<string | null>(null)
const showPassword = ref(false)

const canSubmit = computed(() => {
  return Boolean(form.value.correoElectronico.trim()) && Boolean(form.value.contrasena.trim())
})

const sessionMessage = computed(() => {
  return route.query.reason === "auth"
    ? "Tu sesion ha expirado o no estas autenticado."
    : null
})

function normalizeErrorMessage(message: string) {
  if (message.includes("401")) return "Credenciales invalidas."
  if (message.includes("403")) return "Tu cuenta esta pendiente de aceptacion."
  if (message.includes("400")) return "Revisa los campos del formulario."
  return message
}

async function onSubmit() {
  if (!canSubmit.value) return
  error.value = null
  loading.value = true
  try {
    await auth.login({
      correoElectronico: form.value.correoElectronico,
      contrasena: form.value.contrasena,
    })
    const redirect = typeof route.query.redirect === "string" ? route.query.redirect : "/inicio"
    await router.replace(redirect)
  } catch (e) {
    const message = e instanceof Error ? e.message : String(e)
    error.value = normalizeErrorMessage(message)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="login">
    <div class="login__inner">
      <header class="login__header">
        <p class="eyebrow">FitGym Club</p>
        <h1>Bienvenido de nuevo</h1>
        <p class="subtitle">Accede a tu cuenta para gestionar tus reservas y tu plan.</p>
      </header>

      <div class="card">
        <div v-if="sessionMessage" class="alert alert--info">
          <strong>Sesion requerida:</strong>
          <p>{{ sessionMessage }}</p>
        </div>

        <div v-if="error" class="alert alert--error">
          <strong>Ha ocurrido un error:</strong>
          <p>{{ error }}</p>
        </div>

        <form class="form" @submit.prevent="onSubmit">
          <label class="field">
            <span>Correo electronico</span>
            <input v-model="form.correoElectronico" type="email" placeholder="tu.email@ejemplo.com" />
          </label>

          <label class="field">
            <span>Contrasena</span>
            <div class="field__input">
              <input
                v-model="form.contrasena"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Minimo 8 caracteres"
              />
              <button type="button" class="icon-btn" @click="showPassword = !showPassword">
                {{ showPassword ? "Ocultar" : "Mostrar" }}
              </button>
            </div>
          </label>

          <button class="btn btn--primary" type="submit" :disabled="!canSubmit || loading">
            {{ loading ? "Iniciando sesion..." : "Iniciar sesion" }}
          </button>
        </form>

        <div class="footer">
          <span>No tienes cuenta?</span>
          <RouterLink to="/registro">Crear cuenta</RouterLink>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.login {
  --brand: #0a8fb2;
  --brand-dark: #0b6f89;
  --bg: #eef5f8;
  --card: #ffffff;
  --text: #1f2933;
  --muted: #6b7b8c;
  --border: #e1e7ee;
  font-family: "Manrope", "Segoe UI", sans-serif;
  color: var(--text);
  background: radial-gradient(circle at top, #f5fbff 0%, #eaf1f5 55%, #e1ebf2 100%);
  min-height: 100vh;
  padding: 48px 16px;
}

.login__inner {
  max-width: 520px;
  margin: 0 auto;
  display: grid;
  gap: 24px;
}

.login__header {
  text-align: center;
}

.eyebrow {
  letter-spacing: 0.2em;
  text-transform: uppercase;
  font-size: 11px;
  color: var(--brand);
  font-weight: 700;
  margin-bottom: 8px;
}

.login__header h1 {
  margin: 0 0 6px;
  font-size: 28px;
}

.subtitle {
  margin: 0;
  color: var(--muted);
}

.card {
  background: var(--card);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
  display: grid;
  gap: 18px;
}

.form {
  display: grid;
  gap: 16px;
}

.field span {
  display: block;
  font-size: 13px;
  font-weight: 600;
}

.field input {
  width: 100%;
  margin-top: 6px;
  padding: 11px 12px;
  border-radius: 10px;
  border: 1px solid var(--border);
  font-size: 14px;
}

.field__input {
  display: flex;
  gap: 8px;
  margin-top: 6px;
}

.field__input input {
  margin-top: 0;
  flex: 1;
}

.icon-btn {
  border: 1px solid var(--border);
  background: #fff;
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 12px;
  cursor: pointer;
}

.btn {
  border-radius: 10px;
  padding: 12px 16px;
  border: none;
  font-weight: 600;
  cursor: pointer;
}

.btn--primary {
  background: var(--brand);
  color: #fff;
}

.btn--primary:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
}

.alert {
  border-radius: 10px;
  padding: 12px 14px;
}

.alert--error {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #991b1b;
}

.alert--info {
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  color: #1e3a8a;
}

.footer {
  display: flex;
  justify-content: center;
  gap: 8px;
  font-size: 13px;
  color: var(--muted);
}

.footer a {
  color: var(--brand-dark);
  font-weight: 600;
  text-decoration: none;
}

@media (max-width: 600px) {
  .login {
    padding: 32px 14px;
  }
}
</style>

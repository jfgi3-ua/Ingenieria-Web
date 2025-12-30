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
    <div class="login__canvas">
      <div class="login__card">
        <aside class="login__panel">
          <div class="login__logo">
            <img src="@/assets/icons/Icon.png" alt="FitGym" />
          </div>
          <h2>Bienvenido a FitGym</h2>
          <p class="login__panel-text">
            Tu transformacion comienza aqui. Accede a tu cuenta y continua tu camino hacia tus objetivos.
          </p>
          <ul class="login__features">
            <li>
              <span class="feature__dot" aria-hidden="true">
                <svg viewBox="0 0 20 20">
                  <path d="M7.7 13.2 4.8 10.4l-1.4 1.4 4.3 4.3L17 6.8l-1.4-1.4z" fill="currentColor" />
                </svg>
              </span>
              <div>
                <strong>Acceso seguro 24/7</strong>
                <p>A tu gimnasio y clases favoritas</p>
              </div>
            </li>
            <li>
              <span class="feature__dot" aria-hidden="true">
                <svg viewBox="0 0 20 20">
                  <path d="M7.7 13.2 4.8 10.4l-1.4 1.4 4.3 4.3L17 6.8l-1.4-1.4z" fill="currentColor" />
                </svg>
              </span>
              <div>
                <strong>Gestion completa</strong>
                <p>Reservas, pagos y perfil en un solo lugar</p>
              </div>
            </li>
            <li>
              <span class="feature__dot" aria-hidden="true">
                <svg viewBox="0 0 20 20">
                  <path d="M7.7 13.2 4.8 10.4l-1.4 1.4 4.3 4.3L17 6.8l-1.4-1.4z" fill="currentColor" />
                </svg>
              </span>
              <div>
                <strong>Datos protegidos</strong>
                <p>Encriptacion SSL 256-bit certificada</p>
              </div>
            </li>
          </ul>
          <p class="login__quote">
            "FitGym ha transformado mi estilo de vida. La app hace que gestionar mi rutina sea super facil."
          </p>
          <span class="login__quote-author">- Maria S., Socia Premium</span>
        </aside>

        <div class="login__form">
          <header>
            <h1>Iniciar Sesion</h1>
            <p>Introduce tus credenciales para acceder</p>
          </header>

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
              <div class="field__input field__input--icon">
                <span class="field__icon" aria-hidden="true">
                  <svg viewBox="0 0 24 24">
                    <path
                      d="M4 6h16a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2zm0 2v.5l8 4.6 8-4.6V8H4zm16 8V9.7l-7.5 4.3a1 1 0 0 1-1 0L4 9.7V16h16z"
                      fill="currentColor"
                    />
                  </svg>
                </span>
                <input v-model="form.correoElectronico" type="email" placeholder="tu@email.com" />
              </div>
            </label>

            <label class="field">
              <span>Contrasena</span>
              <div class="field__input field__input--icon">
                <span class="field__icon" aria-hidden="true">
                  <svg viewBox="0 0 24 24">
                    <path
                      d="M7 10V8a5 5 0 0 1 10 0v2h1a2 2 0 0 1 2 2v7a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h1zm2 0h6V8a3 3 0 1 0-6 0v2z"
                      fill="currentColor"
                    />
                  </svg>
                </span>
                <input
                  v-model="form.contrasena"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="********"
                />
                <button type="button" class="icon-btn" @click="showPassword = !showPassword">
                  <svg v-if="showPassword" viewBox="0 0 24 24" aria-hidden="true">
                    <path
                      d="M3.3 5.3 4.7 3.9 20.1 19.3 18.7 20.7l-2.1-2.1A11.8 11.8 0 0 1 12 20c-6 0-10-5.5-10-8 0-1.7 1.2-3.9 3.2-5.6L3.3 5.3zm7.1 7.1 3.3 3.3a2.8 2.8 0 0 0-3.3-3.3zM12 4c6 0 10 5.5 10 8 0 1.4-.9 3.2-2.4 4.8l-3.4-3.4a4.7 4.7 0 0 0-6.3-6.3L7.6 4.7A11.7 11.7 0 0 1 12 4z"
                      fill="currentColor"
                    />
                  </svg>
                  <svg v-else viewBox="0 0 24 24" aria-hidden="true">
                    <path
                      d="M12 4c6 0 10 5.5 10 8s-4 8-10 8-10-5.5-10-8 4-8 10-8zm0 3.2A4.8 4.8 0 1 0 16.8 12 4.8 4.8 0 0 0 12 7.2zm0 2a2.8 2.8 0 1 1-2.8 2.8A2.8 2.8 0 0 1 12 9.2z"
                      fill="currentColor"
                    />
                  </svg>
                </button>
              </div>
            </label>

            <div class="form__row">
              <label class="remember">
                <input type="checkbox" />
                Recordarme
              </label>
              <button class="link" type="button">Olvidaste tu contrasena?</button>
            </div>

            <button class="btn btn--primary" type="submit" :disabled="!canSubmit || loading">
              {{ loading ? "Iniciando sesion..." : "Iniciar Sesion" }}
              <span class="btn__arrow" aria-hidden="true">→</span>
            </button>
          </form>

          <div class="divider">
            <span>No tienes cuenta?</span>
          </div>

          <RouterLink class="btn btn--ghost" to="/registro">Crear cuenta nueva</RouterLink>

          <footer class="support">
            <p>Necesitas ayuda?</p>
            <div>
              <span>soporte@fitgym.com</span>
              <span class="support__dot">·</span>
              <span>+34 900 123 456</span>
            </div>
          </footer>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.login {
  --teal: #0b8fb4;
  --teal-dark: #067a99;
  --navy: #0d1d30;
  --navy-2: #0a2a3a;
  --card: #ffffff;
  --text: #1f2933;
  --muted: #6b7b8c;
  --border: #e3e9ef;
  font-family: "Manrope", "Segoe UI", sans-serif;
  color: var(--text);
  min-height: 100vh;
  background: radial-gradient(circle at top left, #1d2a40 0%, #0f1e32 35%, #0a2433 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 16px;
}

.login__canvas {
  width: min(1040px, 100%);
}

.login__card {
  display: grid;
  grid-template-columns: 1fr 1fr;
  background: var(--card);
  border-radius: 22px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(5, 12, 22, 0.35);
}

.login__panel {
  background: linear-gradient(160deg, #0b8fb4 0%, #0b7394 100%);
  color: #eaf7fb;
  padding: 40px 36px;
  display: grid;
  gap: 16px;
}

.login__logo {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #eaf7fb;
  display: grid;
  place-items: center;
  overflow: hidden;
}

.login__logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.login__panel h2 {
  margin: 0;
  font-size: 20px;
}

.login__panel-text {
  margin: 0;
  color: rgba(234, 247, 251, 0.85);
  line-height: 1.5;
}

.login__features {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 14px;
}

.login__features li {
  display: grid;
  grid-template-columns: 18px 1fr;
  gap: 12px;
}

.feature__dot {
  width: 18px;
  height: 18px;
  border-radius: 6px;
  background: rgba(234, 247, 251, 0.22);
  display: grid;
  place-items: center;
  margin-top: 6px;
}

.feature__dot svg {
  width: 12px;
  height: 12px;
  color: #eaf7fb;
}

.login__features strong {
  display: block;
  font-size: 13px;
}

.login__features p {
  margin: 2px 0 0;
  font-size: 12px;
  color: rgba(234, 247, 251, 0.8);
}

.login__quote {
  margin: 12px 0 0;
  font-size: 12px;
  color: rgba(234, 247, 251, 0.8);
}

.login__quote-author {
  font-size: 12px;
  color: rgba(234, 247, 251, 0.7);
}

.login__form {
  padding: 40px 42px;
  display: grid;
  gap: 18px;
}

.login__form h1 {
  margin: 0;
  font-size: 22px;
}

.login__form p {
  margin: 4px 0 0;
  color: var(--muted);
}

.form {
  display: grid;
  gap: 16px;
}

.field span {
  display: block;
  font-size: 12px;
  font-weight: 600;
}

.field input {
  width: 100%;
  margin-top: 6px;
  padding: 11px 12px;
  border-radius: 10px;
  border: 1px solid var(--border);
  font-size: 14px;
  background: #f9fbfd;
}

.field__input {
  display: flex;
  gap: 8px;
  margin-top: 6px;
}

.field__input--icon input {
  padding-left: 38px;
}

.field__icon {
  position: absolute;
  width: 18px;
  height: 18px;
  color: #94a3b8;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
}

.field__input {
  position: relative;
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
  display: grid;
  place-items: center;
  width: 38px;
}

.icon-btn svg {
  width: 18px;
  height: 18px;
  color: #64748b;
}

.form__row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--muted);
}

.remember {
  display: flex;
  align-items: center;
  gap: 6px;
}

.link {
  background: none;
  border: none;
  color: var(--teal-dark);
  font-size: 12px;
  cursor: pointer;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  padding: 12px 16px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
}

.btn--primary {
  background: var(--teal);
  color: #fff;
  box-shadow: 0 12px 24px rgba(11, 143, 180, 0.25);
  gap: 10px;
}

.btn--primary:disabled {
  background: #b9d4de;
  cursor: not-allowed;
  box-shadow: none;
}

.btn--ghost {
  border: 1px solid var(--border);
  background: #fff;
  color: var(--text);
}

.divider {
  display: flex;
  justify-content: center;
  font-size: 12px;
  color: var(--muted);
}

.btn__arrow {
  font-size: 16px;
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

.support {
  text-align: center;
  font-size: 12px;
  color: var(--muted);
}

.support div {
  margin-top: 4px;
}

.support__dot {
  margin: 0 6px;
}

@media (max-width: 900px) {
  .login__card {
    grid-template-columns: 1fr;
  }

  .login__panel {
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  }
}

@media (max-width: 600px) {
  .login__form {
    padding: 32px 24px;
  }

  .login__panel {
    padding: 32px 24px;
  }
}
</style>

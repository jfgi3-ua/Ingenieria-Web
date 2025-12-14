# FitGym

## Entorno local (PostgreSQL + pgAdmin/Adminer), Backend (Spring Boot) y Frontend (Vue)

Este repositorio incluye un entorno completo para desarrollo local:

- **PostgreSQL 16** como motor de base de datos (en Docker).
- **pgAdmin 4** como UI principal para inspección/gestión de la BD.
- **Adminer** como alternativa opcional (solo bajo profile).
- **Init scripts** para crear el esquema y cargar datos iniciales.
- **Backend** en **Java + Spring Boot** (API REST) con conexión a la BD en Docker.
- **Frontend** en **Vue 3 + Vite + TypeScript + Router + Pinia** (SPA) consumiendo el backend.

> Objetivo: que cualquier miembro del equipo pueda clonar el repo y ejecutar todo en local de forma reproducible, evitando configuraciones “a mano”.

---

## Requisitos

### Para base de datos (Docker)
- Windows 11 (el script de arranque es PowerShell).
- Docker Desktop instalado y en ejecución.
- Git.
- PowerShell.

Verificación rápida:
````powershell
docker --version
docker compose version
````

### Para backend (Spring Boot)

* **JDK 21** instalado y `JAVA_HOME` configurado (recomendado).
* **Maven 3.9+**

Verificación rápida:

````powershell
java --version
mvn --version
````

### Para frontend (Vue)

* **Node.js 22+** (recomendado: LTS/estable en el equipo)
* **npm 11+**

Verificación rápida:

````powershell
node --version
npm --version
````

---

## Estructura relevante del repositorio

* `docker-compose.yml`
  Define los servicios `db`, `pgadmin` y `adminer` (este último por profile).

* `scripts/dev-up.ps1`
  Script de arranque (Windows). Prepara la ruta de persistencia y levanta contenedores.

* `docker/initdb/01_schema.sql`
  Crea el esquema (tablas, relaciones, enums, índices).

* `docker/initdb/02_seed.sql`
  Inserta datos iniciales (incluye tarifas **Básico / Premium / Élite** y datos de ejemplo).

* `backend/`
  Proyecto Spring Boot (API REST).

* `frontend/`
  Proyecto Vue 3 (SPA) generado con Vite.

---

## Qué levanta Docker Compose

### 1) `db` (PostgreSQL)

* Imagen: `postgres:16`
* Puerto en contenedor: `5432`

#### Puerto local (IMPORTANTE)

En el host **NO usamos 5432**, sino **5433**, para evitar conflictos con PostgreSQL instalado localmente en Windows.

* Host: `localhost:5433` → Contenedor: `5432`

> Motivo: en Windows es habitual tener un servicio PostgreSQL local escuchando en 5432 (por ejemplo `postgresql-x64-9.6` el que instalamos para la práctica de WebRatio). Si Docker intenta publicar 5432, o bien falla el bind, o peor: el backend puede acabar conectando al Postgres local “equivocado”.

#### Persistencia (bind mount)

`C:\Users\<usuario>\Desktop\FitGymData\pgdata` → `/var/lib/postgresql/data`

#### Scripts initdb

`./docker/initdb` → `/docker-entrypoint-initdb.d`
**Solo se ejecutan si `pgdata` está vacío** (primer arranque con data dir vacío).

#### Healthcheck

Usa `pg_isready` para marcar la base de datos como `healthy`.

---

### 2) `pgadmin` (UI por defecto)

* Imagen: `dpage/pgadmin4`
* Puerto local: `5050`
* Arranca cuando `db` está `healthy`.

---

### 3) `adminer` (UI alternativa)

* Imagen: `adminer`
* Puerto local: `8081`
* **No arranca por defecto**: solo con `--profile adminer`

---

## Arranque (recomendado) — pgAdmin por defecto

Ejecuta desde la **raíz del repositorio**:

````powershell
powershell -ExecutionPolicy Bypass -File .\scripts\dev-up.ps1
````

Qué hace el script `dev-up.ps1`:

1. Calcula la ruta real del Escritorio del usuario.
2. Crea:

   * `C:\Users\<usuario>\Desktop\FitGymData`
   * `C:\Users\<usuario>\Desktop\FitGymData\pgdata`
3. Exporta la variable de entorno `FITGYM_DATA_DIR` (solo en la sesión del script).
4. Ejecuta `docker compose up -d` y muestra `docker ps`.

---

## Comprobaciones rápidas (DB)

### 1) Ver contenedores activos

````powershell
docker ps
````

### 2) Verificar datos seeded (Tarifas)

````powershell
docker exec -it fitgym_postgres psql -U fitgym_user -d fitgym -c "SELECT id, nombre FROM tarifa ORDER BY id;"
````

---

## Backend (Spring Boot)

### Arranque del backend

Desde la raíz del repo:

````powershell
cd .\backend
mvn spring-boot:run
````

### Verificación del backend (E2E básico)

Actuator se usa como prueba de vida:

````powershell
curl.exe http://localhost:8080/actuator/health
````

Esperado: HTTP 200 y JSON con `"status":"UP"`.

> Nota: en PowerShell usa `curl.exe` para evitar el alias `Invoke-WebRequest` y su warning.

---

## Frontend (Vue 3 + Vite)

### Por qué esta configuración

El frontend se ha inicializado con:

* **Vite**: servidor de desarrollo rápido y estándar actual en Vue.
* **TypeScript**: reduce errores y mejora el refactor (clave en equipo).
* **Vue Router**: necesario para navegación SPA (login, registro, perfil, reservas, etc.).
* **Pinia**: estado global (auth, usuario, roles, caché).
* **ESLint + Prettier**: consistencia de estilo y menos diffs en PRs.

### Instalación

Desde la raíz del repo:

````powershell
cd .\frontend
npm install
````

### Arranque

````powershell
npm run dev
````

Por defecto Vite sirve en:

* `http://localhost:5173`

---

## Conexión Frontend ↔ Backend (proxy recomendado)

### Motivo (CORS en desarrollo)

En desarrollo, el frontend y el backend corren en puertos distintos:

* Frontend: `http://localhost:5173`
* Backend: `http://localhost:8080`

Eso son **orígenes distintos**, y el navegador aplica CORS. Para evitar configurar CORS “a mano” durante el desarrollo, se usa un **proxy de Vite**.

### Requisito: proxy en `frontend/vite.config.ts`

Asegurar que existe:

````ts
server: {
  proxy: {
    "/api": {
      target: "http://localhost:8080",
      changeOrigin: true,
    },
    "/actuator": {
      target: "http://localhost:8080",
      changeOrigin: true,
    }
  }
}
````

**Regla de oro:** desde Vue se llama a la API con rutas relativas:

* ✅ `fetch("/api/tarifas")`
* ❌ `fetch("http://localhost:8080/api/tarifas")` (propenso a CORS en dev)

---

## Router (rutas) y `App.vue` (imprescindible)

### Por qué no se veía `/tarifas`

En una SPA, el componente raíz debe renderizar el router con un `<RouterView />`.
Si `App.vue` no lo incluye, aunque navegues a `/tarifas` no habrá “contenedor” donde pintar la vista.

### `App.vue` recomendado (mínimo)

Debe contener `RouterView` (y opcionalmente navegación con `RouterLink`).

---

## Validación E2E completa (DB → Backend → Frontend)

### 1) Levantar BD

````powershell
powershell -ExecutionPolicy Bypass -File .\scripts\dev-up.ps1
````

### 2) Levantar backend

````powershell
cd .\backend
mvn spring-boot:run
````

### 3) Levantar frontend

````powershell
cd .\frontend
npm run dev
````

### 4) Probar en navegador

* `http://localhost:5173/tarifas`

Esperado:

* La vista carga datos desde `GET /api/tarifas`
* Se muestran las tarifas iniciales (Básico/Premium/Élite) si el backend expone ese endpoint

---

## Estructura del backend (convención del proyecto) y justificación

A partir de este punto, el backend seguirá una **arquitectura por capas** orientada a **API REST** (consumida por Vue) y a un modelo de dominio persistente en PostgreSQL. Esta estructura busca maximizar mantenibilidad, testabilidad y trabajo en equipo, evitando acoplamientos innecesarios entre la API y la base de datos.

---

### 1) Estructura propuesta de paquetes (convención)

**Raíz del backend:**
````
backend/src/main/java/com/fitgym/backend/
````

**Estructura general:**
````
com.fitgym.backend
  api/
    TarifaController.java
    SocioController.java
    ReservaController.java
    ...

    dto/
      TarifaResponse.java
      TarifaCreateRequest.java
      TarifaUpdateRequest.java
      ...
    error/
      ApiError.java
      GlobalExceptionHandler.java

  service/
    TarifaService.java
    SocioService.java
    ReservaService.java
    ...

  repo/
    TarifaRepository.java
    SocioRepository.java
    ReservaRepository.java
    ...

  domain/
    Tarifa.java
    Socio.java
    Reserva.java
    ...
````

> Nota: la idea es que cada entidad importante (Tarifa, Socio, Reserva, Actividad, etc.) tenga sus clases distribuidas en estas capas siguiendo el mismo patrón.

---

### 2) Responsabilidad de cada capa (qué va y qué no va)

#### 2.1 `domain/` — Modelo persistente (JPA)
- **Qué contiene:** Entidades JPA (`@Entity`) y su mapeo a tablas.
- **Responsabilidad:** representar cómo se guarda el dominio en la BD (columnas, constraints, relaciones).
- **No debe contener:** lógica de HTTP, DTOs, ni detalles del frontend.

**Por qué:** separa el modelo de datos (persistencia) del contrato de la API.

---

#### 2.2 `repo/` — Acceso a datos (repositorios)
- **Qué contiene:** interfaces `Repository` (por ejemplo `extends JpaRepository`), métodos de consulta y especificaciones.
- **Responsabilidad:** obtener/persistir datos. No decide reglas de negocio.
- **No debe contener:** validaciones de negocio (eso va en `service/`) ni respuestas HTTP.

**Por qué:** evita mezclar SQL/queries con reglas del dominio o lógica de endpoints.

---

#### 2.3 `service/` — Casos de uso y reglas de negocio
- **Qué contiene:** clases `Service` con métodos que representan operaciones del negocio:
  - `listarTarifas()`, `crearSocio()`, `reservarActividad()`, etc.
- **Responsabilidad:** reglas, orquestación, transacciones (`@Transactional`), validaciones de negocio.
- **No debe contener:** detalles HTTP (rutas, status codes) ni serialización.

**Por qué:** centraliza la lógica del sistema en un punto testable y reutilizable (un mismo caso de uso puede ser llamado desde distintos endpoints).

---

#### 2.4 `api/` — Capa HTTP (controllers)
- **Qué contiene:** controladores REST (`@RestController`) y configuración web asociada.
- **Responsabilidad:** traducir HTTP ↔ aplicación:
  - leer path/query/body
  - invocar `service`
  - devolver respuesta con código HTTP correcto
- **No debe contener:** queries directas a BD ni reglas complejas.

**Por qué:** evita “controllers gordos” y mantiene el contrato HTTP limpio y consistente.

---

#### 2.5 `api/dto/` — Contrato de la API (DTOs)
- **Qué contiene:** clases para request/response:
  - `TarifaResponse`, `TarifaCreateRequest`, etc.
- **Responsabilidad:** definir explícitamente qué campos se exponen y cómo.
- **Por qué es recomendable incluso si al principio parece redundante:**
  - evita acoplar el frontend a la entidad JPA
  - evita problemas de serialización (relaciones lazy, ciclos)
  - reduce exposición accidental de campos
  - facilita evolucionar la BD sin romper la API

> En resumen: **la entidad JPA es “modelo de BD”** y el DTO es **“modelo de API”**.

---

### 3) Beneficios y por qué esta es la mejor estructura para el proyecto

#### 3.1 Menos acoplamiento BD ↔ API
Cambios en el esquema (nombres de columnas, relaciones, normalización) no deben romper el frontend.
- Con DTOs, el contrato público puede mantenerse estable aunque el dominio evolucione.

#### 3.2 Mejor testabilidad
- `service/`: tests unitarios rápidos (sin levantar servidor).
- `repo/`: tests de persistencia (con H2 o Postgres con Testcontainers).
- `api/`: tests de controlador (MockMvc), verificando status codes y formato JSON.

Esto permite detectar errores temprano y mantener estabilidad a medida que crece el sistema.

#### 3.3 Trabajo en equipo sin pisarse
Cada miembro puede trabajar de forma más independiente:
- uno en API/DTOs
- otro en reglas de negocio
- otro en queries
Con menos conflictos en Git y menos efectos colaterales.

#### 3.4 Escalabilidad del proyecto
FitGym crecerá (registro, reservas, pagos, perfiles, actividades, etc.). Tener esta convención desde el inicio evita refactors costosos.

---

### 4) Convenciones adicionales recomendadas (para coherencia)

#### 4.1 Rutas REST (convención)
- Recursos en plural:
  - `/api/tarifas`
  - `/api/socios`
  - `/api/reservas`
- Operaciones estándar:
  - `GET /api/tarifas` (listar)
  - `GET /api/tarifas/{id}` (detalle)
  - `POST /api/tarifas` (crear)
  - `PUT /api/tarifas/{id}` (editar)
  - `DELETE /api/tarifas/{id}` (borrar)

#### 4.2 Errores consistentes
Centralizaremos errores HTTP con un `GlobalExceptionHandler` en `api/error/` para devolver un formato uniforme (útil para Vue).

#### 4.3 CORS (frontend Vue)
Cuando el frontend esté en `http://localhost:5173`, habrá que habilitar CORS (preferible configuración global).

---

### 5) Ejemplo aplicado a `Tarifa` (mapa de archivos)

````
domain/
  Tarifa.java

repo/
  TarifaRepository.java

service/
  TarifaService.java

api/
  TarifaController.java
  dto/
    TarifaResponse.java
````

---

## Troubleshooting (Frontend)

### 1) La ruta existe pero no se muestra nada

Causa habitual: `App.vue` no tiene `<RouterView />` o `main.ts` no está usando el router.

Checklist:

* `frontend/src/main.ts` debe tener: `createApp(App).use(router).mount("#app")`
* `App.vue` debe renderizar: `<RouterView />`

### 2) Error en Network: `/api/...` devuelve 404

* El backend no tiene ese endpoint, o
* el proxy no está bien configurado.

### 3) Error de CORS

Si ves “blocked by CORS policy”, revisa:

* que el proxy en `vite.config.ts` existe
* que estás llamando a `/api/...` (no a `http://localhost:8080/...`)

---

## Comandos resumen (copy/paste)

Levantar BD:

````powershell
powershell -ExecutionPolicy Bypass -File .\scripts\dev-up.ps1
````

Arrancar backend:

````powershell
cd .\backend
mvn spring-boot:run
````

Health backend:

````powershell
curl.exe http://localhost:8080/actuator/health
````

Arrancar frontend:

````powershell
cd .\frontend
npm install
npm run dev
````


## Convenciones del Frontend (Vue 3) — Estructura, organización y normas de equipo

Esta sección define el patrón que seguiremos en el frontend para que el proyecto sea mantenible, escalable y consistente entre todos los miembros del equipo.

### 1) Objetivos de estas convenciones
- Evitar “cada uno lo hace a su manera” (menos fricción en PRs).
- Separar claramente **vistas**, **componentes**, **estado**, **servicios HTTP** y **tipos**.
- Facilitar el testing (unit y e2e) y el refactor.
- Mantener un contrato consistente con el backend.

---

## 2) Estructura recomendada de `src/`

````
frontend/src/
  assets/              # Recursos estáticos (imágenes, iconos, etc.)
  components/          # Componentes reutilizables (UI y piezas comunes)
  common/              # Botones, inputs, modales genéricos, etc.
  layout/              # Navbar, footer, wrappers de layout
  views/               # Vistas (páginas) asociadas a rutas (Router)
    TarifasView.vue
    LoginView.vue
    RegistroView.vue
    PerfilView.vue
    ...
  router/
  index.ts            # Definición de rutas y guards
  stores/             # Pinia stores (auth, user, etc.)
    auth.store.ts
    user.store.ts
    ...
  services/           # Acceso a API (HTTP) y lógica de comunicación
    http.ts           # Cliente HTTP base (fetch/axios)
    tarifas.service.ts
    socios.service.ts
    reservas.service.ts
    ...
  types/               # Tipos TypeScript compartidos (DTOs frontend)
    tarifa.ts
    socio.ts
    reserva.ts
  utils/               # Helpers sin estado (formatters, validators, etc.)
  App.vue
  main.ts
````

### Por qué esta estructura es la recomendada
- **`views/`**: una vista = una ruta. Evita mezclar páginas con componentes reutilizables.
- **`components/`**: piezas reusables y desacopladas de navegación.
- **`services/`**: centraliza llamadas HTTP; si cambia la API, se toca aquí, no en 30 componentes.
- **`stores/`**: estado global (auth, usuario, carrito/reserva, etc.) en Pinia (no en componentes sueltos).
- **`types/`**: el frontend tipa el contrato que consume; reduce errores y mejora DX.

---

## 3) Router: reglas básicas

### 3.1 Rutas en `router/index.ts`
- Todas las rutas viven en `frontend/src/router/index.ts`.
- Las vistas se importan desde `views/`.

Ejemplo:
````ts
{
  path: "/tarifas",
  name: "tarifas",
  component: () => import("@/views/TarifasView.vue"),
}
````

### 3.2 Guards (cuando exista auth)

* Si una ruta requiere autenticación, se controla con guards.
* La fuente de verdad de auth será `stores/auth.store.ts`.

---

## 4) Componentes vs Vistas (regla práctica)

### Vistas (`views/`)

* Orquestan datos y composición de la página.
* Hacen llamadas a `services/` (directamente o vía store).
* Pueden usar varios componentes.

### Componentes (`components/`)

* Preferiblemente sin conocimiento de rutas (no dependen del Router).
* Presentacionales y reutilizables.
* Reciben datos vía `props` y comunican eventos vía `emit`.

**Regla de oro:**

> “Lo que se navega es una vista. Lo que se reutiliza es un componente.”

---

## 5) Servicios HTTP (norma de equipo)

### 5.1 Siempre llamar a la API con rutas relativas `/api/...`

Durante desarrollo usamos proxy de Vite para evitar CORS (Cross-Origin Resource Sharing):

* ✅ `fetch("/api/tarifas")`
* ❌ `fetch("http://localhost:8080/api/tarifas")`

### 5.2 Cliente HTTP centralizado (`services/http.ts`)

Todas las llamadas HTTP deben pasar por un cliente común para:

* centralizar headers
* parseo de errores
* autenticación futura (Authorization)
* timeouts/reintentos si hiciera falta

Ejemplo base con `fetch`:

````ts
export async function apiGet<T>(path: string): Promise<T> {
  const res = await fetch(path, { headers: { Accept: "application/json" } })
  if (!res.ok) throw new Error(`GET ${path} failed: ${res.status}`)
  return (await res.json()) as T
}
````

### 5.3 Servicios por dominio

Cada recurso del backend tiene su fichero de servicio:

* `services/tarifas.service.ts`
* `services/socios.service.ts`
* `services/reservas.service.ts`

Ejemplo:

````ts
import { apiGet } from "./http"
import type { Tarifa } from "@/types/tarifa"

export function listarTarifas() {
  return apiGet<Tarifa[]>("/api/tarifas")
}
````

---

## 6) Tipos (DTOs frontend) — `types/`

Los tipos TypeScript representan lo que **consumimos** del backend (DTOs), no necesariamente las entidades internas del backend.

Ejemplo `types/tarifa.ts`:

````ts
export type Tarifa = {
  id: number
  nombre: string
  cuota: number
  descripcion?: string | null
  clasesGratisMes: number
}
````

**Por qué:**

* Evita “any” y bugs por propiedades mal escritas.
* Si cambia el backend, TypeScript ayuda a detectar qué rompe.

---

## 7) Pinia Stores (estado global)

### 7.1 Qué va en un store

* Estado que se comparte entre vistas/componentes:

  * usuario logueado
  * token (si hay JWT)
  * rol/es_admin
  * filtros globales
  * datos cacheados (si procede)

### 7.2 Qué NO va en un store

* Estado local de un formulario concreto (mejor en la vista).
* Component state efímero que no se comparte.

**Regla práctica:**

> Si solo lo usa una vista, no lo subas a store.

---

## 8) Estilo y calidad (ESLint/Prettier)

* Respetar `npm run lint` antes de abrir PR.
* Evitar commits con formato inconsistente.
* Si se añade una dependencia, documentar el motivo.

---

## 9) Convenciones de nomenclatura

* Vistas: `PascalCaseView.vue` (ej. `TarifasView.vue`)
* Componentes: `PascalCase.vue` (ej. `TarifaCard.vue`)
* Servicios: `kebab` o `dot` consistente (recomendado `tarifas.service.ts`)
* Stores: `xxx.store.ts` (ej. `auth.store.ts`)
* Types: `lowercase` por entidad (ej. `tarifa.ts`, `socio.ts`)

---

## 10) Checklist para nuevas funcionalidades

Cuando se añade una nueva “pantalla” o feature:

1. Crear/actualizar ruta en `router/index.ts`
2. Crear vista en `views/`
3. Crear servicio en `services/` (si consume API)
4. Crear tipos en `types/`
5. Si hay estado compartido, crear/actualizar store en `stores/`
6. Añadir componentes reutilizables en `components/`
7. Probar manualmente (navegación + requests)
8. Ejecutar:

   * `npm run lint`
   * `npm run test:unit` (si está habilitado)

---

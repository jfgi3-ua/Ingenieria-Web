# FitGym

## Entorno local (PostgreSQL + pgAdmin/Adminer) y Backend (Spring Boot)

Este repositorio incluye un entorno completo para desarrollo local:

- **PostgreSQL 16** como motor de base de datos (en Docker).
- **pgAdmin 4** como UI principal para inspección/gestión de la BD.
- **Adminer** como alternativa opcional (solo bajo profile).
- **Init scripts** para crear el esquema y cargar datos iniciales.
- **Backend** en **Java + Spring Boot** (API REST) con conexión a la BD en Docker.

> Objetivo: que cualquier miembro del equipo pueda clonar el repo y tener el backend funcionando en local de forma reproducible, sin depender de instalaciones manuales de PostgreSQL.

---

## Requisitos

### Para base de datos (Docker)
- Windows 11 (el script de arranque es PowerShell).
- Docker Desktop instalado y en ejecución.
- Git.
- PowerShell.

Verificación rápida:
```powershell
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

## Validar el docker-compose

Para validar sintaxis/configuración:

````powershell
docker compose config
````

---

## Acceso a pgAdmin

1. Abre: `http://localhost:5050`

2. Credenciales (definidas en `docker-compose.yml`):

   * Email: `admin@ua.com`
   * Password: `admin1234`

3. Conectar pgAdmin a la base de datos (Add New Server):

   **Si usas pgAdmin en contenedor (el de docker-compose):**

   * Host name/address: `db`
   * Port: `5432`
   * Maintenance database: `fitgym`
   * Username: `fitgym_user`
   * Password: `fitgym_pass`

   **Si usas pgAdmin instalado en local (Windows):**

   * Host name/address: `localhost`
   * Port: `5433`
   * Maintenance database: `fitgym`
   * Username: `fitgym_user`
   * Password: `fitgym_pass`

> Importante: dentro de la red de Docker se usa `db:5432`. En el host Windows se usa `localhost:5433`.

---

## Adminer (opcional)

Para levantar Adminer:

````powershell
docker compose --profile adminer up -d
````

Acceso:

* `http://localhost:8081`

Datos de conexión:

* System: `PostgreSQL`
* Server: `db`
* Username: `fitgym_user`
* Password: `fitgym_pass`
* Database: `fitgym`

---

## Comprobaciones rápidas (DB)

### 1) Ver contenedores activos

````powershell
docker ps
````

### 2) Logs de PostgreSQL

````powershell
docker logs fitgym_postgres --tail 100
````

### 3) Healthcheck (debe ser `healthy`)

````powershell
docker inspect fitgym_postgres --format "{{json .State.Health}}"
````

### 4) Prueba SQL rápida

````powershell
docker exec -it fitgym_postgres psql -U fitgym_user -d fitgym -c "SELECT now();"
````

### 5) Verificar datos seeded (Tarifas)

````powershell
docker exec -it fitgym_postgres psql -U fitgym_user -d fitgym -c "SELECT id, nombre FROM tarifa ORDER BY id;"
````

---

## Inicialización del esquema y datos (initdb)

Los scripts en `docker/initdb/` se ejecutan automáticamente **solo la primera vez**, cuando el directorio persistente `pgdata` está vacío.

* `01_schema.sql`: crea tablas, relaciones, enums e índices.
* `02_seed.sql`: carga datos iniciales (tarifas y datos demo).

> Si `pgdata` ya tiene datos, **no se volverán a ejecutar** automáticamente estos scripts.

---

## Backend (Spring Boot)

### Por qué esta configuración

* Spring Boot + Maven estandarizan compilación, ejecución y dependencias para todo el equipo.
* PostgreSQL en Docker evita diferencias entre máquinas (versiones/config).
* Se usa **puerto 5433** en el host para evitar conflictos con PostgreSQL instalado localmente.

### Configuración de conexión

El backend está configurado para conectar a la BD de Docker usando:

* Host: `localhost`
* Puerto: `5433`
* DB: `fitgym`
* Usuario: `fitgym_user`
* Password: `fitgym_pass`

(Ver `backend/src/main/resources/application.yaml`.)

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

## Operaciones habituales

### Parar el entorno (sin borrar datos)

````powershell
docker compose down
````

### Eliminar una tabla concreta (ejemplo)

Desde pgAdmin (Query Tool) o `psql`:

````sql
DROP TABLE IF EXISTS socio CASCADE;
````

### Reset “lógico” (vaciar esquema, sin tocar persistencia)

````sql
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO fitgym_user;
GRANT ALL ON SCHEMA public TO public;
````

> Nota: esto **no** relanza automáticamente `initdb`.

### Reset “completo” (re-ejecutar initdb)

1. Parar contenedores:

````powershell
docker compose down
````

2. Borrar el contenido de:

* `C:\Users\<usuario>\Desktop\FitGymData\pgdata`

3. Volver a levantar:

````powershell
powershell -ExecutionPolicy Bypass -File .\scripts\dev-up.ps1
````

---

## Troubleshooting

### pgAdmin no arranca por email inválido

pgAdmin valida `PGADMIN_DEFAULT_EMAIL`. Evita dominios “reservados” (por ejemplo, algunos `.local`). En este repo se usa `admin@ua.com`.

### Puerto 5432 ocupado (PostgreSQL local)

Si tienes PostgreSQL instalado en Windows, puede estar escuchando en 5432. Este repo usa **5433** para Docker por defecto.

Diagnóstico:

````powershell
netstat -ano | findstr :5432
Get-Process -Id <PID>
Get-Service | Where-Object { $_.Name -match "postgres" -or $_.DisplayName -match "PostgreSQL" }
````

Ver puerto real del contenedor:

````powershell
docker port fitgym_postgres 5432
````

### Rutas relativas (initdb) no encontradas

Ejecuta el script desde la **raíz del repo** para que `./docker/initdb` resuelva correctamente.

### Error típico en backend: “password authentication failed for user ‘fitgym_user’”

Causas habituales:

* El backend conecta a la instancia equivocada (Postgres local en 5432).
* El contenedor fue inicializado con otras credenciales y se mantiene por el volumen.

Acciones:

1. Asegura que la URL usa el puerto correcto `5433`.
2. Valida credenciales desde el contenedor:

````powershell
docker exec -e PGPASSWORD=fitgym_pass -it fitgym_postgres psql -h 127.0.0.1 -U fitgym_user -d fitgym -c "SELECT 1;"
````

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

Health:

````powershell
curl.exe http://localhost:8080/actuator/health
````

Ver tarifas:

````powershell
docker exec -it fitgym_postgres psql -U fitgym_user -d fitgym -c "SELECT id, nombre FROM tarifa ORDER BY id;"
````


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

Este patrón se repetirá para el resto de entidades del proyecto.


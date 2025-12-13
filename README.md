# FitGym

## Entorno local con Docker (PostgreSQL + pgAdmin / Adminer)

Este repositorio incluye un entorno de base de datos listo para desarrollo local usando **Docker Compose**:

- **PostgreSQL 16** como motor de base de datos.
- **pgAdmin 4** como UI principal para inspección/gestión de la BD.
- **Adminer** como alternativa opcional (solo bajo profile).
- **Init scripts** para crear el esquema y cargar datos iniciales.

---

### Requisitos

- Windows 11 (el script de arranque es PowerShell).
- Docker Desktop instalado y en ejecución.
- Git (para clonar el repo).
- PowerShell.

Verificación rápida:
```powershell
docker --version
docker compose version
````

---

### Estructura relevante del repositorio

* `docker-compose.yml`
  Define los servicios `db`, `pgadmin` y `adminer` (este último por profile).

* `scripts/dev-up.ps1`
  Script de arranque (Windows). Prepara la ruta de persistencia y levanta los contenedores.

* `docker/initdb/01_schema.sql`
  Crea el esquema (tablas, relaciones, enums, índices).

* `docker/initdb/02_seed.sql`
  Inserta datos iniciales (incluye tarifas **Básico / Premium / Élite** y datos de ejemplo).

---

## Qué levanta Docker Compose

#### 1) `db` (PostgreSQL)

* Imagen: `postgres:16`
* Puerto local: `5432`
* Persistencia en host (bind mount):
  `C:\Users\<usuario>\Desktop\FitGymData\pgdata` → `/var/lib/postgresql/data`
* Scripts initdb:
  `./docker/initdb` → `/docker-entrypoint-initdb.d` (solo se ejecutan si `pgdata` está vacío)
* Healthcheck:
  usa `pg_isready` para marcar la base de datos como `healthy`.

#### 2) `pgadmin` (UI por defecto)

* Imagen: `dpage/pgadmin4`
* Puerto local: `5050`
* Arranca cuando `db` está `healthy`.

#### 3) `adminer` (UI alternativa)

* Imagen: `adminer`
* Puerto local: `8081`
* **No arranca por defecto**: solo con `--profile adminer`
* **O desde la raíz del proyecto** usando Powershell:
````powershell
-ExecutionPolicy Bypass -File .\scripts\dev-up.ps1 -Ui adminer
````

---

### Arranque (recomendado) — pgAdmin por defecto

Ejecuta desde la **raíz del repositorio**:

``powershell
powershell -ExecutionPolicy Bypass -File .\scripts\dev-up.ps1
``

Qué hace el script `dev-up.ps1`:

1. Calcula la ruta real del Escritorio del usuario.
2. Crea:
   * `C:\Users\<usuario>\Desktop\FitGymData`
   * `C:\Users\<usuario>\Desktop\FitGymData\pgdata`
3. Exporta la variable de entorno `FITGYM_DATA_DIR` (solo en la sesión del script).
4. Ejecuta `docker compose up -d` y muestra `docker ps`.

---

### Validar el docker-compose

Para validar sintaxis/configuración:

``powershell
docker compose config
``

---

### Acceso a pgAdmin

1. Abre:
   `http://localhost:5050`

2. Credenciales (definidas en `docker-compose.yml`):

   * Email: `admin@ua.com`
   * Password: `admin1234`

3. Conectar pgAdmin a la base de datos (Add New Server):

   * **Host name/address**: `db`
   * **Port**: `5432`
   * **Maintenance database**: `fitgym`
   * **Username**: `fitgym_user`
   * **Password**: `fitgym_pass`

> Importante: dentro de la red de Docker, el servidor Postgres se referencia por el nombre del servicio: `db` (no `localhost`).

---

### Adminer (opcional)

Para levantar Adminer además de los servicios por defecto:

````powershell
docker compose --profile adminer up -d
````

O también:

````powershell
-ExecutionPolicy Bypass -File .\scripts\dev-up.ps1 -Ui adminer
````

Acceso:

* `http://localhost:8081`

Datos de conexión (en Adminer):

* System: `PostgreSQL`
* Server: `db`
* Username: `fitgym_user`
* Password: `fitgym_pass`
* Database: `fitgym`

---

### Comprobaciones de funcionamiento

#### 1) Ver contenedores activos

````powershell
docker ps
````

#### 2) Logs de PostgreSQL

````powershell
docker logs fitgym_postgres --tail 100
````

#### 3) Healthcheck (debe ser `healthy`)

````powershell
docker inspect fitgym_postgres --format "{{json .State.Health}}"
````

#### 4) Prueba SQL rápida

````powershell
docker exec -it fitgym_postgres psql -U fitgym_user -d fitgym -c "SELECT now();"
````

---

### Inicialización del esquema y datos (initdb)

#### Cómo funciona

Los scripts en `docker/initdb/` se ejecutan automáticamente **solo la primera vez**, cuando el directorio persistente `pgdata` está vacío (es decir, cuando Postgres aún no ha inicializado el clúster).

* `01_schema.sql`: crea tablas, relaciones, enums e índices.
* `02_seed.sql`: carga datos iniciales (tarifas y datos demo).

#### Importante

Si el directorio `pgdata` ya tiene datos, **no se volverán a ejecutar** automáticamente estos scripts.

---

### Operaciones habituales

#### Parar el entorno (sin borrar datos)

````powershell
docker compose down
````

#### Eliminar una tabla concreta (ejemplo)

Desde pgAdmin (Query Tool) o `psql` desde entorno CLI de Docker:

````sql
DROP TABLE IF EXISTS socio CASCADE;
````

#### Reset “lógico” (vaciar esquema, sin tocar persistencia)

Ejecuta:

````sql
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO fitgym_user;
GRANT ALL ON SCHEMA public TO public;
````

Nota: esto **no** relanza automáticamente `initdb`.

#### Reset “completo” (re-ejecutar initdb)

1. Parar contenedores:

````powershell
docker compose down
````

2. Borrar el contenido de:

* `C:\Users\<usuario>\Desktop\FitGymData\pgdata`

3. Volver a levantar desde Powershell en la raíz del proyecto:

````powershell
-ExecutionPolicy Bypass -File .\scripts\dev-up.ps1
````

---

### Troubleshooting

### pgAdmin no arranca por email inválido

pgAdmin valida `PGADMIN_DEFAULT_EMAIL`. Evita dominios “reservados” (por ejemplo, algunos `.local`). En este repo se usa `admin@ua.com`.

#### Puertos ocupados

Si `5432` o `5050` están ocupados, cambia el mapeo en `docker-compose.yml`:

* Ejemplo: `15432:5432` o `15050:80`.

#### Rutas relativas (initdb) no encontradas

Ejecuta el script desde la **raíz del repo** para que `./docker/initdb` resuelva correctamente.


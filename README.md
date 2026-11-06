# Franchise API

API para manejar **franquicias**, sus **sucursales** y los **productos** de cada sucursal (con su stock).

```
Franquicia  →  tiene sucursales  →  cada sucursal tiene productos (nombre + stock)
```

## Qué hace

- Crea franquicias, agrega sucursales a una franquicia y productos a una sucursal.
- Elimina un producto y modifica su stock.
- Muestra el producto con más stock de **cada sucursal** de una franquicia.
- Cambia el nombre de una franquicia, una sucursal o un producto.
- Lista las franquicias y muestra una franquicia completa (esto es un extra mío: sirve para recuperar los ids que se usan en las rutas).

## Con qué la hice

| Qué | Para qué |
|---|---|
| Java 17 + Spring Boot 4 | El proyecto base |
| Spring WebFlux | Programación reactiva (`Mono` y `Flux`) |
| DynamoDB (AWS SDK v2, cliente asíncrono) | Guardar los datos |
| JUnit 5, Mockito y StepVerifier | Pruebas unitarias |
| Docker y Docker Compose | Empaquetar la API y correrla con DynamoDB Local |
| CloudFormation | Crear la tabla de DynamoDB como código (IaC) |

## Cómo correrla (lo más fácil: con Docker)

Solo necesitas **Docker Desktop** abierto y **Git**. No necesitas cuenta de AWS ni instalar Java.

**1. Clona el repositorio**

```bash
git clone https://github.com/maria8527/franchise-api.git
cd franchise-api
```

**2. Levanta todo**

```bash
docker compose up --build
```

La primera vez tarda unos minutos porque descarga imágenes y dependencias. Esto levanta tres cosas:

1. **DynamoDB Local**: una base DynamoDB que corre en tu computador (puerto 8000).
2. Un contenedor que **crea la tabla** `franchises` y termina.
3. **La API** (puerto 8080), que arranca cuando la tabla ya existe.

Cuando veas `Started FranchiseApiApplication` en la consola, ya está lista.

**3. Pruébala**

Abre en el navegador `http://localhost:8080/franchises`. Debe mostrar `[]` (todavía no hay franquicias).

Para probar todo de una vez, importa en Postman la colección `docs/franchise-api.postman_collection.json` y ejecútala con **Run collection**. Hace el flujo completo (crear, consultar, actualizar, eliminar y casos de error) y marca en verde lo que pasa.

**4. Apágala**

```bash
docker compose down
```

> Los datos de DynamoDB Local viven en memoria, así que se borran al apagar.

## Otras formas de correrla

Hay tres maneras de correr la API. La de Docker (la de arriba) es la más fácil. Las otras dos son por si quieres correrla desde tu editor o ver los datos en AWS.

| | La API corre en | La base de datos está en | Necesitas | Cuándo la uso |
|---|---|---|---|---|
| **A. Docker** (la de arriba) | Docker | DynamoDB Local (en Docker) | Docker Desktop | Para probarla rápido, sin instalar nada más |
| **B. Terminal o IDE** | Tu computador | DynamoDB Local (en Docker) | Java 17 y Docker | Para correrla o depurarla desde el IDE, sin AWS |
| **C. AWS real** | Tu computador | DynamoDB en AWS | Java 17, cuenta de AWS y AWS CLI | Para ver los datos guardados en la nube |

### Cómo sabe la API a qué base conectarse

Mira una sola configuración: `aws.dynamodb.endpoint`.

- Si tiene una dirección (por ejemplo `http://localhost:8000`), se conecta a **esa base local**.
- Si está **vacía**, se conecta a **DynamoDB en AWS**.

El **perfil `local`** es solo un interruptor que llena esa dirección con `http://localhost:8000`. Entonces:

- **Con** `-Dspring-boot.run.profiles=local` → usa la base local.
- **Sin** ese parámetro → usa AWS.

### B. Desde la terminal o el IDE, con DynamoDB Local

Necesitas Java 17 y Docker.

**1.** Levanta solo la base de datos y la tabla (sin la API):

```bash
docker compose up dynamodb-local dynamodb-init
```

**2.** En otra terminal, corre la API con el perfil `local`:

```powershell
# Windows (PowerShell)
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

```bash
# Mac / Linux
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Si prefieres IntelliJ: *Run → Edit Configurations* y en el campo **Active profiles** escribe `local`.

### C. Con DynamoDB real en AWS

Necesitas una cuenta de AWS y la **AWS CLI** configurada (`aws configure`, con región `us-east-1`).

**1. Crea la tabla en AWS** (se hace una sola vez):

```bash
aws cloudformation deploy --template-file iac/dynamodb-table.yaml --stack-name franchise-api-dynamodb --region us-east-1
```

Si la tabla ya existe, dirá `No changes to deploy`. Es normal: el comando se puede repetir sin problema.

**2. Corre la API sin el perfil `local`**, para que use AWS:

```powershell
.\mvnw.cmd spring-boot:run
```

Las credenciales las toma de tu configuración de AWS CLI. No hay ninguna clave guardada en el proyecto.

**3. Cuando termines, borra la tabla:**

```bash
aws cloudformation delete-stack --stack-name franchise-api-dynamodb --region us-east-1
```
## Endpoints

Las rutas siguen la jerarquía: franquicia → sucursal → producto. Los `{id}` los devuelven las peticiones de creación y de consulta.

| Qué hace | Método y ruta | Cuerpo |
|---|---|---|
| Crear franquicia | `POST /franchises` | `{"name": "..."}` |
| Listar franquicias | `GET /franchises` | |
| Ver una franquicia completa | `GET /franchises/{franchiseId}` | |
| Cambiar nombre de franquicia | `PATCH /franchises/{franchiseId}` | `{"name": "..."}` |
| **Producto con más stock por sucursal** | `GET /franchises/{franchiseId}/top-stock-products` | |
| Agregar sucursal | `POST /franchises/{franchiseId}/branches` | `{"name": "..."}` |
| Cambiar nombre de sucursal | `PATCH /franchises/{franchiseId}/branches/{branchId}` | `{"name": "..."}` |
| Agregar producto | `POST /franchises/{franchiseId}/branches/{branchId}/products` | `{"name": "...", "stock": 10}` |
| Eliminar producto | `DELETE /franchises/{franchiseId}/branches/{branchId}/products/{productId}` | |
| Modificar stock | `PATCH /franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` | `{"stock": 25}` |
| Cambiar nombre de producto | `PATCH /franchises/{franchiseId}/branches/{branchId}/products/{productId}` | `{"name": "..."}` |

**Sobre el top de stock:** devuelve un producto por sucursal. Si hay empate, gana el primero que se encuentra, y las sucursales sin productos no aparecen.

## Cómo está organizado el código

Usé **Clean Architecture**: el negocio no depende de Spring ni de DynamoDB. Las dependencias solo apuntan hacia adentro.

```
src/main/java/com/trujillo/franchise_api
├── domain          El negocio: modelos (Franchise, Branch, Product) y la interfaz FranchiseRepository
├── application     Los casos de uso: una clase por acción (AddBranchUseCase, etc.)
└── infrastructure  Lo técnico: controllers, DTOs, adaptador de DynamoDB y configuración

iac/                CloudFormation de la tabla DynamoDB
docs/               Colección de Postman
Dockerfile, docker-compose.yml
```

El camino de una petición:

```
Controller  →  Caso de uso  →  FranchiseRepository (interfaz, en domain)
                                        ▲
                                        └── DynamoFranchiseRepository (en infrastructure)
```

Como los casos de uso solo conocen la interfaz, podría cambiar DynamoDB por otra base escribiendo un adaptador nuevo, sin tocar el negocio.

## Despliegue en la nube

- **Base de datos:** la tabla de DynamoDB se crea en AWS con CloudFormation (`iac/dynamodb-table.yaml`). La API se probó conectada a esa tabla real.
- **La API:** la ejecuto en mi máquina (con Docker o con Maven) y se conecta a la tabla en AWS. **No la desplegué como servicio en la nube.**

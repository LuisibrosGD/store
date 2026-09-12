# Store

Store es una aplicación web de comercio electrónico compuesta por una SPA en JavaScript y una API REST en Spring Boot. El sistema reúne catálogo, autenticación, carrito, órdenes y administración en una misma experiencia, con persistencia en MySQL y autorización mediante JWT.

## Funcionalidad

La experiencia pública permite consultar el catálogo, buscar y filtrar productos, recorrer categorías jerárquicas y mantener un carrito local sin iniciar sesión. El registro crea cuentas con rol `CLIENTE`; al iniciar sesión, el carrito del visitante se sincroniza con el carrito persistido del usuario.

Los usuarios autenticados cuentan con perfil, carrito remoto, checkout, historial de órdenes y cancelación de órdenes propias pendientes. Las cuentas con rol `ADMIN` disponen además de áreas para gestionar productos, stock, categorías, usuarios y estados de órdenes.

## Arquitectura

```text
Navegador
   │
   ├── SPA HTML/CSS/JavaScript ── puerto 4173
   │            │
   │            └── /api/** ── proxy local
   │                           │
   └───────────────────────────┴── API Spring Boot ── puerto 8080
                                           │
                                           └── Spring Data JPA ── MySQL 8
```

El backend organiza cada dominio en entidad, repositorio, servicio, controlador y DTO. Los controladores exponen el contrato HTTP, los servicios concentran las reglas de negocio y los repositorios gestionan la persistencia.

## Frontend

El frontend es una SPA escrita con HTML semántico, CSS y módulos JavaScript ES6. No utiliza frameworks, paquetes externos, bundler ni proceso de compilación.

Sus características principales son:

- Navegación con History API y rutas para catálogo, detalle, carrito, autenticación, perfil y órdenes.
- Catálogo paginado con búsqueda por nombre y filtros combinados de categoría y precio.
- Carrito de visitante almacenado en `localStorage` y carrito autenticado sincronizado con la API.
- Sesión JWT almacenada en `sessionStorage`, por lo que su alcance corresponde a la pestaña actual.
- Panel administrativo separado para productos, categorías, usuarios y órdenes.
- Diseño adaptable, navegación por teclado, foco visible, avisos accesibles y estados de carga, vacío, error y éxito.

Las rutas protegidas conservan la dirección solicitada mediante el parámetro `next`. Una respuesta `401` elimina la sesión local y devuelve la experiencia al inicio de sesión sin perder el carrito del visitante.

### Rutas de la SPA

| Ruta | Alcance |
|---|---|
| `/productos` y `/productos/:id` | Catálogo y detalle de producto |
| `/carrito` | Carrito local o persistido |
| `/login` y `/registro` | Autenticación y creación de cuenta |
| `/perfil` | Gestión de la cuenta autenticada |
| `/ordenes` y `/ordenes/:id` | Historial y detalle de compras |
| `/admin/*` | Gestión disponible para `ADMIN` |

## Backend

La API utiliza Java 17, Spring Boot 4.1.1, Spring Data JPA, Spring Security, Bean Validation y MySQL 8. Las contraseñas se almacenan con BCrypt y las respuestas se construyen mediante DTO, sin exponer directamente las entidades.

Los dominios implementados son:

| Módulo | Responsabilidad |
|---|---|
| `auth` y `usuario` | Login, renovación de token, registro, perfil y administración de usuarios |
| `categoria` | Categorías raíz y subcategorías |
| `producto` | Catálogo, stock, búsqueda, filtros y paginación |
| `carrito` | Carrito por usuario y validación de existencias |
| `orden` | Checkout, historial, estados, cancelación y restauración de stock |

### Seguridad

El login en `POST /api/auth/login` devuelve un token con el identificador, email, rol y versión del usuario. Las solicitudes protegidas utilizan `Authorization: Bearer <token>`.

Las consultas de productos y categorías son públicas. El registro y el login también son públicos. Las operaciones sobre carrito, perfil y órdenes requieren autenticación, mientras que las mutaciones administrativas requieren el rol `ADMIN`.

La renovación en `POST /api/auth/refresh` incrementa la versión de token del usuario; como resultado, el token anterior deja de ser válido.

## Flujo de compra

1. El catálogo presenta productos disponibles y limita las cantidades según el stock comunicado por la API.
2. El carrito calcula subtotales y total, y conserva su estado local o remotamente según la sesión.
3. El checkout valida nuevamente el stock dentro de una transacción.
4. La orden se crea con estado `PENDIENTE`, registra el precio unitario y descuenta las existencias.
5. La cancelación de una orden pendiente restaura el stock y cambia su estado a `CANCELADA`.

Los estados admitidos son `PENDIENTE`, `PAGADA`, `ENVIADA`, `ENTREGADA` y `CANCELADA`. El panel administrativo representa el avance normal `PENDIENTE → PAGADA → ENVIADA → ENTREGADA`.

## Persistencia

El modelo relacional contiene las tablas `usuarios`, `categorias`, `productos`, `carritos`, `carrito_items`, `ordenes` y `orden_items`. Las categorías mantienen una referencia opcional a su categoría padre y cada carrito pertenece de forma única a un usuario.

La inicialización automática está deshabilitada mediante `spring.sql.init.mode=never` y `spring.jpa.hibernate.ddl-auto=none`. Los archivos [`schema.sql`](src/main/resources/schema.sql) y [`data.sql`](src/main/resources/data.sql) contienen el esquema y los datos de demostración para una instalación local.

## Entorno local

El entorno de desarrollo utiliza:

- Java 17 y Maven Wrapper para el backend.
- MySQL 8 en `localhost:3306/store`.
- Node.js para el servidor frontal, construido únicamente con módulos nativos.
- Las variables `DB_USERNAME`, `DB_PASSWORD` y `JWT_SECRET`; el secreto JWT requiere al menos 32 caracteres.

Ejemplo de variables en PowerShell:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="tu-clave-local"
$env:JWT_SECRET="genera-un-secreto-aleatorio-de-al-menos-32-caracteres"
```

El backend se inicia desde la raíz con:

```powershell
.\mvnw.cmd spring-boot:run
```

El frontend se sirve en `http://localhost:4173` mediante:

```powershell
node .\frontend\dev-server.js
```

El servidor frontal entrega los recursos de `frontend/`, aplica el fallback de la SPA y reenvía `/api/**` a `http://localhost:8080`.

## Estructura del proyecto

```text
store/
├── frontend/                    # SPA y servidor de desarrollo
├── docs/frontend-spec.md        # Especificación funcional del frontend
├── openapi.yaml                 # Contrato de la API
├── specs/                       # Features, historias, incidencias y colección HTTP
├── src/main/java/.../store/     # Backend organizado por dominios
├── src/main/resources/          # Configuración y scripts SQL
└── src/test/                    # Pruebas de integración con H2
```

## Pruebas y documentación

Las pruebas de integración utilizan H2 en modo compatible con MySQL y cubren registro, permisos, paginación, renovación de JWT, categorías, checkout y cancelación con restauración de stock.

```powershell
.\mvnw.cmd test
```

El contrato completo está disponible en [`openapi.yaml`](openapi.yaml). La colección de solicitudes se encuentra en [`specs/collection.json`](specs/collection.json) y la descripción detallada de pantallas, estados y accesibilidad está en [`docs/frontend-spec.md`](docs/frontend-spec.md).

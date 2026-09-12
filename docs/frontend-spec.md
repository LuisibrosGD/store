# Especificación funcional del frontend

## 1. Objetivo y alcance MVP

El frontend permite descubrir productos, mantener un carrito, registrarse, autenticarse,
comprar y consultar órdenes. También ofrece a usuarios `ADMIN` las operaciones de gestión
expuestas por el backend para productos, categorías, usuarios y estados de órdenes.

Es una SPA escrita únicamente con HTML, CSS y módulos JavaScript ES6. El contrato de red es
[`openapi.yaml`](../openapi.yaml). El frontend se sirve en el mismo origen que la API y usa
la ruta relativa `/api`; el servidor frontal o reverse proxy debe enviar `/api/**` a Spring
Boot en `http://localhost:8080`.

El MVP no incluye pagos, imágenes de producto, favoritos, reseñas, recuperación de contraseña,
direcciones, envíos ni ordenamiento porque el backend no expone esos datos o endpoints.

## 2. Roles y permisos

| Capacidad | Visitante | CLIENTE | ADMIN |
|---|---:|---:|---:|
| Ver, buscar y filtrar productos/categorías | Sí | Sí | Sí |
| Mantener carrito local | Sí | — | — |
| Mantener carrito persistido por API | No | Sí | Sí |
| Registrarse como CLIENTE | Sí | — | — |
| Ver/editar/eliminar perfil propio | No | Sí | Sí |
| Crear orden y consultar historial propio | No | Sí | Sí |
| Cancelar una orden propia PENDIENTE | No | Sí | Sí |
| Gestionar productos y categorías | No | No | Sí |
| Gestionar usuarios | No | No | Sí |
| Ver todas las órdenes y avanzar estados | No | No | Sí |

El JWT se conserva en `sessionStorage`: cerrar la pestaña termina la sesión local. El carrito
invitado y la última copia del carrito autenticado se conservan en `localStorage`. Tras iniciar
sesión, los ítems invitados se agregan al carrito remoto; solo se eliminan del almacenamiento
invitado si toda la sincronización finaliza correctamente.

## 3. Historias de usuario

- Como visitante, quiero explorar, buscar y filtrar productos para decidir qué comprar.
- Como visitante, quiero agregar productos a un carrito local y conservarlos al recargar.
- Como visitante, quiero registrarme e iniciar sesión para completar una compra.
- Como cliente, quiero cambiar cantidades, quitar productos o vaciar mi carrito.
- Como cliente, quiero confirmar el carrito y recibir una orden `PENDIENTE`.
- Como cliente, quiero revisar mis órdenes y cancelar una que aún esté pendiente.
- Como cliente, quiero consultar y actualizar mi perfil, o eliminar mi cuenta.
- Como administrador, quiero crear, actualizar, ajustar stock y eliminar productos.
- Como administrador, quiero crear, renombrar y eliminar categorías válidas.
- Como administrador, quiero crear, actualizar y eliminar usuarios.
- Como administrador, quiero revisar todas las órdenes y avanzar sus estados permitidos.

## 4. Navegación

La SPA usa History API. El servidor debe devolver `frontend/index.html` para rutas frontales
desconocidas, sin interceptar `/api/**`.

```text
/
└── /productos
    └── /productos/:id
/carrito
/login
/registro
/perfil                         (autenticado)
/ordenes                        (autenticado)
└── /ordenes/:id                (autenticado)
/admin/productos                (ADMIN)
/admin/categorias               (ADMIN)
/admin/usuarios                 (ADMIN)
/admin/ordenes                  (ADMIN)
```

Las rutas protegidas redirigen a `/login?next=<ruta>`. Una ruta administrativa abierta por un
usuario que no es `ADMIN` muestra acceso denegado y ofrece volver al catálogo.

## 5. Sistema común de interfaz

- Cabecera con marca, navegación contextual, estado de sesión y contador del carrito.
- Región principal enfocada mediante navegación de SPA y enlace “Saltar al contenido”.
- Avisos con `role="status"` o `role="alert"` para éxito, error y eventos de red.
- Botones deshabilitados durante operaciones mutables para evitar envíos repetidos.
- Diálogo nativo accesible para formularios administrativos y confirmaciones destructivas.
- Estados compartidos: skeleton/indicador de carga, vacío con siguiente acción, error recuperable
  con botón “Reintentar” y confirmación breve tras éxito.
- Diseño mobile first; controles táctiles de al menos 44 px, foco visible, etiquetas explícitas,
  contraste suficiente y navegación completa por teclado.

## 6. Detalle por pantalla

### `/productos` — Catálogo

Componentes: título, buscador, categoría, precio mínimo/máximo, botón de filtros, resumen de
resultados, cuadrícula de productos y paginador. Cada tarjeta muestra nombre, categoría, precio,
stock, enlace al detalle y acción de agregar.

Acciones: buscar con debounce al escribir, combinar filtros mediante `/productos/filtrar`, limpiar
filtros, cambiar página y agregar una unidad al carrito. El botón de compra se deshabilita sin stock.

Validaciones: precios mayores o iguales a cero; mínimo no mayor al máximo. Si no hay filtros se usa
`GET /productos`; si existe cualquier filtro se usa `GET /productos/filtrar`.

Estados: carga inicial, sin resultados, fallo de API, resultados disponibles y confirmación de ítem
agregado.

### `/productos/:id` — Detalle de producto

Componentes: ruta de regreso, nombre, categoría, precio, disponibilidad, selector numérico y botón
“Agregar al carrito”. Validación de cantidad entera entre 1 y el stock comunicado por la API.

Estados: cargando, no encontrado, sin stock, error de red y agregado correctamente.

### `/carrito` — Carrito

Componentes: lista de ítems, control de cantidad, quitar, vaciar, resumen total y confirmar compra.
El visitante ve que el carrito es local; el usuario autenticado trabaja contra la API y mantiene una
copia local para recuperación visual.

Acciones: incrementar, disminuir, escribir cantidad, quitar, vaciar y checkout. Cantidad cero elimina
el ítem. Si un visitante intenta comprar se dirige a login; después de autenticarse se sincroniza.

Estados: sincronizando, vacío, cambio guardado, error de stock, error de red y orden creada.

### `/login` — Inicio de sesión

Formulario con email y contraseña. Ambos son obligatorios y el email usa validación nativa. Durante
el envío se bloquea el botón. Al completar, guarda sesión, sincroniza el carrito invitado y navega a
`next` o a `/productos`. Los errores 400/401 se muestran sin revelar más datos que el backend.

### `/registro` — Registro

Formulario con nombre, email y contraseña. No permite elegir rol: el registro público siempre crea
`CLIENTE`. Tras éxito ofrece iniciar sesión. Maneja validaciones por campo y conflicto de email.

### `/perfil` — Mi perfil

Muestra id, rol y formulario de nombre, email y contraseña nueva, todos obligatorios para actualizar.
Incluye cerrar sesión y eliminar cuenta con confirmación. Tras eliminar, limpia sesión y carrito remoto
cacheado, y vuelve al catálogo.

### `/ordenes` y `/ordenes/:id` — Órdenes propias

El historial muestra número, fecha local, estado, total y acceso al detalle. El detalle lista ítems,
precios y subtotales. Una orden `PENDIENTE` propia muestra “Cancelar”; otros estados son solo lectura.
Estados: cargando, historial vacío, detalle no encontrado, error y cancelación confirmada.

### `/admin/productos` — Productos

Tabla adaptable con producto, categoría, precio, stock y acciones. Un diálogo permite crear o editar
usando el cuerpo completo requerido por la API. Existe un ajuste rápido de stock y eliminación con
confirmación. Validaciones: nombre obligatorio, precio y stock no negativos, categoría obligatoria.

### `/admin/categorias` — Categorías

Árbol de dos niveles con creación de raíz o subcategoría, renombrado y eliminación confirmada. El
formulario no permite elegir como padre una subcategoría. Al editar solo envía el nombre efectivo,
pues el backend conserva el padre actual.

### `/admin/usuarios` — Usuarios

Tabla con id, nombre, email y rol. Crear/editar exige nombre, email y contraseña; el rol es `ADMIN` o
`CLIENTE`. Eliminar requiere confirmación. La interfaz no ofrece editar el usuario propio desde esta
pantalla; para ello enlaza a `/perfil`.

### `/admin/ordenes` — Todas las órdenes

Listado con usuario, fecha, estado y total. Permite ver detalle y avanzar una transición válida:
`PENDIENTE → PAGADA → ENVIADA → ENTREGADA`. La cancelación no aparece aquí porque el endpoint de
cancelación solo admite al dueño de la orden.

### Ruta desconocida

Muestra “Página no encontrada” con enlace al catálogo; no realiza solicitudes a la API.

## 7. Mapa pantalla–API

| Pantalla | Endpoints usados |
|---|---|
| Catálogo | `GET /productos`, `GET /productos/filtrar`, `GET /categorias` |
| Detalle de producto | `GET /productos/{id}` |
| Carrito visitante | Ninguno; `localStorage` |
| Carrito autenticado | `GET /carrito`, `POST /carrito/items`, `PUT /carrito/items/{productoId}`, `DELETE /carrito/items/{productoId}`, `DELETE /carrito` |
| Login | `POST /auth/login`, y durante sincronización `POST /carrito/items` |
| Registro | `POST /usuarios` |
| Perfil | `GET /usuarios/me`, `PUT /usuarios/me`, `DELETE /usuarios/me` |
| Órdenes propias | `GET /ordenes`, `GET /ordenes/{id}`, `POST /ordenes/{id}/cancelar` |
| Checkout | `POST /ordenes/checkout` |
| Admin productos | `GET /productos`, `GET /categorias`, `POST /productos`, `PUT /productos/{id}`, `PATCH /productos/{id}/stock`, `DELETE /productos/{id}` |
| Admin categorías | `GET /categorias`, `POST /categorias`, `PUT /categorias/{id}`, `DELETE /categorias/{id}` |
| Admin usuarios | `GET /usuarios`, `POST /usuarios`, `PUT /usuarios/{id}`, `DELETE /usuarios/{id}` |
| Admin órdenes | `GET /ordenes/admin/todas`, `GET /ordenes/{id}`, `PATCH /ordenes/{id}/estado` |
| Renovación de sesión | `POST /auth/refresh` (función disponible en la capa API; no se renueva automáticamente) |

El alias `POST /usuarios/login` está documentado por completitud pero el frontend usa la ruta canónica
`POST /auth/login`. `GET /categorias/paginadas` y `GET /productos/buscar` también están documentados,
pero la experiencia usa el árbol completo y el filtro combinado, respectivamente.

## 8. Criterios de aceptación

1. La aplicación funciona sin frameworks, librerías, bundlers ni paso de build.
2. Todas las solicitudes usan `fetch`, JSON y exactamente las rutas/campos documentados.
3. Las operaciones protegidas incluyen `Authorization: Bearer <token>`.
4. Una respuesta 401 limpia la sesión y conduce al login sin perder el carrito invitado.
5. El visitante conserva el carrito tras recargar y este se sincroniza al iniciar sesión.
6. No se puede agregar más que el stock visible ni enviar valores negativos desde la interfaz.
7. Catálogo y administraciones muestran estados loading, empty, error y success pertinentes.
8. Las rutas y acciones administrativas solo están disponibles para `ADMIN`.
9. El cliente solo consulta su historial y solo puede cancelar órdenes propias pendientes.
10. La interfaz funciona desde 320 px de ancho y en escritorio sin desplazamiento horizontal global.
11. Todos los campos tienen etiquetas, los avisos se anuncian y el foco es visible.
12. Los fallos de red producen un mensaje comprensible y una acción de reintento cuando aplica.

## 9. TODO y condiciones de integración

- Para desarrollo local, `frontend/dev-server.js` sirve la SPA y reenvía `/api/**` a Spring Boot
  sin dependencias externas. En producción debe configurarse el mismo fallback y proxy en el
  servidor elegido. El backend no habilita CORS y no se modifica por esta entrega.
- **TODO de contrato:** Spring no define un formato propio para errores de conversión de query/path ni
  para fallos internos de persistencia; consultar el comentario al final de `openapi.yaml`.
- El backend usa `Double` para importes. El frontend formatea a dos decimales, pero no puede corregir
  posibles diferencias binarias del cálculo en servidor.
- No se implementa ordenamiento porque ningún endpoint acepta parámetros de orden.

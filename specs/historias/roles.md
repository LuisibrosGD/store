# Historias de usuario: Roles y permisos

## HU-ROL-001 — Administrar los recursos de la tienda

**Actor:** Administrador autenticado

**Como** administrador, **quiero** gestionar los recursos principales de la tienda **para** mantener su operación y catálogo.

**Descripción:** El administrador dispone de permisos completos sobre usuarios, categorías, productos y seguimiento de órdenes.

### Criterios de aceptación

1. Permitir crear, consultar, actualizar y eliminar productos.
2. Permitir crear, consultar, actualizar y eliminar categorías.
3. Permitir crear, consultar, actualizar y eliminar usuarios.
4. Permitir gestionar el estado de las órdenes.
5. Permitir consultar todas las órdenes de todos los clientes.

### Reglas de negocio

- Estas operaciones requieren una sesión con rol ADMIN.

### Dependencias

- Depende de HU-JWT-002 y de las historias administrativas de cada dominio.

## HU-ROL-002 — Utilizar las funciones de cliente

**Actor:** Cliente autenticado

**Como** cliente, **quiero** utilizar el catálogo, carrito y órdenes propias **para** realizar y administrar mis compras.

**Descripción:** El cliente accede a las funciones comerciales relacionadas con su propia cuenta.

### Criterios de aceptación

1. Permitir consultar productos.
2. Permitir agregar, modificar y quitar productos del carrito propio.
3. Permitir confirmar la compra del carrito.
4. Mostrar únicamente el historial de órdenes propio.
5. Permitir cancelar órdenes propias que estén pendientes.

### Reglas de negocio

- Las operaciones personales se limitan al cliente autenticado.

### Dependencias

- Depende de HU-JWT-002, las historias del carrito y las historias de órdenes.

## HU-ROL-003 — Restringir operaciones administrativas al cliente

**Actor:** Cliente autenticado

**Como** responsable de seguridad, **quiero** restringir las operaciones administrativas de los clientes **para** proteger la información y configuración de la tienda.

**Descripción:** Un cliente conserva acceso a sus recursos, pero no puede administrar recursos globales ni información ajena.

### Criterios de aceptación

1. Denegar al cliente la creación, actualización o eliminación de productos.
2. Denegar al cliente la creación, actualización o eliminación de categorías.
3. Denegar al cliente la gestión de otra cuenta de usuario.
4. Impedir que consulte órdenes de otros clientes.
5. Denegar el cambio administrativo de estados de órdenes.

### Reglas de negocio

- Las denegaciones no deben modificar datos.
- La cancelación de una orden propia pendiente continúa permitida.

### Dependencias

- Complementa HU-ROL-002.

## HU-ROL-004 — Acceder a funciones públicas

**Actor:** Visitante

**Como** visitante, **quiero** registrarme e iniciar sesión sin una sesión previa **para** comenzar a utilizar la tienda.

**Descripción:** Las funciones de entrada al sistema son públicas, mientras que las operaciones personales o administrativas requieren autenticación.

### Criterios de aceptación

1. Permitir iniciar sesión sin autenticación previa.
2. Permitir registrar públicamente una cuenta con rol CLIENTE.
3. Rechazar el acceso a una función protegida cuando no exista una sesión válida.

### Reglas de negocio

- El registro público no permite crear administradores.
- Las funciones protegidas exigen una sesión vigente.

### Dependencias

- Se relaciona con HU-USU-001, HU-USU-005 y HU-JWT-002.

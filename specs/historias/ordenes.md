# Historias de usuario: Órdenes y compras

## HU-ORD-001 — Crear una orden desde el carrito

**Actor:** Cliente autenticado

**Como** cliente, **quiero** confirmar la compra de mi carrito **para** generar una orden con los productos seleccionados.

**Descripción:** La confirmación registra una orden pendiente, descuenta el inventario y limpia el carrito de forma conjunta.

### Criterios de aceptación

1. Con productos disponibles, crear una orden PENDIENTE con cantidades y precios, descontar el stock y vaciar el carrito.
2. Con un carrito vacío, informar la situación y no crear una orden.
3. Si algún producto ya no tiene stock suficiente, informar qué producto presenta el problema, no crear la orden y conservar el carrito.

### Reglas de negocio

- La operación completa debe realizarse de forma indivisible.
- Los precios de la orden corresponden al momento de confirmar la compra.

### Dependencias

- Depende de HU-CAR-004 y HU-PRO-005.

## HU-ORD-002 — Consultar una orden

**Actor:** Cliente autenticado

**Como** cliente, **quiero** consultar una orden propia **para** revisar su estado, total e ítems comprados.

**Descripción:** El detalle solo está disponible para el propietario de la orden.

### Criterios de aceptación

1. Mostrar los datos completos, ítems y estado de una orden propia.
2. Si la orden no existe, informar que no fue encontrada.
3. Si otro cliente intenta consultar la orden, no revelar sus datos e informar que no fue encontrada.

### Reglas de negocio

- Un cliente no puede consultar órdenes de otros clientes.

### Dependencias

- Depende de HU-ORD-001 y HU-JWT-002.

## HU-ORD-003 — Consultar el historial de órdenes

**Actor:** Cliente autenticado

**Como** cliente, **quiero** consultar mi historial de órdenes **para** revisar mis compras anteriores y actuales.

**Descripción:** El historial contiene exclusivamente las órdenes del cliente actual.

### Criterios de aceptación

1. Mostrar las órdenes propias ordenadas de la más reciente a la más antigua.
2. Si el cliente no tiene órdenes, mostrar una lista vacía.

### Reglas de negocio

- No se incluyen órdenes de otros clientes.

### Dependencias

- Depende de HU-ORD-001.

## HU-ORD-004 — Gestionar estados de una orden

**Actor:** Administrador y cliente propietario

**Como** administrador, **quiero** avanzar el estado de las órdenes **para** reflejar su procesamiento; **como** cliente, **quiero** cancelar una orden pendiente **para** desistir de la compra.

**Descripción:** El administrador controla el ciclo normal y el cliente propietario puede cancelar mientras la orden esté pendiente.

### Criterios de aceptación

1. Permitir al administrador las transiciones indicadas:

| Estado actual | Nuevo estado |
|---|---|
| PENDIENTE | PAGADA |
| PAGADA | ENVIADA |
| ENVIADA | ENTREGADA |

2. Permitir al propietario cancelar una orden PENDIENTE, cambiarla a CANCELADA y restaurar el stock.
3. Rechazar la cancelación de una orden que no esté PENDIENTE sin modificarla.
4. Rechazar cualquier estado no permitido, como EN_PROCESO, sin modificar la orden.

### Reglas de negocio

- Los estados válidos son PENDIENTE, PAGADA, ENVIADA, ENTREGADA y CANCELADA.
- La restauración de stock ocurre una sola vez al cancelar.
- El cliente no puede administrar el ciclo de estados.

### Dependencias

- Depende de HU-ORD-001, HU-PRO-005 y de las reglas de roles.

# Historias de usuario: Carrito de compras

## HU-CAR-001 — Agregar productos al carrito

**Actor:** Cliente autenticado

**Como** cliente, **quiero** agregar productos y cantidades a mi carrito **para** preparar una compra.

**Descripción:** El carrito acumula cantidades del mismo producto y valida la disponibilidad antes de aceptar cambios.

### Criterios de aceptación

1. Al agregar dos unidades disponibles de “iPhone 15”, incorporar el producto con cantidad 2 y calcular su subtotal.
2. Si el producto ya tiene dos unidades en el carrito y se agregan tres más, actualizar su cantidad total a 5 sin duplicar el ítem.
3. Rechazar productos agotados o cantidades superiores al stock sin modificar el carrito.
4. Si el producto no existe, informar que no fue encontrado y conservar el carrito sin cambios.

| Situación | Stock | Cantidad solicitada | Resultado esperado |
|---|---:|---:|---|
| Producto agotado | 0 | 1 | Informar que no tiene stock disponible |
| Stock insuficiente | 5 | 10 | Informar que no hay stock suficiente |

### Reglas de negocio

- La cantidad agregada debe ser positiva.
- La cantidad acumulada no puede superar el stock disponible.
- Cada carrito pertenece al cliente autenticado.

### Dependencias

- Requiere autenticación y productos registrados.

## HU-CAR-002 — Cambiar la cantidad de un producto

**Actor:** Cliente autenticado

**Como** cliente, **quiero** modificar la cantidad de un producto en mi carrito **para** ajustar mi compra.

**Descripción:** La cantidad puede aumentarse, reducirse o establecerse en cero para retirar el producto.

### Criterios de aceptación

1. Cambiar la cantidad del producto seleccionado al valor solicitado cuando exista stock suficiente.
2. Eliminar el ítem cuando la nueva cantidad sea cero.
3. Rechazar una cantidad superior al stock y conservar la cantidad anterior.
4. Si el producto no está en el carrito, informar la situación y no modificarlo.

### Reglas de negocio

- No se permiten cantidades negativas.
- Todo aumento debe validarse contra el stock actual.

### Dependencias

- Depende de HU-CAR-001.

## HU-CAR-003 — Quitar un producto del carrito

**Actor:** Cliente autenticado

**Como** cliente, **quiero** quitar un producto de mi carrito **para** excluirlo de la compra.

**Descripción:** La eliminación afecta solamente al carrito del cliente actual.

### Criterios de aceptación

1. Al quitar un producto existente, retirarlo del carrito; si era el único, dejar el carrito vacío.
2. Si el producto no está en el carrito, informar la situación y no realizar cambios.

### Reglas de negocio

- No debe alterarse el carrito de otro cliente.

### Dependencias

- Depende de HU-CAR-001.

## HU-CAR-004 — Consultar y vaciar el carrito

**Actor:** Cliente autenticado

**Como** cliente, **quiero** consultar o vaciar mi carrito **para** conocer el contenido y controlar mi selección.

**Descripción:** La consulta muestra los ítems, subtotales y total calculado.

### Criterios de aceptación

1. Mostrar todos los ítems del carrito y un total igual a la suma de sus subtotales.
2. Para un carrito vacío, mostrar cero ítems y total 0.
3. Al vaciar un carrito con productos, eliminar todos sus ítems y establecer el total en 0.

### Reglas de negocio

- Cada subtotal se calcula como precio por cantidad.
- El total es la suma de todos los subtotales.

### Dependencias

- Depende de HU-CAR-001, HU-CAR-002 y HU-CAR-003.

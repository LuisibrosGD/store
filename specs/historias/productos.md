# Historias de usuario: Gestión de productos

## HU-PRO-001 — Crear un producto

**Actor:** Administrador

**Como** administrador, **quiero** registrar productos **para** incorporarlos al catálogo de venta.

**Descripción:** Cada producto se registra con nombre, precio, stock y una categoría existente.

### Criterios de aceptación

1. Crear un producto válido y mostrarlo en el catálogo con su precio y stock.
2. Rechazar el registro cuando la categoría no exista.
3. Rechazar precios negativos.
4. Rechazar cantidades de stock negativas.

| Caso inválido | Resultado esperado |
|---|---|
| Categoría inexistente | Informar que la categoría no fue encontrada |
| Precio negativo | Informar que el precio no puede ser negativo |
| Stock negativo | Informar que el stock no puede ser negativo |

### Reglas de negocio

- Nombre, precio, stock y categoría son obligatorios.
- El producto debe pertenecer a una categoría existente.

### Dependencias

- Depende de HU-CAT-001.

## HU-PRO-002 — Consultar productos

**Actor:** Usuario del catálogo

**Como** usuario, **quiero** consultar productos **para** conocer la oferta, precio y disponibilidad.

**Descripción:** Se permite consultar el listado o el detalle de un producto.

### Criterios de aceptación

1. Mostrar el listado completo de productos registrados.
2. Mostrar los datos completos, precio y stock de un producto específico.
3. Si el producto no existe, informar que no fue encontrado.

### Reglas de negocio

- La consulta del catálogo está disponible para clientes y administradores.

### Dependencias

- Depende de HU-PRO-001.

## HU-PRO-003 — Actualizar un producto

**Actor:** Administrador

**Como** administrador, **quiero** actualizar los datos de un producto **para** mantener correcto el catálogo.

**Descripción:** Se pueden modificar nombre, precio, stock y categoría.

### Criterios de aceptación

1. Actualizar un producto existente y reflejar sus nuevos datos.
2. Si el producto no existe, informar que no fue encontrado y no realizar cambios.
3. Rechazar la actualización si la nueva categoría no existe y conservar los datos anteriores.

### Reglas de negocio

- Se aplican las mismas validaciones utilizadas al crear productos.

### Dependencias

- Depende de HU-PRO-001 y HU-CAT-001.

## HU-PRO-004 — Eliminar un producto

**Actor:** Administrador

**Como** administrador, **quiero** eliminar productos **para** retirarlos del catálogo cuando ya no estén disponibles.

**Descripción:** La eliminación retira el producto identificado del catálogo.

### Criterios de aceptación

1. Eliminar un producto existente y dejar de mostrarlo en el listado.
2. Si el producto no existe, informar que no fue encontrado y no realizar ninguna eliminación.

### Reglas de negocio

- Solo un administrador puede eliminar productos.

### Dependencias

- Depende de HU-PRO-001.

## HU-PRO-005 — Actualizar el stock de un producto

**Actor:** Administrador

**Como** administrador, **quiero** actualizar el stock de un producto **para** mantener su disponibilidad real.

**Descripción:** El stock puede modificarse independientemente de los demás datos del producto.

### Criterios de aceptación

1. Sustituir el stock actual por el nuevo valor indicado.
2. Permitir establecer stock en 0 para representar un producto agotado.
3. Permitir establecer una cantidad positiva, como 100 unidades.
4. Rechazar valores negativos y conservar el stock anterior.

| Nuevo stock | Resultado esperado |
|---:|---|
| 0 | Producto agotado |
| 100 | Stock actualizado a 100 |

### Reglas de negocio

- El stock nunca puede ser negativo.

### Dependencias

- Depende de HU-PRO-001.

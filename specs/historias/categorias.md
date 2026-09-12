# Historias de usuario: Gestión de categorías

## HU-CAT-001 — Crear categorías

**Actor:** Administrador

**Como** administrador, **quiero** crear categorías y subcategorías **para** organizar jerárquicamente los productos.

**Descripción:** Una categoría puede ser raíz o depender de una categoría padre.

### Criterios de aceptación

1. Crear una categoría raíz cuando no se indique categoría padre y mostrarla entre las raíces.
2. Crear una subcategoría bajo una categoría existente y relacionarla con su padre.
3. Rechazar un nombre duplicado bajo el mismo padre sin crear otro registro.

### Reglas de negocio

- Las categorías raíz no tienen padre.
- La jerarquía permite como máximo dos niveles.
- El nombre debe ser único entre categorías que compartan el mismo padre.

### Dependencias

- La creación de una subcategoría requiere una categoría padre existente.

## HU-CAT-002 — Consultar categorías

**Actor:** Usuario del catálogo

**Como** usuario, **quiero** consultar categorías y sus relaciones **para** comprender la organización del catálogo.

**Descripción:** Las categorías pueden consultarse como árbol o individualmente.

### Criterios de aceptación

1. Mostrar las categorías raíz junto con sus subcategorías respetando la estructura jerárquica.
2. Al consultar una categoría específica, mostrar sus datos y subcategorías directas.
3. Si la categoría no existe, informar que no fue encontrada.

### Reglas de negocio

- Las relaciones padre-hijo deben conservarse en la presentación.

### Dependencias

- Depende de HU-CAT-001.

## HU-CAT-003 — Actualizar una categoría

**Actor:** Administrador

**Como** administrador, **quiero** actualizar el nombre de una categoría **para** mantener vigente la organización del catálogo.

**Descripción:** La actualización conserva la posición de la categoría en la jerarquía.

### Criterios de aceptación

1. Actualizar el nombre de una categoría existente y mostrar el nuevo nombre.
2. Rechazar un nombre ya utilizado en el mismo nivel y conservar los datos anteriores.
3. Si la categoría no existe, informar que no fue encontrada.

### Reglas de negocio

- La actualización no debe crear duplicados bajo el mismo padre.

### Dependencias

- Depende de HU-CAT-001.

## HU-CAT-004 — Eliminar una categoría

**Actor:** Administrador

**Como** administrador, **quiero** eliminar categorías sin dependencias **para** retirar clasificaciones que ya no se utilizan.

**Descripción:** Una categoría solo puede eliminarse cuando no tenga subcategorías ni productos asociados.

### Criterios de aceptación

1. Eliminar una categoría existente sin subcategorías ni productos y retirarla del listado.
2. Rechazar la eliminación si tiene subcategorías.
3. Rechazar la eliminación si tiene productos asociados.
4. Si la categoría no existe, informar que no fue encontrada.

### Reglas de negocio

- No se permite dejar productos o subcategorías sin categoría válida.

### Dependencias

- Depende de HU-CAT-001 y de la gestión de productos.

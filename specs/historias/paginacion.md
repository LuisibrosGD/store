# Historias de usuario: Paginación

## HU-PAG-001 — Paginar productos

**Actor:** Cliente

**Como** cliente, **quiero** consultar el catálogo por páginas **para** navegar cómodamente entre muchos productos.

**Descripción:** Cada respuesta incluye los elementos solicitados y datos que permiten conocer la posición dentro del catálogo.

### Criterios de aceptación

1. Para 25 productos y páginas de tamaño 10, devolver 10 elementos en las páginas 1 y 2, y 5 en la página 3.
2. Incluir total de elementos, total de páginas, página actual y tamaño solicitado.
3. Sin parámetros, mostrar la página 1 con tamaño 10.
4. Para una página fuera de rango, mostrar una lista vacía conservando la página solicitada y el total real de páginas.

### Reglas de negocio

- La numeración funcional comienza en 1.
- El tamaño predeterminado es 10.

### Dependencias

- Depende de HU-PRO-002.

## HU-PAG-002 — Paginar categorías

**Actor:** Cliente

**Como** cliente, **quiero** consultar categorías por páginas **para** navegar de forma eficiente cuando existan muchas clasificaciones.

**Descripción:** La consulta paginada incluye las categorías y sus datos de navegación.

### Criterios de aceptación

1. Con 15 categorías, al solicitar la página 1 con tamaño 5, mostrar 5 categorías.
2. Informar 15 elementos totales, 3 páginas, página actual 1 y tamaño 5.

### Reglas de negocio

- Se aplican los mismos valores predeterminados y numeración de la paginación de productos.

### Dependencias

- Depende de HU-CAT-002.

## HU-PAG-003 — Paginar resultados filtrados

**Actor:** Cliente

**Como** cliente, **quiero** paginar búsquedas y filtros **para** revisar resultados específicos sin recibirlos todos a la vez.

**Descripción:** La información de paginación se calcula sobre el resultado filtrado, no sobre todo el catálogo.

### Criterios de aceptación

1. Si 12 productos contienen “Phone”, mostrar 5 en la página 1 de tamaño 5 e informar 12 resultados y 3 páginas.
2. Si una categoría tiene 20 productos, mostrar los productos 11 al 20 en la página 2 de tamaño 10 e informar 20 resultados y 2 páginas.

### Reglas de negocio

- Los filtros se aplican antes de calcular totales y páginas.

### Dependencias

- Depende de HU-PAG-001 y de las historias de búsqueda y filtros.

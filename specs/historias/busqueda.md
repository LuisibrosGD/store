# Historias de usuario: Búsqueda y filtros de productos

## HU-BUS-001 — Buscar productos por nombre

**Actor:** Cliente

**Como** cliente, **quiero** buscar productos mediante una parte de su nombre **para** encontrar rápidamente artículos de mi interés.

**Descripción:** La búsqueda debe considerar coincidencias parciales sin distinguir mayúsculas de minúsculas.

### Criterios de aceptación

1. Al buscar `iPhone`, mostrar los productos cuyos nombres contengan ese texto, incluidos “iPhone 15” e “iPhone 14”.
2. Cuando ningún producto coincida con el texto buscado, mostrar una lista vacía.

### Reglas de negocio

- La coincidencia se realiza por contenido y no por igualdad exacta.
- La búsqueda no distingue mayúsculas de minúsculas.

### Dependencias

- Requiere productos registrados en el catálogo.

## HU-BUS-002 — Filtrar productos por categoría

**Actor:** Cliente

**Como** cliente, **quiero** filtrar productos por categoría **para** explorar solamente artículos del tipo que necesito.

**Descripción:** El catálogo debe limitar los resultados a la categoría seleccionada.

### Criterios de aceptación

1. Al seleccionar una categoría con productos, mostrar únicamente los productos asociados a ella.
2. Al seleccionar una categoría sin productos, mostrar una lista vacía.

### Reglas de negocio

- Ningún resultado puede pertenecer a una categoría distinta de la seleccionada.

### Dependencias

- Depende de la gestión de categorías y del catálogo de productos.

## HU-BUS-003 — Filtrar productos por rango de precio

**Actor:** Cliente

**Como** cliente, **quiero** establecer un precio mínimo y máximo **para** encontrar productos dentro de mi presupuesto.

**Descripción:** Los límites del rango se incluyen en la búsqueda.

### Criterios de aceptación

1. Mostrar todos los productos cuyo precio se encuentre entre el mínimo y el máximo indicados.
2. Respetar las siguientes variantes especificadas:

| Precio mínimo | Precio máximo | Resultados esperados |
|---:|---:|---|
| 500 | 9999 | iPhone 15 y Samsung Galaxy |
| 0 | 100 | Camisa Azul |
| 50 | 900 | Samsung Galaxy |

### Reglas de negocio

- Los precios iguales a cualquiera de los límites son válidos.

### Dependencias

- Requiere productos con precios registrados.

## HU-BUS-004 — Combinar filtros de productos

**Actor:** Cliente

**Como** cliente, **quiero** combinar nombre, categoría y precio **para** refinar los resultados del catálogo.

**Descripción:** Los filtros seleccionados se aplican de manera acumulativa.

### Criterios de aceptación

1. Al buscar `iPhone` dentro de la categoría 1, mostrar “iPhone 15” e “iPhone 14”.
2. Al filtrar la categoría 1 con un precio máximo de 900, mostrar “iPhone 14”.
3. Cuando los filtros combinados no coincidan con un mismo producto, mostrar una lista vacía.
4. Cuando no se indique ningún filtro, mostrar todos los productos.

### Reglas de negocio

- Todas las condiciones proporcionadas se combinan mediante una relación lógica AND.

### Dependencias

- Depende de HU-BUS-001, HU-BUS-002 y HU-BUS-003.

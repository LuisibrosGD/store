## 📋 Índice
 
| ID | Resumen | Endpoint | Estado | Gherkin |
|----|---------|----------|--------|---------|
| #001 | Cliente puede crear, actualizar, eliminar, crear subcategoria | `POST, PUT, DEL {{baseUrl}}/categorias` | Abierto | `categorias.md, roles.md` |
| #002 | Cliente puede crear, actualizar, eliminar un producto, actualizar stock | `POST UPDATE {{baseUrl}}/productos` | Abierto | `roles.md,  productos.md` |
| #003 | Filtrar por precio y categoria no tiene algun uso | `GET {{baseUrl}}/productos/filtrar` | Abierto | `busqueda.md` |
| #004 | Buscar por nombre no tiene algun uso | `GET {{baseUrl}}/productos/buscar` | Abierto | `busqueda.md` |
| #005 | Buscar por filtro combinado no se sabe como usarse | `GET {{baseUrl}}/productos/filtrar` | Abierto | `busqueda.md` |
---
 
## Detalles de Errores
 
### #001 - Cliente puede crear, actualizar, eliminar, crear subcategoria
 
**Endpoint:** `POST, PUT, DEL {{baseUrl}}/categorias`
 
**Resumen:** El sistema acepta que un usuario con rol de cliente pueda crear una categoria, actualizarla, eliminarla, crear una subcategoria
 
**Especificación Gherkin:** `categorias.md, roles.md`
 
 
 
**Resultado esperado:**
Un mensaje de error que tenga que indique que no se pueda realizar esta accion.
 
---
 
### #002 - Cliente puede crear, actualizar un producto, actualizar stock

**Endpoint:** `POST UPDATE {{baseUrl}}/productos`

**Resumen:** Un usuario con rol de cliente no puede crear un producto, se le debe de impedir esa accion.

**Especificación Gherkin:** `roles.md,  productos.md`


**Dato de entrada:**
```json
{
  "nombre": "Audífonos Sony V2",
  "precio": 19.99,
  "stock": 20,
  "categoriaId": 1
}
```

**Resultado esperado:**
```json
mensaje de error o rechazo de accion.
```

**Resultado actual:**
```json
{
    "id": 8,
    "nombre": "Audífonos Sony V2",
    "precio": 19.99,
    "stock": 20,
    "categoriaId": 1,
    "categoriaNombre": "Electrónica"
}
```

---


### #003 - Filtrar por precio y categoria no tiene algun uso

**Endpoint:** `GET {{baseUrl}}/productos/filtrar`

**Resumen:** Al ejecutar este endpoint en POSTMAN aparecen todos los productos y en la terminal aparece esto:
(precio)
```bash
Hibernate: select p1_0.id,p1_0.categoria_id,p1_0.nombre,p1_0.precio,p1_0.stock from productos p1_0 limit ?,?
Hibernate: select c1_0.id,c1_0.nombre,p1_0.id,p1_0.nombre,p1_0.padre_id from categorias c1_0 left join categorias p1_0 on p1_0.id=c1_0.padre_id where c1_0.id=?
Hibernate: select c1_0.id,c1_0.nombre,p1_0.id,p1_0.nombre,p1_0.padre_id from categorias c1_0 left join categorias p1_0 on p1_0.id=c1_0.padre_id where c1_0.id=?
Hibernate: select c1_0.id,c1_0.nombre,p1_0.id,p1_0.nombre,p1_0.padre_id from categorias c1_0 left join categorias p1_0 on p1_0.id=c1_0.padre_id where c1_0.id=?
```
(categoria)
```bash
Hibernate: select p1_0.id,p1_0.categoria_id,p1_0.nombre,p1_0.precio,p1_0.stock from productos p1_0 limit ?,?
Hibernate: select c1_0.id,c1_0.nombre,p1_0.id,p1_0.nombre,p1_0.padre_id from categorias c1_0 left join categorias p1_0 on p1_0.id=c1_0.padre_id where c1_0.id=?
Hibernate: select c1_0.id,c1_0.nombre,p1_0.id,p1_0.nombre,p1_0.padre_id from categorias c1_0 left join categorias p1_0 on p1_0.id=c1_0.padre_id where c1_0.id=?
Hibernate: select c1_0.id,c1_0.nombre,p1_0.id,p1_0.nombre,p1_0.padre_id from categorias c1_0 left join categorias p1_0 on p1_0.id=c1_0.padre_id where c1_0.id=?
```
**Especificación Gherkin:** `busqueda.md`


**Dato de entrada:**
```json
{}
```

**Resultado esperado:**
Productos con filtro de precio

**Resultado actual:**
```json
{
    "content": [
        {
            "id": 1,
            "nombre": "iPhone 15",
            "precio": 999.99,
            "stock": 50,
            "categoriaId": 2,
            "categoriaNombre": "Celulares"
        },
        {
            "id": 2,
            "nombre": "Samsung Galaxy S24",
            "precio": 899.99,
            "stock": 30,
            "categoriaId": 2,
            "categoriaNombre": "Celulares"
        },
        {
            "id": 3,
            "nombre": "MacBook Pro",
            "precio": 1999.99,
            "stock": 15,
            "categoriaId": 3,
            "categoriaNombre": "Laptops"
        }
    ],
    "empty": false,
    "first": true,
    "last": true,
    "number": 0,
    "numberOfElements": 8,
    "pageable": {
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 20,
        "paged": true,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "unpaged": false
    },
    "size": 20,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "totalElements": 8,
    "totalPages": 1
}
```
### #004 - Buscar por nombre no tiene algun uso

**Endpoint:** `GET {{baseUrl}}/productos/buscar`

**Resumen:** Se debe colocar en la descripcion de la coleccion .json un ejemplo de como usarse a nivel del backend con datos validos.

**Especificación Gherkin:** `busqueda.md`

```

---
---
### #005 - Buscar por filtro combinado no se sabe como usarse

**Endpoint:** `GET {{baseUrl}}/productos/filtrar`

**Resumen:** No se sabe como utilizar este endpoint.

**Especificación Gherkin:** `busqueda.md`

**Descripción del error:**
Tanto como el #0003 y #0004 se recomienda colocar en la descripcion del .json del postman en las partes de busquedas un ejemplo de como usar esta endpoint. Sino el desarrollador tendra problemas para entender el endpoint. Con ejemplos me refiero a colocar como se usa y que es lo que se deberia mostrar.

**Dato de entrada:**
```json
{
}
```

**Resultado esperado:**
```json
Productos filtrado por nombre, categoria, rango de precio
```

**Resultado actual:**
```json
{
    "content": [
        {
            "id": 1,
            "nombre": "iPhone 15 Pro",
            "precio": 1099.99,
            "stock": 75,
            "categoriaId": 2,
            "categoriaNombre": "Celulares"
        },
        {
            "id": 2,
            "nombre": "Samsung Galaxy S24",
            "precio": 899.99,
            "stock": 30,
            "categoriaId": 2,
            "categoriaNombre": "Celulares"
        },
        {
            "id": 3,
            "nombre": "MacBook Pro",
            "precio": 1999.99,
            "stock": 15,
            "categoriaId": 3,
            "categoriaNombre": "Laptops"
        },
        {
            "id": 4,
            "nombre": "Dell XPS 13",
            "precio": 1299.99,
            "stock": 20,
            "categoriaId": 3,
            "categoriaNombre": "Laptops"
        }
    ],
    "empty": false,
    "first": true,
    "last": false,
    "number": 0,
    "numberOfElements": 20,
    "pageable": {
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 20,
        "paged": true,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "unpaged": false
    },
    "size": 20,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "totalElements": 61,
    "totalPages": 4
}
```


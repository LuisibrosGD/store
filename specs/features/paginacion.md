# Features: Paginación

## Descripción general

Los listados de productos y categorías soportan paginación para manejar
grandes volúmenes de datos de forma eficiente.

---

## Paginación de productos

```gherkin
Feature: Paginación de productos

  Background:
    Given existen 25 productos registrados

  Scenario Outline: El cliente solicita una página de productos
    When el cliente solicita la página <page> con tamaño <size>
    Then el sistema retorna <cantidad> productos
     y la respuesta incluye metadata:
       | campo          | valor      |
       | totalElements  | 25         |
       | totalPages     | 3          |
       | currentPage    | <page>     |
       | size           | <size>     |

    Examples:
      | page | size | cantidad |
      | 1    | 10   | 10       |
      | 2    | 10   | 10       |
      | 3    | 10   | 5        |

  Scenario: El cliente solicita una página sin parámetros
    When el cliente solicita productos sin especificar página ni tamaño
    Then el sistema retorna la página 1 con tamaño por defecto de 10

  Scenario: El cliente solicita una página fuera de rango
    When el cliente solicita la página 10 con tamaño 10
    Then el sistema retorna una lista vacía
     y la respuesta incluye metadata con currentPage = 10 y totalPages = 3
```

---

## Paginación de categorías

```gherkin
Feature: Paginación de categorías

  Background:
    Given existen 15 categorías registradas

  Scenario: El cliente solicita categorías paginadas
    When el cliente solicita la página 1 de categorías con tamaño 5
    Then el sistema retorna 5 categorías
     y la respuesta incluye metadata:
       | campo          | valor |
       | totalElements  | 15    |
       | totalPages     | 3     |
       | currentPage    | 1     |
       | size           | 5     |
```

---

## Paginación con filtros

```gherkin
Feature: Paginación combinada con filtros

  Background:
    Given existen 30 productos y 12 contienen "Phone" en el nombre

  Scenario: El cliente pagina resultados de una búsqueda
    When el cliente busca "Phone" en la página 1 con tamaño 5
    Then el sistema retorna 5 productos que contienen "Phone"
     y la respuesta incluye metadata con totalElements = 12 y totalPages = 3

  Scenario: El cliente pagina resultados filtrados por categoría
    Given la categoría 1 tiene 20 productos
    When el cliente filtra por categoría 1 en la página 2 con tamaño 10
    Then el sistema retorna 10 productos de la categoría 1 (los productos 11 al 20)
     y la respuesta incluye metadata con totalElements = 20 y totalPages = 2
```

---

## Notas
- Los parámetros de paginación son: `page` (número de página, inicia en 1) y `size` (tamaño de página).
- Valores por defecto: página 1, tamaño 10.
- La metadata de respuesta incluye: totalElements, totalPages, currentPage, size.
- Los filtros de búsqueda y los parámetros de paginación se pueden combinar.

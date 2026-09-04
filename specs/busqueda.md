# Features: Búsqueda y Filtros de Productos

## Descripción general

Permite buscar y filtrar productos por nombre, categoría y rango de precio.
Los filtros se pueden combinar entre sí.

---

## Buscar por nombre

```gherkin
Feature: Buscar productos por nombre

  Background:
    Given existen los siguientes productos:
      | nombre          | precio  | categoriaId |
      | iPhone 15       | 999.99  | 1           |
      | iPhone 14       | 799.99  | 1           |
      | Samsung Galaxy  | 899.99  | 1           |

  Scenario: El cliente busca productos por nombre
    When el cliente busca productos con el texto "iPhone"
    Then el sistema retorna los productos cuyo nombre contiene "iPhone"
     y se muestran "iPhone 15" e "iPhone 14"

  Scenario: El cliente busca un nombre que no coincide con ningún producto
    When el cliente busca productos con el texto "xyz123"
    Then el sistema retorna una lista vacía
```

---

## Filtrar por categoría

```gherkin
Feature: Filtrar productos por categoría

  Background:
    Given existen los siguientes productos:
      | nombre        | precio  | categoriaId |
      | iPhone 15     | 999.99  | 1           |
      | Camisa Azul   | 29.99   | 2           |
      | Samsung Galaxy| 899.99  | 1           |

  Scenario: El cliente filtra productos por categoría
    When el cliente filtra productos por la categoría 1
    Then el sistema retorna solo los productos de la categoría 1
     y se muestran "iPhone 15" y "Samsung Galaxy"

  Scenario: El cliente filtra por una categoría sin productos
    When el cliente filtra productos por la categoría 10
    Then el sistema retorna una lista vacía
```

---

## Filtrar por precio

```gherkin
Feature: Filtrar productos por precio

  Background:
    Given existen los siguientes productos:
      | nombre        | precio  | categoriaId |
      | iPhone 15     | 999.99  | 1           |
      | Camisa Azul   | 29.99   | 2           |
      | Samsung Galaxy| 899.99  | 1           |

  Scenario Outline: El cliente filtra productos por rango de precio
    When el cliente filtra productos con precio mínimo <min> y máximo <max>
    Then el sistema retorna los productos con precio entre <min> y <max>
     y se muestran <resultados>

    Examples:
      | min | max  | resultados                    |
      | 500 | 9999 | "iPhone 15" y "Samsung Galaxy"|
      | 0   | 100  | "Camisa Azul"                 |
      | 50  | 900  | "Samsung Galaxy"              |
```

---

## Combinar filtros

```gherkin
Feature: Combinar múltiples filtros

  Background:
    Given existen los siguientes productos:
      | nombre          | precio | categoriaId |
      | iPhone 15       | 999.99 | 1           |
      | iPhone 14       | 799.99 | 1           |
      | Camisa Azul     | 29.99  | 2           |

  Scenario: El cliente combina búsqueda por nombre y categoría
    When el cliente busca "iPhone" en la categoría 1
    Then el sistema retorna los productos que coinciden con ambos filtros
     y se muestran "iPhone 15" e "iPhone 14"

  Scenario: El cliente combina categoría y rango de precio
    When el cliente filtra productos de la categoría 1 con precio máximo 900
    Then el sistema retorna los productos de la categoría 1 con precio ≤ 900
     y se muestra "iPhone 14"

  Scenario: Los filtros no coinciden con ningún producto
    When el cliente busca "Camisa" en la categoría 1
    Then el sistema retorna una lista vacía
```

---

## Notas
- La búsqueda por nombre es parcial (contiene) y no distingue mayúsculas/minúsculas.
- Los filtros se aplican de forma acumulativa (AND).
- Si no se proporciona filtro, se retornan todos los productos.

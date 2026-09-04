# Features: Búsqueda y Filtros de Productos

## Descripción general

Permite buscar y filtrar productos por nombre, categoría y rango de precio.
Los filtros se pueden combinar entre sí.

---

## Buscar por nombre

```gherkin
Feature: Buscar productos por nombre

  Scenario: El cliente busca productos por nombre
    Given existen los siguientes productos:
      | nombre          | precio  | categoriaId |
      | iPhone 15       | 999.99  | 1           |
      | iPhone 14       | 799.99  | 1           |
      | Samsung Galaxy  | 899.99  | 1           |
    When el cliente busca productos con el texto "iPhone"
    Then el sistema retorna los productos cuyo nombre contiene "iPhone"
     y se muestran "iPhone 15" e "iPhone 14"

  Scenario: El cliente busca un nombre que no coincide con ningún producto
    Given existen productos registrados
    When el cliente busca productos con el texto "xyz123"
    Then el sistema retorna una lista vacía
```

---

## Filtrar por categoría

```gherkin
Feature: Filtrar productos por categoría

  Scenario: El cliente filtra productos por categoría
    Given existen los siguientes productos:
      | nombre        | precio | categoriaId |
      | iPhone 15     | 999.99 | 1           |
      | Camisa Azul   | 29.99  | 2           |
      | Samsung Galaxy| 899.99 | 1           |
    When el cliente filtra productos por la categoría 1
    Then el sistema retorna solo los productos de la categoría 1
     y se muestran "iPhone 15" y "Samsung Galaxy"

  Scenario: El cliente filtra por una categoría sin productos
    Given la categoría "Vacía" existe con id 10
    And no hay productos en la categoría 10
    When el cliente filtra productos por la categoría 10
    Then el sistema retorna una lista vacía
```

---

## Filtrar por precio

```gherkin
Feature: Filtrar productos por precio

  Scenario: El cliente filtra productos por precio mínimo
    Given existen los siguientes productos:
      | nombre        | precio  | categoriaId |
      | iPhone 15     | 999.99  | 1           |
      | Camisa Azul   | 29.99   | 2           |
      | Samsung Galaxy| 899.99  | 1           |
    When el cliente filtra productos con precio mínimo de 500
    Then el sistema retorna solo los productos con precio mayor o igual a 500
     y se muestran "iPhone 15" y "Samsung Galaxy"

  Scenario: El cliente filtra productos por precio máximo
    Given existen los productos mencionados anteriormente
    When el cliente filtra productos con precio máximo de 100
    Then el sistema retorna solo los productos con precio menor o igual a 100
     y se muestra "Camisa Azul"

  Scenario: El cliente filtra productos por rango de precio
    Given existen los productos mencionados anteriormente
    When el cliente filtra productos con precio mínimo 50 y máximo 900
    Then el sistema retorna los productos con precio entre 50 y 900
     y se muestra "Samsung Galaxy"
```

---

## Combinar filtros

```gherkin
Feature: Combinar múltiples filtros

  Scenario: El cliente combina búsqueda por nombre y categoría
    Given existen los siguientes productos:
      | nombre          | precio | categoriaId |
      | iPhone 15       | 999.99 | 1           |
      | iPhone 14       | 799.99 | 1           |
      | Camisa Azul     | 29.99  | 2           |
    When el cliente busca "iPhone" en la categoría 1
    Then el sistema retorna los productos que coinciden con ambos filtros
     y se muestran "iPhone 15" e "iPhone 14"

  Scenario: El cliente combina categoría y rango de precio
    Given existen los productos mencionados anteriormente
    When el cliente filtra productos de la categoría 1 con precio máximo 900
    Then el sistema retorna los productos de la categoría 1 con precio ≤ 900
     y se muestra "iPhone 14"

  Scenario: Los filtros no coinciden con ningún producto
    Given existen los productos mencionados anteriormente
    When el cliente busca "Camisa" en la categoría 1
    Then el sistema retorna una lista vacía
```

---

## Notas
- La búsqueda por nombre es parcial (contiene) y no distingue mayúsculas/minúsculas.
- Los filtros se aplican de forma acumulativa (AND).
- Si no se proporciona filtro, se retornan todos los productos.

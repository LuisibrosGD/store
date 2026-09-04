# Features: Carrito de Compras

## Descripción general

El cliente agrega productos a su carrito de compras, puede modificar cantidades
y ver el total antes de realizar la compra. El carrito es por usuario.

---

## Agregar un producto al carrito

```gherkin
Feature: Agregar un producto al carrito

  Scenario: El cliente agrega un producto a su carrito
    Given el producto "iPhone 15" está registrado con id 1 y stock 50
    And el cliente tiene un carrito vacío
    When el cliente agrega 2 unidades del producto 1 a su carrito
    Then el carrito contiene 1 ítem
     And el ítem es "iPhone 15" con cantidad 2
     And el subtotal del ítem es 1999.98

  Scenario: El cliente agrega el mismo producto nuevamente
    Given el producto "iPhone 15" está registrado con id 1
    And el cliente ya tiene 2 unidades del producto 1 en su carrito
    When el cliente agrega 3 unidades más del producto 1 a su carrito
    Then el ítem "iPhone 15" ahora tiene cantidad 5

  Scenario: El cliente intenta agregar un producto sin stock
    Given el producto "Agotado" está registrado con id 2 y stock 0
    When el cliente intenta agregar 1 unidad del producto 2 a su carrito
    Then el sistema informa que el producto no tiene stock disponible
         y no se agrega al carrito

  Scenario: El cliente intenta agregar más unidades de las disponibles
    Given el producto "iPhone 15" está registrado con id 1 y stock 5
    When el cliente intenta agregar 10 unidades del producto 1 a su carrito
    Then el sistema informa que no hay stock suficiente
         y no se agrega al carrito

  Scenario: El cliente intenta agregar un producto que no existe
    Given el producto con identificación "999" no está registrado
    When el cliente intenta agregar 1 unidad del producto 999 a su carrito
    Then el sistema informa que el producto no fue encontrado
         y no se modifica el carrito
```

---

## Cambiar cantidad de un ítem

```gherkin
Feature: Cambiar cantidad de un ítem en el carrito

  Scenario: El cliente cambia la cantidad de un ítem
    Given el cliente tiene 2 unidades del producto "iPhone 15" (id 1) en su carrito
    When el cliente cambia la cantidad del producto 1 a 4
    Then el ítem "iPhone 15" ahora tiene cantidad 4

  Scenario: El cliente pone la cantidad a cero para quitar el ítem
    Given el cliente tiene 2 unidades del producto "iPhone 15" (id 1) en su carrito
    When el cliente cambia la cantidad del producto 1 a 0
    Then el ítem "iPhone 15" se elimina del carrito

  Scenario: El cliente intenta poner una cantidad mayor al stock
    Given el cliente tiene 2 unidades del producto "iPhone 15" (id 1) en su carrito
    And el producto "iPhone 15" tiene stock 3
    When el cliente intenta cambiar la cantidad del producto 1 a 10
    Then el sistema informa que no hay stock suficiente
         y la cantidad no se modifica

  Scenario: El cliente intenta cambiar la cantidad de un ítem que no está en su carrito
    Given el cliente no tiene el producto con id 5 en su carrito
    When el cliente intenta cambiar la cantidad del producto 5 a 2
    Then el sistema informa que el producto no está en el carrito
         y no se modifica el carrito
```

---

## Quitar un producto del carrito

```gherkin
Feature: Quitar un producto del carrito

  Scenario: El cliente quita un producto de su carrito
    Given el cliente tiene 2 unidades del producto "iPhone 15" (id 1) en su carrito
    When el cliente quita el producto 1 de su carrito
    Then el carrito ya no contiene el producto "iPhone 15"
         y el carrito queda vacío

  Scenario: El cliente intenta quitar un producto que no está en su carrito
    Given el cliente no tiene el producto con id 5 en su carrito
    When el cliente intenta quitar el producto 5 de su carrito
    Then el sistema informa que el producto no está en el carrito
         y no se modifica el carrito
```

---

## Consultar el carrito

```gherkin
Feature: Consultar el carrito

  Scenario: El cliente consulta su carrito con ítems
    Given el cliente tiene en su carrito:
      | productoId | cantidad |
      | 1          | 2        |
      | 3          | 1        |
    When el cliente solicita ver su carrito
    Then el carrito muestra 2 ítems
     And el total del carrito es la suma de los subtotales de cada ítem

  Scenario: El cliente consulta su carrito vacío
    Given el cliente tiene un carrito vacío
    When el cliente solicita ver su carrito
    Then el carrito muestra 0 ítems
     y el total del carrito es 0

  Scenario: El cliente limpia su carrito
    Given el cliente tiene ítems en su carrito
    When el cliente vacía el carrito
    Then el carrito queda vacío
     y el total del carrito es 0
```

---

## Notas
- El carrito es temporal y pertenece a cada usuario autenticado.
- El stock se valida al agregar y al modificar cantidades.
- El total se calcula sumando (precio × cantidad) de cada ítem.
- Al confirmar la compra (checkout), el carrito se convierte en una orden.

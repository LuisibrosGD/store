# Features: Órdenes y Compras

## Descripción general

El cliente realiza una compra desde su carrito de compras, lo que genera una
orden con un estado que avanza a lo largo del ciclo de vida. El cliente puede
consultar su historial de órdenes.

---

## Crear una orden (checkout)

```gherkin
Feature: Crear una orden desde el carrito

  Scenario: El cliente realiza una compra exitosa
    Given el cliente tiene en su carrito:
      | productoId | cantidad |
      | 1          | 2        |
      | 3          | 1        |
    When el cliente confirma la compra
    Then se crea una orden con estado PENDIENTE
     And el stock de los productos se reduce según la cantidad comprada
     And el carrito del cliente queda vacío
     And la orden contiene los ítems comprados con sus precios

  Scenario: El cliente intenta comprar con un carrito vacío
    Given el cliente tiene un carrito vacío
    When el cliente intenta confirmar la compra
    Then el sistema informa que el carrito está vacío
         y no se crea ninguna orden

  Scenario: El cliente intenta comprar un producto sin stock suficiente
    Given el cliente tiene 5 unidades del producto "iPhone 15" (id 1) en su carrito
    And el producto "iPhone 15" tiene stock 2
    When el cliente intenta confirmar la compra
    Then el sistema informa que no hay stock suficiente para el producto "iPhone 15"
         y no se crea la orden
         y el carrito no se modifica
```

---

## Consultar una orden

```gherkin
Feature: Consultar una orden

  Background:
    Given el cliente tiene una orden con id 1

  Scenario: El cliente consulta los datos de una orden
    When el cliente solicita ver los datos de la orden 1
    Then el sistema muestra los datos completos de la orden
         y la orden incluye el listado de ítems
         y el estado de la orden es PENDIENTE

  Scenario: El cliente consulta una orden que no existe
    When el cliente solicita ver los datos de la orden 999
    Then el sistema informa que la orden no fue encontrada

  Scenario: Un cliente consulta una orden que no le pertenece
    When el cliente "María" solicita ver los datos de la orden 1
    Then el sistema informa que la orden no fue encontrada
```

---

## Historial de órdenes

```gherkin
Feature: Historial de órdenes del cliente

  Scenario: El cliente consulta su historial de órdenes
    Given el cliente tiene las siguientes órdenes:
      | id | estado    |
      | 1  | PENDIENTE |
      | 2  | PAGADA    |
    When el cliente solicita ver su historial de órdenes
    Then el sistema retorna las órdenes del cliente
     y las órdenes aparecen ordenadas de la más reciente a la más antigua

  Scenario: El cliente consulta su historial y no tiene órdenes
    Given el cliente no tiene órdenes registradas
    When el cliente solicita ver su historial de órdenes
    Then el sistema retorna una lista vacía
```

---

## Estados de la orden

```gherkin
Feature: Gestión de estados de una orden

  Scenario Outline: El administrador cambia el estado de una orden
    Given la orden 1 tiene estado <estadoActual>
    When el administrador cambia el estado de la orden 1 a <nuevoEstado>
    Then el sistema confirma el cambio de estado
         y la orden ahora tiene estado <nuevoEstado>

    Examples:
      | estadoActual | nuevoEstado |
      | PENDIENTE    | PAGADA      |
      | PAGADA       | ENVIADA     |
      | ENVIADA      | ENTREGADA   |

  Scenario: El cliente cancela una orden pendiente
    Given la orden 1 tiene estado PENDIENTE
    When el cliente cancela la orden 1
    Then el sistema confirma la cancelación
         y la orden ahora tiene estado CANCELADA
         y el stock de los productos se restaura

  Scenario: El cliente intenta cancelar una orden que no está pendiente
    Given la orden 1 tiene estado PAGADA
    When el cliente intenta cancelar la orden 1
    Then el sistema informa que solo se pueden cancelar órdenes pendientes
         y no se realiza ningún cambio

  Scenario: El administrador intenta poner un estado inválido
    Given la orden 1 tiene estado PENDIENTE
    When el administrador intenta cambiar el estado de la orden 1 a "EN_PROCESO"
    Then el sistema informa que el estado proporcionado no es válido
         y no se realiza ningún cambio
```

---

## Notas
- Los estados válidos son: PENDIENTE, PAGADA, ENVIADA, ENTREGADA, CANCELADA.
- Solo se puede cancelar una orden en estado PENDIENTE.
- Al cancelar, el stock de los productos se restaura.
- El cliente solo puede ver sus propias órdenes.
- El administrador puede gestionar el estado de cualquier orden.

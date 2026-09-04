# Features: Roles de Usuario

## Descripción general

El sistema maneja dos roles: ADMIN y CLIENTE. Cada rol tiene permisos
diferentes sobre los recursos del sistema.

---

## Administrador (ADMIN)

```gherkin
Feature: Permisos del administrador

  Background:
    Given un usuario autenticado con rol ADMIN

  Scenario Outline: El administrador puede gestionar <recurso>
    When el administrador realiza operaciones CRUD sobre <recurso>
    Then todas las operaciones son permitidas

    Examples:
      | recurso   |
      | productos |
      | categorías|
      | usuarios  |

  Scenario: El administrador puede gestionar el estado de las órdenes
    When el administrador cambia el estado de una orden
    Then la operación es permitida

  Scenario: El administrador puede ver todas las órdenes
    When el administrador solicita ver todas las órdenes del sistema
    Then el sistema retorna todas las órdenes de todos los clientes
```

---

## Cliente (CLIENTE)

```gherkin
Feature: Permisos del cliente

  Background:
    Given un usuario autenticado con rol CLIENTE

  Scenario: El cliente puede ver productos
    When el cliente solicita ver los productos
    Then la operación es permitida

  Scenario: El cliente puede gestionar su carrito
    When el cliente agrega, modifica o quita productos de su carrito
    Then todas las operaciones sobre su carrito son permitidas

  Scenario: El cliente puede realizar compras
    When el cliente confirma la compra de su carrito
    Then la operación es permitida

  Scenario: El cliente puede ver su historial de órdenes
    When el cliente solicita ver su historial de órdenes
    Then el sistema retorna solo las órdenes del cliente autenticado

  Scenario: El cliente puede cancelar sus órdenes pendientes
    Given el cliente tiene una orden pendiente
    When el cliente cancela su orden pendiente
    Then la operación es permitida
```

---

## Restricciones del cliente

```gherkin
Feature: Acceso denegado para el cliente

  Background:
    Given un usuario autenticado con rol CLIENTE

  Scenario Outline: El cliente no puede gestionar <recurso>
    When el cliente intenta crear, actualizar o eliminar <recurso>
    Then el sistema rechaza la operación con estado 403

    Examples:
      | recurso               |
      | un producto           |
      | una categoría         |
      | otro usuario          |

  Scenario: El cliente no puede ver órdenes de otros clientes
    When el cliente intenta ver una orden que no le pertenece
    Then el sistema rechaza la operación con estado 403

  Scenario: El cliente no puede cambiar estados de órdenes
    When el cliente intenta cambiar el estado de una orden
    Then el sistema rechaza la operación con estado 403
```

---

## Acceso sin autenticación

```gherkin
Feature: Endpoints públicos

  Background:
    Given no hay token de autenticación

  Scenario Outline: Cualquier usuario puede acceder a <endpoint>
    When se envía una solicitud POST a <endpoint>
    Then la operación es permitida

    Examples:
      | endpoint              |
      | /api/usuarios/login   |
      | /api/usuarios         |

  Scenario: Se requiere autenticación para endpoints protegidos
    When se envía una solicitud a un endpoint protegido
    Then el sistema rechaza la operación con estado 401
```

---

## Notas
- Los roles se asignan al momento de crear el usuario (por defecto CLIENTE).
- Un usuario no puede cambiar su propio rol.
- El rol ADMIN tiene acceso total a todos los recursos.
- El rol CLIENTE solo puede acceder a productos, su carrito y sus propias órdenes.

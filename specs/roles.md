# Features: Roles de Usuario

## Descripción general

El sistema maneja dos roles: ADMIN y CLIENTE. Cada rol tiene permisos
diferentes sobre los recursos del sistema.

---

## Administrador (ADMIN)

```gherkin
Feature: Permisos del administrador

  Scenario: El administrador puede gestionar productos
    Given un usuario autenticado con rol ADMIN
    When el administrador realiza operaciones CRUD sobre productos
    Then todas las operaciones son permitidas

  Scenario: El administrador puede gestionar categorías
    Given un usuario autenticado con rol ADMIN
    When el administrador realiza operaciones CRUD sobre categorías
    Then todas las operaciones son permitidas

  Scenario: El administrador puede gestionar usuarios
    Given un usuario autenticado con rol ADMIN
    When el administrador realiza operaciones CRUD sobre usuarios
    Then todas las operaciones son permitidas

  Scenario: El administrador puede gestionar el estado de las órdenes
    Given un usuario autenticado con rol ADMIN
    When el administrador cambia el estado de una orden
    Then la operación es permitida

  Scenario: El administrador puede ver todas las órdenes
    Given un usuario autenticado con rol ADMIN
    When el administrador solicita ver todas las órdenes del sistema
    Then el sistema retorna todas las órdenes de todos los clientes
```

---

## Cliente (CLIENTE)

```gherkin
Feature: Permisos del cliente

  Scenario: El cliente puede ver productos
    Given un usuario autenticado con rol CLIENTE
    When el cliente solicita ver los productos
    Then la operación es permitida

  Scenario: El cliente puede gestionar su carrito
    Given un usuario autenticado con rol CLIENTE
    When el cliente agrega, modifica o quita productos de su carrito
    Then todas las operaciones sobre su carrito son permitidas

  Scenario: El cliente puede realizar compras
    Given un usuario autenticado con rol CLIENTE
    When el cliente confirma la compra de su carrito
    Then la operación es permitida

  Scenario: El cliente puede ver su historial de órdenes
    Given un usuario autenticado con rol CLIENTE
    When el cliente solicita ver su historial de órdenes
    Then el sistema retorna solo las órdenes del cliente autenticado

  Scenario: El cliente puede cancelar sus órdenes pendientes
    Given un usuario autenticado con rol CLIENTE
    And el cliente tiene una orden pendiente
    When el cliente cancela su orden pendiente
    Then la operación es permitida
```

---

## Restricciones del cliente

```gherkin
Feature: Acceso denegado para el cliente

  Scenario: El cliente no puede gestionar productos
    Given un usuario autenticado con rol CLIENTE
    When el cliente intenta crear, actualizar o eliminar un producto
    Then el sistema rechaza la operación con estado 403

  Scenario: El cliente no puede gestionar categorías
    Given un usuario autenticado con rol CLIENTE
    When el cliente intenta crear, actualizar o eliminar una categoría
    Then el sistema rechaza la operación con estado 403

  Scenario: El cliente no puede gestionar otros usuarios
    Given un usuario autenticado con rol CLIENTE
    When el cliente intenta crear, actualizar o eliminar otro usuario
    Then el sistema rechaza la operación con estado 403

  Scenario: El cliente no puede ver órdenes de otros clientes
    Given un usuario autenticado con rol CLIENTE
    When el cliente intenta ver una orden que no le pertenece
    Then el sistema rechaza la operación con estado 403

  Scenario: El cliente no puede cambiar estados de órdenes
    Given un usuario autenticado con rol CLIENTE
    When el cliente intenta cambiar el estado de una orden
    Then el sistema rechaza la operación con estado 403
```

---

## Acceso sin autenticación

```gherkin
Feature: Endpoints públicos

  Scenario: Cualquier usuario puede iniciar sesión
    Given no hay token de autenticación
    When se envía una solicitud POST a /api/usuarios/login
    Then la operación es permitida

  Scenario: Cualquier usuario puede registrar una cuenta
    Given no hay token de autenticación
    When se envía una solicitud POST a /api/usuarios
    Then la operación es permitida

  Scenario: Se requiere autenticación para endpoints protegidos
    Given no hay token de autenticación
    When se envía una solicitud a un endpoint protegido
    Then el sistema rechaza la operación con estado 401
```

---

## Notas
- Los roles se asignan al momento de crear el usuario (por defecto CLIENTE).
- Un usuario no puede cambiar su propio rol.
- El rol ADMIN tiene acceso total a todos los recursos.
- El rol CLIENTE solo puede acceder a productos, su carrito y sus propias órdenes.

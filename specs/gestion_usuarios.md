# Features: Gestión de Usuarios

## Descripción general

El administrador gestiona los usuarios del sistema, pudiendo crear,
consultar, actualizar y eliminar registros. Las operaciones se realizan a través
de la API del sistema. Solo los usuarios con rol ADMIN pueden gestionar usuarios.

---

## Crear un usuario

```gherkin
Feature: Crear un usuario

  Background:
    Given el administrador está autenticado con rol ADMIN

  Scenario: El administrador registra un usuario nuevo con rol CLIENTE
    Given el email "ana@email.com" no está registrado
    When el administrador envía una solicitud para crear un usuario con:
      | nombre     | email           | contraseña | rol     |
      | Ana García | ana@email.com   | pass123    | CLIENTE |
    Then el sistema confirma la creación del usuario
         y el usuario "Ana García" aparece en el listado
         y el usuario tiene rol CLIENTE
         y puede iniciar sesión con sus credenciales

  Scenario: El administrador registra un usuario nuevo con rol ADMIN
    Given el email "admin_nuevo@email.com" no está registrado
    When el administrador envía una solicitud para crear un usuario con:
      | nombre       | email                 | contraseña | rol  |
      | Pedro Admin  | admin_nuevo@email.com | admin123   | ADMIN |
    Then el sistema confirma la creación del usuario
         y el usuario "Pedro Admin" aparece en el listado
         y el usuario tiene rol ADMIN

  Scenario: El administrador registra un usuario sin especificar rol
    Given el email "sinrol@email.com" no está registrado
    When el administrador envía una solicitud para crear un usuario con:
      | nombre     | email             | contraseña |
      | Luis Gómez | sinrol@email.com  | pass123    |
    Then el sistema confirma la creación del usuario
         y el usuario tiene rol CLIENTE por defecto

  Scenario: El administrador intenta registrar un usuario con email ya registrado
    Given el email "carlos@email.com" ya está registrado
    When el administrador envía una solicitud para crear un usuario con:
      | nombre     | email            | contraseña |
      | Juan Pérez | carlos@email.com | 1234       |
    Then el sistema informa que el email ya está en uso
         y no se crea un nuevo usuario

  Scenario: El administrador intenta registrar un usuario sin contraseña
    Given el email "luis@email.com" no está registrado
    When el administrador envía una solicitud para crear un usuario con:
      | nombre     | email           | contraseña |
      | Luis Gómez | luis@email.com  |            |
    Then el sistema indica que la contraseña es obligatoria
         y no se crea un nuevo usuario

  Scenario: El administrador intenta registrar un usuario con rol inválido
    Given el email "otro@email.com" no está registrado
    When el administrador envía una solicitud para crear un usuario con:
      | nombre     | email           | contraseña | rol      |
      | Test User  | otro@email.com  | pass123    | SUPERVISOR |
    Then el sistema indica que el rol debe ser ADMIN o CLIENTE
         y no se crea un nuevo usuario

  Scenario: Un cliente no puede crear usuarios
    Given un usuario autenticado con rol CLIENTE
    When el cliente envía una solicitud para crear un usuario con:
      | nombre     | email             | contraseña |
      | Nuevo User | nuevo@email.com   | pass123    |
    Then el sistema rechaza la operación con estado 403
```
---
## Consultar usuarios
```gherkin

Feature: Consultar usuarios

  Background:
    Given el administrador está autenticado con rol ADMIN

  Scenario: El administrador consulta todos los usuarios registrados
    Given existen usuarios registrados en el sistema
    When el administrador solicita ver todos los usuarios
    Then el sistema muestra el listado completo
         y se ven "Carlos Pérez" y "María López" en la lista

  Scenario: El administrador consulta los datos de un usuario específico
    Given el usuario "Carlos Pérez" está registrado
    When el administrador solicita ver los datos del usuario "Carlos Pérez"
    Then el sistema muestra sus datos completos
         y su email es "carlos@email.com"

  Scenario: El administrador consulta un usuario que no existe
    Given el usuario con identificación "999" no está registrado
    When el administrador solicita ver los datos del usuario "999"
    Then el sistema informa que el usuario no fue encontrado

```
---
## Actualizar un usuario
```gherkin
Feature: Actualizar un usuario

  Background:
    Given el administrador está autenticado con rol ADMIN

  Scenario: El administrador actualiza los datos de un usuario existente
    Given el usuario "Carlos Pérez" está registrado con email "carlos@email.com"
    When el administrador envía una solicitud para actualizar al usuario "Carlos Pérez" con:
      | nombre            | email                   | contraseña |
      | Carlos Actualizado| carlos_nuevo@email.com  | nueva123   |
    Then el sistema confirma la actualización
         y el usuario ahora se llama "Carlos Actualizado"
         y su nuevo email es "carlos_nuevo@email.com"
         y puede iniciar sesión con la nueva contraseña

  Scenario: El administrador actualiza solo el email de un usuario
    Given el usuario "María López" está registrado con email "maria@email.com"
    When el administrador cambia el email de "María López" a "maria_nueva@email.com"
    Then el sistema confirma el cambio de email
         y el usuario ahora tiene el email "maria_nueva@email.com"
         y el resto de sus datos permanecen igual

  Scenario: El administrador intenta actualizar un usuario que no existe
    Given el usuario con identificación "999" no está registrado
    When el administrador intenta actualizar los datos del usuario "999"
    Then el sistema informa que el usuario no fue encontrado
         y no se realiza ninguna actualización

  Scenario: El administrador intenta actualizar un usuario con email ya usado por otro
    Given el usuario "Carlos Pérez" tiene el email "carlos@email.com"
         y el usuario "María López" tiene el email "maria@email.com"
    When el administrador intenta cambiar el email de "Carlos Pérez" a "maria@email.com"
    Then el sistema informa que ese email ya está siendo usado por otro usuario
         y no se realiza la actualización
```
---
## Eliminar un usuario
```gherkin
Feature: Eliminar un usuario

  Background:
    Given el administrador está autenticado con rol ADMIN

  Scenario: El administrador elimina un usuario existente
    Given el usuario "Carlos Pérez" está registrado
    When el administrador elimina al usuario "Carlos Pérez"
    Then el sistema confirma la eliminación
         y el usuario ya no aparece en el listado
         y no puede iniciar sesión con sus credenciales

  Scenario: El administrador intenta eliminar un usuario que no existe
    Given el usuario con identificación "999" no está registrado
    When el administrador intenta eliminar al usuario "999"
    Then el sistema informa que el usuario no fue encontrado
         y no se realiza ninguna eliminación
```

---

## Iniciar sesión

```gherkin
Feature: Iniciar sesión

  Scenario: Un usuario inicia sesión con credenciales correctas
    Given el usuario "Carlos Pérez" está registrado con email "carlos@email.com" y contraseña "1234"
    When el usuario intenta iniciar sesión con email "carlos@email.com" y contraseña "1234"
    Then el sistema permite el acceso
         y muestra los datos del usuario "Carlos Pérez"

  Scenario: Un usuario intenta iniciar sesión con contraseña incorrecta
    Given el usuario "Carlos Pérez" está registrado con email "carlos@email.com" y contraseña "1234"
    When el usuario intenta iniciar sesión con email "carlos@email.com" y contraseña "wrong"
    Then el sistema informa que las credenciales son incorrectas
         y no permite el acceso

  Scenario: Un usuario intenta iniciar sesión con email no registrado
    Given el email "noexiste@email.com" no está registrado
    When el usuario intenta iniciar sesión con email "noexiste@email.com" y contraseña "1234"
    Then el sistema informa que el email no está registrado
         y no permite el acceso
```
---

## Notas
- Las contraseñas se muestran en texto plano en los escenarios solo para fines
de prueba. En la implementación real deben estar encriptadas.

- Los IDs de usuario (como "999") son internos del sistema y se asignan
automáticamente al crear un nuevo registro.

- El sistema debe validar que el email tenga un formato correcto antes de
registrar o actualizar un usuario.

- La gestión de usuarios es independiente del resto del sistema de órdenes y
catálogo de productos.
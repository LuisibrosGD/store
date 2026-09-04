# Features: Autenticación JWT

## Descripción general

El sistema utiliza JSON Web Tokens (JWT) para la autenticación. Al iniciar
sesión se genera un token que debe enviarse en las solicitudes a endpoints
protegidos.

---

## Iniciar sesión

```gherkin
Feature: Iniciar sesión y obtener token

  Scenario: Un usuario inicia sesión con credenciales correctas
    Given el usuario "Carlos" está registrado con email "carlos@email.com" y contraseña "1234"
    When el usuario envía una solicitud de login con email "carlos@email.com" y contraseña "1234"
    Then el sistema retorna un token JWT válido
     y el token contiene el id del usuario
     y el token contiene el rol del usuario

  Scenario: Un usuario intenta iniciar sesión con contraseña incorrecta
    Given el usuario "Carlos" está registrado con email "carlos@email.com" y contraseña "1234"
    When el usuario envía una solicitud de login con email "carlos@email.com" y contraseña "wrong"
    Then el sistema informa que las credenciales son incorrectas
         y no se genera ningún token

  Scenario: Un usuario intenta iniciar sesión con email no registrado
    Given el email "noexiste@email.com" no está registrado
    When el usuario envía una solicitud de login con email "noexiste@email.com" y contraseña "1234"
    Then el sistema informa que el email no está registrado
         y no se genera ningún token
```

---

## Acceder con token

```gherkin
Feature: Acceder a endpoints protegidos con token

  Scenario: Un usuario accede a un endpoint con token válido
    Given el usuario tiene un token JWT válido
    When el usuario envía una solicitud a un endpoint protegido con el token
    Then el sistema procesa la solicitud normalmente

  Scenario: Un usuario accede a un endpoint sin token
    Given no hay token de autenticación
    When el usuario envía una solicitud a un endpoint protegido
    Then el sistema rechaza la operación con estado 401
         y el mensaje indica que se requiere autenticación

  Scenario: Un usuario envía un token expirado
    Given el usuario tiene un token JWT expirado
    When el usuario envía una solicitud a un endpoint protegido con el token expirado
    Then el sistema rechaza la operación con estado 401
         y el mensaje indica que el token ha expirado

  Scenario: Un usuario envía un token inválido (manipulado)
    Given el usuario envía un token con formato incorrecto
    When el usuario envía la solicitud a un endpoint protegido
    Then el sistema rechaza la operación con estado 401
         y el mensaje indica que el token es inválido
```

---

## Refresh token

```gherkin
Feature: Renovar token

  Scenario: Un usuario renueva su token antes de que expire
    Given el usuario tiene un token JWT válido
    When el usuario solicita un nuevo token
    Then el sistema retorna un nuevo token JWT válido
     y el token anterior queda invalidado

  Scenario: Un usuario intenta renovar un token expirado
    Given el usuario tiene un token JWT expirado
    When el usuario solicita un nuevo token
    Then el sistema rechaza la operación con estado 401
         y el mensaje indica que el token ha expirado
```

---

## Notas
- El token JWT se genera al iniciar sesión y contiene: id del usuario, rol y fecha de expiración.
- El token se envía en el header `Authorization: Bearer <token>`.
- El token tiene un tiempo de expiración configurado (ej: 24 horas).
- Los endpoints públicos (login, registro) no requieren token.
- Los endpoints protegidos requieren un token válido y no expirado.

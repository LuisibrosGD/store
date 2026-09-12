# Historias de usuario: Autenticación y sesión

## HU-JWT-001 — Obtener una sesión segura

**Actor:** Usuario registrado

**Como** usuario, **quiero** obtener una sesión segura al identificarme **para** acceder a las funciones protegidas según mi rol.

**Descripción:** Una autenticación correcta genera una credencial temporal asociada con la identidad y el rol del usuario.

### Criterios de aceptación

1. Con correo y contraseña correctos, generar una credencial de sesión válida que identifique al usuario y su rol.
2. Con contraseña incorrecta, informar que las credenciales son incorrectas y no crear sesión.
3. Con correo no registrado, informar la situación y no crear sesión.

### Reglas de negocio

- La sesión contiene la identidad, el rol y una fecha de expiración.
- La vigencia predeterminada es de 24 horas.

### Dependencias

- Depende de HU-USU-001 y se relaciona con HU-USU-005.

## HU-JWT-002 — Acceder a funciones protegidas

**Actor:** Usuario autenticado

**Como** usuario, **quiero** acreditar una sesión válida **para** utilizar las funciones protegidas que me correspondan.

**Descripción:** El sistema valida la presencia, autenticidad y vigencia de la sesión antes de procesar una operación protegida.

### Criterios de aceptación

1. Procesar normalmente una operación protegida cuando la sesión sea válida.
2. Rechazar una sesión expirada e informar que ha vencido.
3. Rechazar una sesión inválida e informar que no es válida.
4. Rechazar el acceso sin sesión e informar que se requiere autenticación.

### Reglas de negocio

- Una sesión válida no sustituye los permisos del rol.

### Dependencias

- Depende de HU-JWT-001 y de las reglas de roles.

## HU-JWT-003 — Renovar una sesión

**Actor:** Usuario autenticado

**Como** usuario, **quiero** renovar mi sesión antes de su vencimiento **para** continuar usando la tienda de forma segura.

**Descripción:** La renovación reemplaza la credencial anterior por una nueva.

### Criterios de aceptación

1. Con una sesión vigente, generar una nueva credencial válida e invalidar la anterior.
2. Con una sesión expirada, rechazar la renovación e informar que la sesión ha vencido.

### Reglas de negocio

- Después de renovar, la credencial anterior no puede volver a utilizarse.

### Dependencias

- Depende de HU-JWT-001 y HU-JWT-002.

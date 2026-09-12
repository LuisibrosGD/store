# Historias de usuario: Gestión de usuarios

## HU-USU-001 — Crear usuarios

**Actor:** Administrador y visitante

**Como** administrador, **quiero** registrar cuentas con roles autorizados **para** habilitar el acceso de nuevos usuarios; **como** visitante, **quiero** registrar mi cuenta de cliente **para** utilizar la tienda.

**Descripción:** La administración puede crear cuentas CLIENTE o ADMIN. El registro público siempre crea una cuenta CLIENTE.

### Criterios de aceptación

1. Permitir al administrador registrar un usuario CLIENTE que pueda iniciar sesión.
2. Permitir al administrador registrar un usuario ADMIN.
3. Si el administrador omite el rol, asignar CLIENTE de forma predeterminada.
4. Rechazar un correo ya registrado sin crear otra cuenta.
5. Rechazar una solicitud sin contraseña.
6. Rechazar cualquier rol distinto de ADMIN o CLIENTE.
7. Impedir que un cliente autenticado cree cuentas para otros usuarios.
8. Permitir el registro público únicamente con rol CLIENTE, aunque se solicite otro rol.

### Reglas de negocio

- El correo debe tener formato válido y ser único.
- La contraseña es obligatoria y se almacena protegida.
- Los únicos roles válidos son ADMIN y CLIENTE.

### Dependencias

- La administración requiere autenticación con rol ADMIN.

## HU-USU-002 — Consultar usuarios

**Actor:** Administrador

**Como** administrador, **quiero** consultar usuarios registrados **para** revisar sus datos y roles.

**Descripción:** La consulta puede mostrar todos los usuarios o el detalle de uno.

### Criterios de aceptación

1. Mostrar el listado completo de usuarios registrados.
2. Mostrar nombre, correo y rol del usuario seleccionado.
3. Si el usuario no existe, informar que no fue encontrado.

### Reglas de negocio

- Las contraseñas nunca deben mostrarse.
- Solo un administrador puede consultar cuentas ajenas.

### Dependencias

- Depende de HU-USU-001 y de la autenticación administrativa.

## HU-USU-003 — Actualizar usuarios

**Actor:** Administrador

**Como** administrador, **quiero** actualizar cuentas de usuario **para** mantener sus datos y credenciales vigentes.

**Descripción:** La administración puede modificar nombre, correo, contraseña y rol dentro de los valores permitidos.

### Criterios de aceptación

1. Actualizar todos los datos de un usuario y permitirle iniciar sesión con la nueva contraseña.
2. Permitir modificar solo el correo conservando los demás datos.
3. Si el usuario no existe, informar que no fue encontrado y no realizar cambios.
4. Rechazar un correo utilizado por otro usuario y conservar los datos anteriores.

### Reglas de negocio

- El correo actualizado debe tener formato válido y ser único.
- Una contraseña nueva debe almacenarse protegida.

### Dependencias

- Depende de HU-USU-001 y HU-USU-002.

## HU-USU-004 — Eliminar usuarios

**Actor:** Administrador

**Como** administrador, **quiero** eliminar cuentas de usuario **para** revocar su acceso al sistema.

**Descripción:** Una cuenta eliminada deja de estar disponible y sus credenciales dejan de funcionar.

### Criterios de aceptación

1. Eliminar un usuario existente, retirarlo del listado e impedir que vuelva a iniciar sesión.
2. Si el usuario no existe, informar que no fue encontrado y no realizar ninguna eliminación.

### Reglas de negocio

- Solo un administrador puede eliminar cuentas ajenas.

### Dependencias

- Depende de HU-USU-001 y HU-USU-002.

## HU-USU-005 — Iniciar sesión

**Actor:** Usuario registrado

**Como** usuario, **quiero** iniciar sesión con mi correo y contraseña **para** acceder a las funciones autorizadas de la tienda.

**Descripción:** El sistema comprueba las credenciales y devuelve la identidad funcional del usuario autenticado.

### Criterios de aceptación

1. Con credenciales correctas, permitir el acceso y mostrar los datos del usuario.
2. Con una contraseña incorrecta, informar que las credenciales no son correctas y denegar el acceso.
3. Con un correo no registrado, informar la situación y denegar el acceso.

### Reglas de negocio

- No se debe iniciar una sesión cuando alguna credencial sea inválida.

### Dependencias

- Depende de HU-USU-001.
- Se relaciona con HU-JWT-001.

## HU-USU-006 — Gestionar el perfil propio

**Actor:** Usuario autenticado

**Como** usuario, **quiero** consultar, actualizar o eliminar mi perfil **para** administrar mis datos personales y mi permanencia en la tienda.

**Descripción:** El usuario administra exclusivamente su cuenta y no puede elevar sus propios permisos.

### Criterios de aceptación

1. Mostrar al usuario autenticado su nombre, correo y rol.
2. Permitir actualizar el nombre conservando correo y rol.
3. Permitir actualizar el correo y utilizarlo posteriormente para iniciar sesión.
4. Ignorar cualquier intento de cambiar el rol propio.
5. Rechazar un correo registrado por otra persona y conservar los datos anteriores.
6. Denegar la consulta del perfil cuando no exista autenticación.
7. Permitir eliminar la cuenta propia, impedir futuros inicios de sesión y liberar el correo para un registro posterior.

### Reglas de negocio

- El usuario solo puede gestionar su propia cuenta.
- El rol nunca puede modificarse desde la gestión del perfil.
- El correo debe ser válido y único.

### Dependencias

- Depende de HU-USU-001 y HU-USU-005.

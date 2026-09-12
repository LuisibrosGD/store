## 📋 Índice

| ID | Resumen | Endpoint | Estado | Gherkin |
|----|---------|----------|--------|---------|
| #001 | Cliente puede ver todos los usuarios | `GET /{{baseUrl}}/usuarios` | Abierto | `roles.md` |
| #002 | Cliente puede crear un usuario | `POST {{baseUrl}}/usuarios` | Abierto | `roles.md` |
| #003 | No hay un mensaje en la terminal que indique que esta prohibido que un Cliente pueda actualizar, eliminar u obtener ID de un usuario | `UPDATE {{baseUrl}}/usuarios` | Abierto | `roles.md` |
| #004 | Falta implementar la creacion de usuarios con rol de ADMIN | `POST ?` | Abierto | `roles.md` |



---

## Detalles de Errores

### #001 - Cliente puede ver todos los usuarios

**Endpoint:** `GET /{{baseUrl}}/usuarios`

**Resumen:** Cliente puede ver todos los usuarios

**Especificación Gherkin:** `roles.md`

**Descripción del error:**
Un cliente no puede ver todos los usuarios solo el administrador, que en la base de datos esta como 'ADMIN'


**Resultado esperado:**
Esta operacion no esta permitida para el CLIENTE

---


### #002 - Cliente puede crear un usuario

**Endpoint:** `POST {{baseUrl}}/usuarios`

**Resumen:** El sistema acepta que un CLIENTE pueda crear un usuario en la base de datos

**Especificación Gherkin:** `roles.md`

**Descripción del error:**
Un CLIENTE no puede crear un usuario, solo un usuario con rol ADMIN

**Dato de entrada:**
```json
{
  "nombre": "Nuevo Usuario",
  "email": "nuevo@email.com",
  "password": "pass123"
}
```

**Resultado esperado:**
No lo se, algun mensaje que diga que no esta permitido

**Resultado actual:**
```json
{
  "status": 201,
  
}
```

---

### #003 - No hay un mensaje en la terminal que indique que esta prohibido que un Cliente pueda actualizar, eliminar u obtener ID de un usuario

**Endpoint:** `UPDATE {{baseUrl}}/usuarios`

**Resumen:** Se necesita un mensaje o bueno no se si sea apropiado colocar un mensaje y dejarlo con status 403. Solicito Confirmacion y/o cambios.
**Descripcion:** A pesar de que la operacion no se puede con un usuario CLIENTE, no se si es correcto colocar un mensaje cuando la operacion no esta permitida ya que actualmente solo devuelve el status 403.
**Especificación Gherkin:** `roles.md`


**Dato de entrada:**
```json
{
  "nombre": "Nombre Actualizado",
  "email": "actualizado@email.com",
  "password": "nueva123"
}
```

**Resultado esperado:**
```json
{
  "status": 403,
  "message": "El email ya está registrado"
}
```

**Resultado actual:**
```json
{
  "status": 403,
  
}
```

---

### #004 - Falta implementar la creacion de usuarios con rol de ADMIN

**Endpoint:** `POST ?`

**Resumen:** Por defecto los usuarios se crean con el rol de CLIENTE. Si se quiere crear otro ADMIN hay un problema ya que no existe esa opcion mas que hacerla en la base de datos con un query de UPDATE.
**Especificación Gherkin:** No esta en ninguna de las especificaciones


**Dato de entrada:**
```json
{
  "nombre": "Junior Alfredo",
  "email": "alfredo_jr@email.com",
  "password": "nueva123",
  "role": "ADMIN"
}
```

**Resultado esperado:**
```json
{
  "status": 201,
  {
    "id": 10,
    "nombre": "Junior Alfredo",
    "email": "alfredo_jr@email.com",
    "role": "ADMIN"
  }
}
```

---
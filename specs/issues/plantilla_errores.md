# Errores Encontrados v1

## 📋 Índice

| ID | Resumen | Endpoint | Estado | Gherkin |
|----|---------|----------|--------|---------|
| #001 | Cliente con email duplicado en registro | `POST /auth/register` | Abierto | `auth.feature:15` |
| #002 | Password incompleta no valida | `POST /auth/register` | Abierto | `auth.feature:20` |

---

## Detalles de Errores

### #001 - Cliente con email duplicado en registro

**Endpoint:** `POST /auth/register`

**Resumen:** El sistema acepta registros con emails duplicados cuando debería rechazarlos

**Especificación Gherkin:** `auth.feature:15`

**Descripción del error:**
Al intentar registrar un cliente con un email que ya existe en la base de datos, la API devuelve un código 200 en lugar de un error 409 (Conflict).

**Dato de entrada:**
```json
{
  "email": "carlos_nuevo@email.com",
  "password": "nueva123"
}
```

**Resultado esperado:**
```json
{
  "status": 409,
  "message": "El email ya está registrado"
}
```

**Resultado actual:**
```json
{
  "status": 200,
  "message": "Cliente registrado exitosamente",
  "data": {
    "id": "uuid_duplicado",
    "email": "carlos_nuevo@email.com"
  }
}
```

---

### #002 - Password incompleta no valida

**Endpoint:** `POST /auth/register`

**Resumen:** Contraseñas cortas pasan la validación cuando deberían ser rechazadas

**Especificación Gherkin:** `auth.feature:20`

**Descripción del error:**
El validador de password acepta contraseñas menores a 8 caracteres, incumpliendo los requisitos de seguridad.

**Dato de entrada:**
```json
{
  "email": "test@email.com",
  "password": "123"
}
```

**Resultado esperado:**
```json
{
  "status": 400,
  "message": "La contraseña debe tener mínimo 8 caracteres"
}
```

**Resultado actual:**
```json
{
  "status": 200,
  "message": "Cliente registrado exitosamente",
  "data": {
    "id": "uuid",
    "email": "test@email.com"
  }
}
```

---

## 📝 Plantilla para nuevos errores

Copia esta plantilla cuando encuentres un nuevo error:

```markdown
### #XXX - [Resumen breve del error]

**Endpoint:** `METHOD /ruta/exacta`

**Resumen:** Una línea describiendo qué funciona mal

**Especificación Gherkin:** `archivo.feature:linea`

**Descripción del error:**
[Opcional] Explicación detallada de qué ocurre y por qué es incorrecto.

**Dato de entrada:**
[Opcional]
\`\`\`json
{
  "campo": "valor"
}
\`\`\`

**Resultado esperado:**
\`\`\`json
{
  "status": 200,
  "message": "Mensaje"
}
\`\`\`

**Resultado actual:**
\`\`\`json
{
  "status": 500,
  "message": "Error inesperado"
}
\`\`\`
```

---

## 🔄 Instrucciones de uso

1. **Cuando encuentres un error:**
   - Añade una nueva fila en la tabla de índice
   - Crea una sección con el detalle completo del error
   - Usa la plantilla para consistencia

2. **Para la IA:**
   - Cada error tiene estructura clara: endpoint, entrada, esperado vs actual
   - La referencia a Gherkin ayuda a entender los requisitos
   - Datos de entrada opcionales pero útiles para reproducir

3. **Mantén actualizado:**
   - Cuando resuelvas un error, cambia "Abierto" a "Resuelto"
   - No elimines errores, solo marca como resuelto

---

**Última actualización:** 2026-09-05
# Usuarios de prueba

Credenciales para testing de la API.

## ADMIN

| Campo    | Valor                  |
|----------|------------------------|
| Nombre   | Test Admin             |
| Email    | test_admin@email.com   |
| Password | admin123               |
| Rol      | ADMIN                  |

## CLIENTE

| Campo    | Valor                  |
|----------|------------------------|
| Nombre   | Test Cliente           |
| Email    | test_cliente@email.com |
| Password | cliente123             |
| Rol      | CLIENTE                |

---

## Login rápido

```bash
# ADMIN
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test_admin@email.com","password":"admin123"}'

# CLIENTE
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test_cliente@email.com","password":"cliente123"}'
```

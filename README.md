# Store API

API REST para la gestión de usuarios de una tienda. Permite operaciones CRUD y autenticación de usuarios.

## Stack

- Java 17
- Spring Boot 4.1.1
- Spring Data JPA + MySQL 8
- Spring Security (BCrypt)
- Bean Validation

## Estructura

```
src/main/java/com/example/store/
├── config/           # Configuración de seguridad
├── dto/              # Objetos de transferencia de datos
├── exception/        # Excepciones y manejo global de errores
├── Usuario.java      # Entidad
├── UsuarioController.java
├── UsuarioService.java
└── UsuarioRepository.java
```

## Endpoints

| Método | Ruta                      | Descripción                            |
|--------|---------------------------|----------------------------------------|
| GET    | `/api/usuarios`           | Listar todos los usuarios              |
| POST   | `/api/usuarios`           | Crear un usuario nuevo                 |
| GET    | `/api/usuarios/{id}`      | Obtener un usuario por ID              |
| PUT    | `/api/usuarios/{id}`      | Actualizar un usuario existente        |
| DELETE | `/api/usuarios/{id}`      | Eliminar un usuario existente          |
| POST   | `/api/usuarios/login`     | Iniciar sesión                         |

## Ejecución

Se requiere MySQL corriendo en `localhost:3306` con una base de datos llamada `store`.

Antes de iniciar, configura las credenciales y un secreto JWT de al menos 32 caracteres:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="tu-clave-local"
$env:JWT_SECRET="genera-un-secreto-aleatorio-de-al-menos-32-caracteres"
```

```bash
./mvnw spring-boot:run
```

La aplicación inicia en `http://localhost:8080`.

## Postman

En `specs/postman_collection.json` se encuentra una colección listo para importar con todos los endpoints y cuerpos de ejemplo.

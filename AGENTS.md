# AGENTS.md

## Project overview

Java 17 / Spring Boot 4.1.1 REST API for a store (users, categories, products, cart, orders). MySQL 8 backend. JWT authentication.

## Build & run

```bash
.\mvnw.cmd spring-boot:run        # run app (port 8080)
.\mvnw.cmd compile                # compile only
.\mvnw.cmd test                   # run tests (only contextLoads exists)
.\mvnw.cmd clean package          # build JAR
```

## Database

MySQL must be running at `localhost:3306/store` before starting the app.

**Critical:** `spring.sql.init.mode=never` and `spring.jpa.hibernate.ddl-auto=none`. Schema is NOT auto-created. You must run `schema.sql` and `data.sql` manually the first time.

Database credentials must be provided through the `DB_USERNAME` and `DB_PASSWORD` environment variables.

## Module structure

```
src/main/java/com/example/store/
├── config/SecurityConfig.java     # Security filter chain, BCrypt, public endpoints
├── auth/                          # JWT login: POST /api/auth/login
├── auth/jwt/                      # JwtTokenProvider, JwtAuthenticationFilter
├── usuario/                       # User CRUD + /api/usuarios
├── categoria/                     # Category CRUD + tree /api/categorias
├── producto/                      # Product CRUD + search/filter /api/productos
├── carrito/                       # Shopping cart /api/carrito (JWT required)
├── orden/                         # Orders /api/ordenes (JWT required)
└── exception/                     # GlobalExceptionHandler + custom exceptions
```

Each module follows: `Entity.java`, `Repository.java`, `Service.java`, `Controller.java`, `dto/` subfolder.

## Auth flow

1. `POST /api/auth/login` with `{email, password}` → returns JWT
2. Send `Authorization: Bearer <token>` header on protected endpoints
3. `JwtAuthenticationFilter` extracts userId into `SecurityContextHolder` → use `Authentication.getPrincipal()` to get userId

JWT config: secret from the `JWT_SECRET` environment variable, 24h expiration.

## Public vs protected endpoints

Public (no token): `/api/auth/**`, `GET/POST /api/usuarios`, `/api/productos/**`, `/api/categorias/**`
Protected (token required): `/api/carrito/**`, `/api/ordenes/**`, `GET/PUT/DELETE /api/usuarios/{id}`

## Known gaps (don't repeat these mistakes)

- **`precio`/`total` use `Double`** — not `BigDecimal`, can cause floating-point precision issues
- **`usuario/dto/LoginRequest.java` is dead code** — login uses `auth/dto/AuthRequest` instead
- **`spring-boot-starter-web` pinned to `4.2.0-M1`** — milestone version, may cause compatibility issues

## Style conventions

- All validation messages in Spanish
- No Lombok — manual getters/setters and constructors
- DTOs separate from entities — entities never returned directly in responses
- `@JsonIgnore` on password fields
- Constructor injection (no `@Autowired`)
- Error responses: `{timestamp, status, error, message}`
- DB table names in Spanish plural: `usuarios`, `productos`, `categorias`, `ordenes`

## Specs

Gherkin feature specs in `specs/features/` — useful context for business rules and edge cases.
User stories in `specs/historias/`.
Error tracking in `specs/issues/`.
Postman collection in `specs/collection.json`.

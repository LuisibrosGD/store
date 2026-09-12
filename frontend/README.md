# Ejecutar el frontend localmente

Este frontend necesita el backend Spring Boot activo en `http://localhost:8080`.
El servidor incluido usa únicamente módulos nativos de Node.js: no instala paquetes,
no compila archivos y no modifica el backend.

## 1. Iniciar el backend

Desde la raíz del proyecto:

```powershell
.\mvnw.cmd spring-boot:run
```

MySQL debe estar activo y la base `store` debe contener `schema.sql` y `data.sql`.

## 2. Iniciar el frontend

En otra terminal, también desde la raíz:

```powershell
node .\frontend\dev-server.js
```

Abrir `http://localhost:4173`. Las rutas de la SPA, los estilos y los módulos
JavaScript se sirven desde `/frontend`; las solicitudes `/api/**` se reenvían al
backend en el puerto 8080.

Para detener cualquiera de los procesos, presionar `Ctrl+C` en su terminal.

## Credenciales de muestra

Si se ejecutó `data.sql`, el administrador incluido es:

- Email: `carlos@email.com`
- Contraseña: `1234`

También se puede registrar un cliente desde la pantalla `/registro`.

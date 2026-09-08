# ⚙️ Backend - API REST de Control de Asistencia (Profamilia)

API REST desarrollada con **Java 17** y **Spring Boot 3+** aplicando arquitectura en capas, contratos DTO, base de datos relacional H2, validaciones Jakarta y pruebas unitarias con JUnit 5 y Mockito.

---

## 🏛️ Arquitectura en Capas

```text
Controller ──> Service ──> Repository ──> H2 Database
```

- **Controller (`/controller`):** Exposición de endpoints REST, habilitación de CORS para `http://localhost:4200` y validación de entrada (`@Valid`).
- **Service (`/service`):** Lógica de negocio (nacer ausente, alternancia de estado) y mapeo entre Entidades JPA y DTOs.
- **Repository (`/repository`):** Persistencia relacional mediante Spring Data JPA.
- **Exception (`/exception`):** Capturador global de errores (`@RestControllerAdvice`) que responde errores en formato JSON con códigos HTTP adecuados (400, 404, 500).

---

## 🔌 Endpoints REST

Base: `http://localhost:8080/api/employees`

- `GET /api/employees` - Listar todos los colaboradores (`200 OK`).
- `POST /api/employees` - Registrar colaborador (`201 Created`). Nace inicialmente con `present = false`.
- `PUT /api/employees/{id}/status` - Modificar o alternar estado de asistencia (`200 OK` / `404 Not Found`).

---

## 🧪 Pruebas Unitarias

Para ejecutar las pruebas unitarias:
```bash
.\mvnw.cmd test
```

Cubre los 4 escenarios críticos de negocio en `EmpleadoServiceTest.java`:
1. Creación de empleado como ausente por defecto.
2. Listado con mapeo a DTO.
3. Alternancia y cambio de estado de asistencia.
4. Lanzamiento de excepción `ResourceNotFoundException` cuando el ID no existe.

---

## 🚀 Ejecución del Backend

```bash
.\mvnw.cmd spring-boot:run
```

- **Consola H2:** `http://localhost:8080/h2-console`
  - **JDBC URL:** `jdbc:h2:mem:testdb`
  - **User:** `sa`
  - **Password:** `password`

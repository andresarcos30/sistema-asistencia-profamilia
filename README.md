# 🏥 Sistema de Control de Asistencia - Profamilia

> **Prueba Técnica:** Desarrollador Junior  
> **Solución Full Stack:** Spring Boot 3+ (Java 17) + Angular (Arquitectura Modular / Micro-Frontend) + Base de Datos SQL (H2 / JPA)

Aplicación web Full Stack empresarial diseñada para registrar colaboradores del área médica y administrativa, gestionando en tiempo real su estado de asistencia (Presente / Ausente).

---

## 🏗️ 1. Arquitectura del Sistema

El proyecto está diseñado bajo principios de **Clean Code**, **Arquitectura en Capas** en el backend y **Arquitectura Modular orientada a Micro Frontends** en el frontend:

```text
               ┌────────────────────────────────────────────────────────┐
               │              FRONTEND (Angular CLI)                    │
               │                                                        │
               │   ┌───────────────┐         ┌──────────────────────┐   │
               │   │  HOST SHELL   │         │    MFE ASISTENCIA    │   │
               │   │ • Auth (Login)│ ──────> │ • Formulario         │   │
               │   │ • Navbar      │ (Lazy)  │ • Métricas (KPIs)    │   │
               │   │ • AuthGuard   │         │ • Tabla Asistencia   │   │
               │   └───────────────┘         └──────────────────────┘   │
               └───────────────────────────┬────────────────────────────┘
                                           │ HTTP (REST + JSON) / CORS
                                           ▼
               ┌────────────────────────────────────────────────────────┐
               │              BACKEND (Spring Boot 3 / Java 17)         │
               │                                                        │
               │   Controller (REST API) ──> @CrossOrigin localhost:4200│
               │       │                                                │
               │   Service Layer (Business Logic + DTO Mapping)         │
               │       │                                                │
               │   Repository Layer (Spring Data JPA)                   │
               │       │                                                │
               │   H2 Relational Database (In-Memory SQL)               │
               └────────────────────────────────────────────────────────┘
```

---

## 🛠️ 2. Tecnologías Utilizadas

### Backend
- **Java 17 LTS:** Lenguaje principal con características modernas.
- **Spring Boot 3.4+:** Framework empresarial base.
- **Spring Data JPA & Hibernate:** Capa de persistencia y mapeo objeto-relacional (ORM).
- **H2 Database:** Motor de base de datos relacional SQL en memoria con consola web interactiva.
- **Jakarta Bean Validation:** Validación declarativa de datos de entrada (`@NotBlank`, `@Valid`).
- **Lombok:** Reducción de código repetitivo (Getters, Setters, Constructores).
- **JUnit 5 & Mockito:** Suite de pruebas unitarias automatizadas.

### Frontend
- **Angular 18/19+:** Framework SPA con Standalone Components (sin NgModules obsoletos).
- **Arquitectura Micro-Frontend / Host Shell:** Aislamiento del módulo de asistencia con carga perezosa (**Lazy Loading**).
- **Angular Signals:** Gestión de estado reactivo y control de sesión en el cliente.
- **Functional Guards (`CanActivateFn`):** Protección de rutas autenticadas.
- **CSS Moderno Responsivo:** Diseño profesional con métricas en tiempo real y Badges de estado.

---

## 📊 3. Modelo de Datos Relacional

### Tabla: `empleados`

| Columna | Tipo SQL | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Identificador único autogenerado |
| `full_name` | `VARCHAR(255)` | `NOT NULL` | Nombre completo del colaborador |
| `position` | `VARCHAR(255)` | `NOT NULL` | Cargo o especialidad (ej. Enfermera, Médico) |
| `present` | `BOOLEAN` | `NOT NULL` | Estado de asistencia (`true` = Presente, `false` = Ausente) |

> 📌 **Regla de Negocio:** Todo nuevo empleado se registra inicialmente como **Ausente (`present = false`)**.

---

## 🔌 4. Contratos de la API REST

Base URL: `http://localhost:8080/api/empleados` (o `http://localhost:8080/api/employees`)  
`Content-Type: application/json`

| Método | Endpoint Oficial | Endpoint Alternativo | Descripción | Código Éxito | Códigos Error |
| :--- | :--- | :--- | :--- | :---: | :---: |
| `GET` | `/api/empleados` | `/api/employees` | Listar todos los colaboradores | `200 OK` | `500` |
| `POST` | `/api/empleados` | `/api/employees` | Registrar nuevo colaborador | `201 Created` | `400 Bad Request` |
| `PUT` | `/api/empleados/{id}/asistencia` | `/api/employees/{id}/status` | Alternar o actualizar asistencia | `200 OK` | `404 Not Found` |

---

### Ejemplos de Petición y Respuesta

#### 1. Obtener Empleados (`GET /api/employees`)
**Respuesta `200 OK`:**
```json
[
  {
    "id": 1,
    "fullName": "Ana Pérez",
    "position": "Enfermera Jefe",
    "present": true
  },
  {
    "id": 2,
    "fullName": "Carlos Gómez",
    "position": "Médico General",
    "present": false
  }
]
```

#### 2. Registrar Empleado (`POST /api/employees`)
**Request Body:**
```json
{
  "fullName": "Laura Torres",
  "position": "Especialista en Sistemas"
}
```
**Respuesta `201 Created`:**
```json
{
  "id": 3,
  "fullName": "Laura Torres",
  "position": "Especialista en Sistemas",
  "present": false
}
```

#### 3. Cambiar Asistencia (`PUT /api/employees/{id}/status`)
**Request Body (Opcional):**
```json
true
```
*(Si se envía un booleano, se asigna ese valor; si se envía vacío, el servicio alterna automáticamente el estado).*

**Respuesta `200 OK`:**
```json
{
  "id": 3,
  "fullName": "Laura Torres",
  "position": "Especialista en Sistemas",
  "present": true
}
```

#### 4. Manejo de Errores Estructurado (`GlobalExceptionHandler`)
Si se busca un empleado inexistente (`PUT /api/employees/999/status`):
**Respuesta `404 Not Found`:**
```json
{
  "timestamp": "2026-09-07T16:15:00.000",
  "status": 404,
  "error": "Not Found",
  "message": "Empleado no encontrado con id: 999"
}
```

---

## 🧪 5. Pruebas Unitarias (Testing)

Se implementaron pruebas unitarias sobre la capa de servicio (`EmpleadoServiceTest.java`) utilizando **JUnit 5** y **Mockito**, garantizando cobertura sobre las reglas críticas de negocio:

```powershell
cd back
.\mvnw.cmd test
```

### Casos de Prueba Verificados:
- `debeListarEmpleados`: Verifica el correcto mapeo entre entidades y DTOs de salida.
- `debeCrearEmpleadoComoAusente`: Valida que la regla de negocio de nacer ausente (`present = false`) se cumpla estrictamente.
- `debeCambiarEstadoAsistencia`: Comprueba la alternancia y actualización del estado de asistencia.
- `debeLanzarExcepcionSiEmpleadoNoExiste`: Valida que se lance `ResourceNotFoundException` y no se realicen escrituras en base de datos ante un ID inexistente.

---

## 🚀 6. Guía de Instalación y Ejecución

### Prerrequisitos
- **Java JDK 17+** (verificar con `java -version`)
- **Node.js 18+** y **npm** (verificar con `node -v` y `npm -v`)
- **Angular CLI** (verificar con `ng version`)

---

### Opción A: Ejecución Rápida de Ambos Proyectos (Monorepo)
Desde la raíz del repositorio:
```powershell
npm run start:all
```

---

### Opción B: Ejecución por Separado

#### 1. Iniciar el Backend (Spring Boot)
```powershell
cd back
.\mvnw.cmd spring-boot:run
```
- **API disponible en:** `http://localhost:8080/api/employees`
- **Consola Visual SQL H2:** `http://localhost:8080/h2-console`
  - **JDBC URL:** `jdbc:h2:mem:testdb`
  - **Usuario:** `sa`
  - **Contraseña:** `password`

#### 2. Iniciar el Frontend (Angular)
```powershell
cd frontend
npm start
```
- **Aplicación web:** `http://localhost:4200`
- **Usuario de acceso demo:** `andres`
- **Contraseña:** `123`

---

## 💡 7. Decisiones de Diseño y Arquitectura

1. **Patrón DTO (Data Transfer Object):**  
   Se separaron estrictamente `EmpleadoRequestDTO` y `EmpleadoResponseDTO` de la entidad JPA `Empleado`. Esto previene sobreescrituras accidentales de atributos protegidos (Mass Assignment), evita ciclos de serialización JSON y permite evolucionar la base de datos sin romper los contratos con clientes externos.

2. **Elección de Base de Datos H2:**  
   Proporciona agilidad máxima para la prueba técnica sin dependencias de servicios externos pesados. Gracias a la capa de abstracción de Spring Data JPA, migrar a PostgreSQL o MySQL en producción solo requiere ajustar las propiedades de conexión en `application.properties`.

3. **Arquitectura Modular / Micro-Frontend:**  
   El frontend está dividido en un **Host Shell** (Login, Navbar y `AuthGuard`) y un módulo independiente de **Asistencia** (`mfe-empleados`), cargado bajo demanda con `loadComponent()`. Esto optimiza los tiempos de carga inicial y deja la arquitectura lista para evolucionar hacia Module Federation.

4. **Base de Datos Limpia por Defecto:**  
   La base de datos arranca limpia desde cero (`0` registros). Al consultar `GET /api/employees` inicialmente se responde `200 OK` con un arreglo vacío `[]`, cumpliendo de forma exacta el requerimiento del evaluador.

---

## 👨‍💻 Autor
Desarrollado para el proceso de selección técnica de **Profamilia**.

# 📘 Guía Maestra de Estudio - Prueba Técnica Profamilia
**Candidato:** Desarrollador Junior Full Stack  
**Tecnologías:** Java 17+ | Spring Boot 3+ | Angular CLI | H2 Database (SQL) | JUnit 5 & Mockito  
**Formato de la Prueba:** 3 Horas de Desarrollo + Espacio de Sustentación en Vivo con el Líder Técnico

---

## 📑 Índice de Navegación
1. [Enunciado del Problema y Reglas de Negocio](#1-enunciado-del-problema-y-reglas-de-negocio)
2. [Arquitectura del Backend (Spring Boot 3 + Java 17)](#2-arquitectura-del-backend-spring-boot-3--java-17)
3. [Base de Datos SQL (H2 & Spring Data JPA)](#3-base-de-datos-sql-h2--spring-data-jpa)
4. [Pruebas Unitarias Automatizadas (JUnit 5 & Mockito)](#4-pruebas-unitarias-automatizadas-junit-5--mockito)
5. [Arquitectura del Frontend (Angular Modular & Micro-Frontend)](#5-arquitectura-del-frontend-angular-modular--micro-frontend)
6. [Últimos Ajustes y Optimizaciones Aplicadas](#6-últimos-ajustes-y-optimizaciones-aplicadas)
7. [Simulador de Preguntas y Respuestas para la Sustentación](#7-simulador-de-preguntas-y-respuestas-para-la-sustentación)
8. [Comandos Rápidos de Ejecución](#8-comandos-rápidos-de-ejecución)

---

## 1. Enunciado del Problema y Reglas de Negocio

El objetivo es desarrollar un **Sistema de Control de Asistencia** para colaboradores de Profamilia (médicos, enfermeras y personal de apoyo).

### 📋 Reglas Obligatorias:
1. **Entidad `Empleado`:**
   - `id`: Identificador autoincremental (Clave primaria).
   - `fullName`: Nombre completo (Obligatorio, no vacío).
   - `position`: Cargo o puesto (Obligatorio, no vacío).
   - `present`: Booleano del estado de asistencia (`true` = Presente, `false` = Ausente).
2. **Regla de Creación (Nacer Ausente):**
   - Todo colaborador registrado nace inicialmente como **Ausente (`present = false`)**. El cliente no puede forzar que nazca presente.
3. **Regla de Consulta Inicial Limpia:**
   - Si no hay colaboradores registrados, `GET /api/employees` debe retornar código `200 OK` con un arreglo vacío `[]`.
4. **Regla de Alternancia de Asistencia:**
   - La API debe permitir alternar o modificar el estado de asistencia entre Presente y Ausente.
5. **Manejo de Errores:**
   - Si se intenta modificar un colaborador inexistente, debe responder `404 Not Found`.
   - Si faltan datos al registrar, debe responder `400 Bad Request`.

---

## 2. Arquitectura del Backend (Spring Boot 3 + Java 17)

Se implementó una **Arquitectura en Capas (Layered Architecture)** basada en principios de bajo acoplamiento y alta cohesión:

```text
  [ Petición HTTP ] 
         │
         ▼
[ EmpleadoController ]    --> Recibe petición, valida con @Valid, expone CORS y responde DTOs.
         │
         ▼
 [ EmpleadoService ]      --> Aplica reglas de negocio (@Transactional) y mapea Entity <-> DTO.
         │
         ▼
[ EmpleadoRepository ]    --> Interfaz Spring Data JPA con métodos CRUD automáticos.
         │
         ▼
 [ Base de Datos H2 ]     --> Motor relacional SQL en memoria.
```

### 📂 Estructura de Clases en `back/src/main/java/com/example/backend/`:

| Clase | Capa | Responsabilidad |
| :--- | :--- | :--- |
| `model/Empleado.java` | **Dominio (JPA)** | Entidad mapeada a la tabla `empleados`. Protege el esquema relacional con `@Entity`, `@Table`, `@Id`, `@GeneratedValue` y `@Column(nullable = false)`. |
| `dto/EmpleadoRequestDTO.java` | **Contrato Entrada** | Modela los datos que ingresan por `POST`. Usa `@NotBlank` de Jakarta Validation para impedir campos nulos o vacíos. |
| `dto/EmpleadoResponseDTO.java` | **Contrato Salida** | Modela la respuesta JSON devuelta al cliente (`id`, `fullName`, `position`, `present`). Evita exponer la entidad interna. |
| `repository/EmpleadoRepository.java` | **Persistencia** | Extiende `JpaRepository<Empleado, Long>`. Genera automáticamente las consultas SQL sin escribir sentencias manuales. |
| `service/EmpleadoService.java` | **Lógica de Negocio** | Aplica `@Transactional`. Implementa `listar()`, `crear()` (forzando `present = false`) y `cambiarEstado()`. |
| `controller/EmpleadoController.java` | **Controlador REST** | Expone `/api/empleados` (y `/api/employees`), `@CrossOrigin(origins = "http://localhost:4200")`, respondiendo códigos HTTP estándar: `200 OK`, `201 Created`. |
| `exception/GlobalExceptionHandler.java` | **Manejo de Errores** | `@RestControllerAdvice` que captura `ResourceNotFoundException` (retorna `404`) y `MethodArgumentNotValidException` (retorna `400` con detalle de campos). |

---

## 3. Base de Datos SQL (H2 & Spring Data JPA)

### ¿Por qué H2 Database?
- Es un **motor SQL relacional real** que corre en memoria dentro del proceso de Java.
- **Cero dependencias externas:** No requiere tener instalado ni configurar servicios pesados de MySQL ni PostgreSQL durante la prueba.
- **Agilidad:** El enunciado de Profamilia la recomienda explícitamente para agilizar los tiempos de desarrollo.
- **Configuración (`application.properties`):**
  ```properties
  spring.datasource.url=jdbc:h2:mem:testdb
  spring.datasource.driverClassName=org.h2.Driver
  spring.datasource.username=sa
  spring.datasource.password=password
  spring.jpa.hibernate.ddl-auto=update
  spring.h2.console.enabled=true
  spring.h2.console.path=/h2-console
  ```
- **Consola Web SQL:** Accesible en `http://localhost:8080/h2-console` para ejecutar `SELECT * FROM EMPLEADOS;`.
- **Estado Limpio:** La base de datos arranca limpia con 0 registros para validar que el sistema registra y lista datos reales en vivo.

---

## 4. Pruebas Unitarias Automatizadas (JUnit 5 & Mockito)

Ubicación: `back/src/test/java/com/example/backend/EmpleadoServiceTest.java`

Se diseñó una suite de pruebas unitarias sobre la capa de negocio sin necesidad de levantar Spring Boot completo ni tocar la base de datos, ejecutándose en **milisegundos**.

```powershell
cd back
.\mvnw.cmd test
```

### 🧪 Casos de Prueba Verificados (`BUILD SUCCESS`):
1. **`debeListarEmpleados()`:**
   - Comprueba que el servicio consulte el repositorio y transforme correctamente cada entidad a `EmpleadoResponseDTO`.
2. **`debeCrearEmpleadoComoAusente()` (Prueba Crítica):**
   - Valida formalmente la regla de negocio: al invocar `crear()`, el valor del atributo `present` devuelto y guardado es estrictamente **`false`**.
3. **`debeCambiarEstadoAsistencia()`:**
   - Comprueba que un empleado existente pueda alternar su estado a `true` (Presente) y se guarde la actualización.
4. **`debeLanzarExcepcionSiEmpleadoNoExiste()`:**
   - Simula la búsqueda de un ID inexistente (`999L`), valida que se lance `ResourceNotFoundException` y verifica con `never().save()` que **nunca** se intentó escribir en la base de datos.

---

## 5. Arquitectura del Frontend (Angular Modular & Micro-Frontend)

Estructura desacoplada en `frontend/src/app/` simulando la arquitectura empresarial de **Micro Frontends**:

```text
src/app/
├── core/                                # Capa Transversal del Sistema
│   ├── services/auth.service.ts         # Manejo reactivo de sesión con Angular Signals y localStorage
│   └── guards/auth.guard.ts             # CanActivateFn funcional que protege las rutas privadas
│
├── host/                                # HOST SHELL (Aplicación Contenedora)
│   ├── auth/login/                      # Formulario de Login (sin datos quemados, validación reactiva)
│   └── layout/navbar/                   # Barra de navegación superior con usuario activo y logout
│
├── mfe-empleados/                       # MICRO-FRONTEND DESACOPLADO (Dominio de Asistencia)
│   ├── models/empleado.model.ts         # Interfaces de dominio (Empleado, EmpleadoRequest)
│   ├── services/empleado.service.ts     # Cliente HTTP hacia http://localhost:8080/api/employees
│   └── components/empleados/            # Vista principal con KPIs, formulario y tabla de asistencia
│
├── app.routes.ts                        # Enrutador principal con Lazy Loading (loadComponent)
└── app.ts / app.html                    # Contenedor raíz (<app-navbar> + <router-outlet>)
```

### 🌟 Fortalezas Técnicas del Frontend:
1. **Lazy Loading Real:** En `app.routes.ts`, el módulo de asistencia solo se descarga cuando el usuario se autentica. Al ejecutar `ng build`, Angular empaqueta este módulo en un archivo JavaScript separado (`chunk-xxx.js`).
2. **Protección con `AuthGuard`:** Si un usuario intenta ingresar directamente por URL a `http://localhost:4200/asistencia` sin haberse autenticado, el guard cancela la petición y lo redirige forzosamente a `/login`.
3. **Tarjetas de Métricas en Vivo (KPIs):** Contadores reactivos automáticos para:
   - **Total de Empleados**
   - **Presentes Hoy** (🟢 Verde)
   - **Ausentes Hoy** (🔴 Rojo)
4. **Feedback Visual Completo:** Alertas dinámicas de éxito (verde), alertas de error (rojo), spinners de carga y estados vacíos (*"📂 No hay empleados registrados actualmente"*).

---

## 6. Últimos Ajustes y Optimizaciones Aplicadas

Durante las pruebas de integración se aplicaron tres mejoras de ingeniería indispensables:

1. **Eliminación de `withFetch()` en `app.config.ts`:**
   - Se configuró `provideHttpClient()` en su modo nativo para evitar retrasos en el ciclo de detección de cambios de Angular Zone.js.
2. **Uso de `ChangeDetectorRef` y Actualizaciones Inmutables:**
   - En `empleados.component.ts`, al recibir la confirmación de creación del backend (`201 Created`), se agrega el colaborador directamente al arreglo local (`[...this.empleados, creado]`) y se ejecuta `this.cdr.detectChanges()`.
   - **Resultado:** La tabla y los contadores se actualizan en **milisegundos**, y el botón de registro vuelve inmediatamente a su estado normal sin quedarse pegado en *"Guardando..."*.
3. **Limpieza Total de Datos Quemados:**
   - **Login:** Se eliminaron las credenciales predefinidas (`username = ''`, `password = ''`) y los textos de simulacro. Los inputs ahora inician limpios con placeholders profesionales.
   - **Backend:** Se retiró el `DataLoader.java` para que la base de datos inicie limpia desde cero (`0` registros), permitiendo hacer la demostración de registro en vivo ante el evaluador.

---

## 7. Simulador de Preguntas y Respuestas para la Sustentación

Estas son las preguntas fijas que formula un Líder Técnico para evaluar el criterio de un Desarrollador Junior:

---

### 🔹 Sección A: Backend & Buenas Prácticas Java

#### P1: "¿Por qué utilizaste DTOs en lugar de devolver la entidad `@Entity` en el Controller?"
> **Tu Respuesta:**  
> *"Utilicé DTOs (`EmpleadoRequestDTO` y `EmpleadoResponseDTO`) para desacoplar el contrato de la API pública del modelo interno de persistencia. Esto evita vulnerabilidades de seguridad como **Mass Assignment** (donde un usuario malintencionado podría enviar un `id` o forzar el campo `present`), previene ciclos de serialización JSON y permite cambiar las tablas de la base de datos sin romper a los clientes que consumen el servicio."*

#### P2: "¿Dónde y cómo garantizaste que un empleado nuevo nazca obligatoriamente como Ausente?"
> **Tu Respuesta:**  
> *"Se garantiza en la capa de negocio dentro de `EmpleadoService.crear()`, asignando explícitamente `empleado.setPresent(false)` antes de persistir. Además, la regla está blindada con una prueba unitaria automatizada en `EmpleadoServiceTest` que verifica formalmente que el valor devuelto y guardado sea siempre `false`."*

#### P3: "¿Por qué inyectaste dependencias por constructor (`@RequiredArgsConstructor`) y no con `@Autowired` en el atributo?"
> **Tu Respuesta:**  
> *"Porque la inyección por constructor es la práctica oficial recomendada por el equipo de Spring. Garantiza la inmutabilidad de los beans (`private final`), previene dependencias circulares y permite escribir pruebas unitarias con Mockito instanciando la clase directamente con `new EmpleadoService(mockRepo)` sin necesidad de levantar el contenedor de Spring."*

#### P4: "¿Para qué sirve la anotación `@Transactional` en el Servicio?"
> **Tu Respuesta:**  
> *"Garantiza que los métodos se ejecuten dentro de una transacción bajo las propiedades ACID. Si ocurre una excepción no controlada, Spring ejecuta un **Rollback** automático, evitando que la base de datos quede en un estado inconsistente. En métodos de lectura uso `@Transactional(readOnly = true)` para optimizar rendimiento y evitar que Hibernate realice dirty checking innecesario."*

#### P5: "¿Por qué elegiste H2 en memoria y qué tan complejo sería migrar a PostgreSQL o MySQL?"
> **Tu Respuesta:**  
> *"Elegí H2 porque agiliza la ejecución de la prueba técnica al no requerir software externo instalado ni credenciales locales. Gracias a la capa de abstracción de **Spring Data JPA e Hibernate**, migrar a PostgreSQL o MySQL en producción solo requiere agregar el driver (`postgresql` o `mysql-connector-j`) en el `pom.xml` y actualizar la URL de conexión en `application.properties`; no se modifica ni una sola línea de código Java."*

#### P6: "¿Cómo centralizaste el manejo de errores en la API?"
> **Tu Respuesta:**  
> *"Implementé `@RestControllerAdvice` en `GlobalExceptionHandler`. Intercepta `ResourceNotFoundException` devolviendo `404 Not Found`, captura las fallas de Jakarta Validation (`@NotBlank`) retornando `400 Bad Request` con un mapa estructurado de los campos erróneos, y atrapa cualquier error general con `500 Internal Server Error`, garantizando respuestas homogéneas en JSON."*

---

### 🔹 Sección B: Frontend & Arquitectura Angular

#### P7: "¿Por qué diseñaste el Frontend bajo un patrón Host Shell y Micro-Frontend?"
> **Tu Respuesta:**  
> *"Para garantizar modularidad y escalabilidad empresarial. Dividí la aplicación en un **Host Shell** (encargado de la sesión con `AuthService`, la protección con `AuthGuard` y la barra de navegación) y un módulo autónomo de **Asistencia** (`mfe-empleados`). Gracias al **Lazy Loading** (`loadComponent`), la funcionalidad de asistencia se descarga bajo demanda, lista para extraerse a un micro-frontend independiente con Module Federation si la organización lo requiere."*

#### P8: "¿Qué ventaja tienen los `Standalone Components` frente a los tradicionales `NgModules`?"
> **Tu Respuesta:**  
> *"Son el estándar moderno de Angular desde la versión 14/17+. Eliminan la necesidad de declarar módulos intermedios (`app.module.ts`), simplifican el árbol de dependencias, reducen el peso del bundle JavaScript final y hacen que la carga perezosa de rutas sea directa y declarativa."*

#### P9: "¿Por qué utilizaste un Functional Guard (`CanActivateFn`) en vez de una clase?"
> **Tu Respuesta:**  
> *"Porque las clases guards basadas en interfaces (`implements CanActivate`) están deprecadas en Angular moderno. Los Functional Guards son funciones puras, eliminan código repetitivo y permiten inyectar dependencias directamente mediante la función `inject()`."*

#### P10: "¿Cómo evitaste bloqueos de CORS entre el puerto 4200 y el 8080?"
> **Tu Respuesta:**  
> *"Configuré `@CrossOrigin(origins = "http://localhost:4200")` en el controlador de Spring Boot. Esto le responde al navegador en las peticiones preflight (`OPTIONS`) que el origen `http://localhost:4200` tiene autorización para consumir los métodos `GET`, `POST` y `PUT` con encabezados JSON."*

---

### 🔹 Sección C: Visión Técnica y Cierre de Entrevista

#### P11: "¿Si hubieras tenido 2 horas más para la prueba, qué mejoras le habrías agregado al proyecto?"
> **Tu Respuesta (Muestra madurez y visión Senior):**  
> *"Habría priorizado 4 mejoras clave:*  
> *1. **Paginación y Filtros:** Implementar `Pageable` de Spring Data (`Page<EmpleadoResponseDTO>`) y buscador en tiempo real en Angular.*  
> *2. **Seguridad Robusta:** Integrar Spring Security con JWT (JSON Web Tokens) para validar credenciales y roles contra la base de datos.*  
> *3. **Historial de Auditoría:** Una tabla relacional `asistencias_historial` que guarde la fecha y hora exacta (`LocalDateTime`) de cada entrada y salida.*  
> *4. **Contenerización:** Un archivo `docker-compose.yml` para levantar Backend, Frontend y PostgreSQL con un solo comando `docker compose up`."*

---

## 8. Comandos Rápidos de Ejecución

### Terminal 1: Iniciar Backend (Spring Boot en Puerto 8080)
```powershell
cd back
.\mvnw.cmd spring-boot:run
```
- **API Endpoints:** `http://localhost:8080/api/employees`
- **Consola SQL H2:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`, Password: `password`)

### Terminal 2: Ejecutar Pruebas Unitarias Backend
```powershell
cd back
.\mvnw.cmd test
```

### Terminal 3: Iniciar Frontend (Angular en Puerto 4200)
```powershell
cd frontend
npm start
```
- **Aplicación Web:** `http://localhost:4200` (Ingresa tu usuario y contraseña en el login para acceder).

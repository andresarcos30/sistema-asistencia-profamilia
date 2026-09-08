# 💻 Frontend - Sistema de Control de Asistencia (Profamilia)

Módulo Frontend desarrollado en **Angular** bajo una **Arquitectura Modular orientada a Micro Frontends**, utilizando Standalone Components, Angular Signals y carga diferida (Lazy Loading).

---

## 🏛️ Estructura del Proyecto

```text
src/app/
├── core/                                # Servicios globales y seguridad
│   ├── guards/auth.guard.ts             # CanActivateFn para protección de rutas
│   └── services/auth.service.ts         # Manejo de sesión reactiva con Signals
│
├── host/                                # HOST SHELL
│   ├── auth/login/                      # Vista y lógica de Login
│   └── layout/navbar/                   # Barra de navegación con usuario y logout
│
├── mfe-empleados/                       # MICRO-FRONTEND DESACOPLADO (Asistencia)
│   ├── models/empleado.model.ts         # Interfaces de datos (Empleado, EmpleadoRequest)
│   ├── services/empleado.service.ts     # Cliente HTTP hacia la API REST
│   └── components/empleados/            # Formulario, métricas (KPIs) y tabla interactiva
│
├── app.routes.ts                        # Enrutador principal con Lazy Loading (loadComponent)
└── app.ts / app.html                    # Shell principal del Host
```

---

## 🚀 Ejecución Local

```bash
npm install
npm start
```
La aplicación quedará disponible en **`http://localhost:4200`**.

### Credenciales de acceso de prueba:
- **Usuario:** `andres`
- **Contraseña:** `123`

---

## 🛠️ Comandos Útiles

- **Compilar para producción:** `npm run build`
- **Verificar bundle:** Al compilar, observarás que `empleados-component` se empaqueta en un **lazy chunk independiente**, garantizando el aislamiento del micro-frontend.

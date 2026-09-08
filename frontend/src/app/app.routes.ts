// Importaciones de Angular Router
import { Routes } from '@angular/router';

// Guard funcional para proteger rutas autenticadas
import { authGuard } from './core/guards/auth.guard';

// Componente de Login del Host Shell
import { LoginComponent } from './host/auth/login/login.component';

/**
 * Rutas principales de la aplicación (Host Shell)
 * Configura la navegación y la carga perezosa (Lazy Loading) del Micro-Frontend de Asistencia.
 */
export const routes: Routes = [
  // Ruta raíz: redirige por defecto a la pantalla de inicio de sesión
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },

  // Ruta pública: formulario de Login del Host Shell
  {
    path: 'login',
    component: LoginComponent
  },

  // Ruta protegida: Micro-Frontend de Gestión de Asistencia
  {
    path: 'asistencia',
    canActivate: [authGuard], // Bloquea el acceso si no hay sesión activa

    // Lazy Loading (Carga bajo demanda):
    // El navegador no descarga el código de este módulo hasta que el usuario se autentica e ingresa a /asistencia.
    // En el build de producción, genera un chunk JS independiente, simulando la arquitectura de Micro Frontends.
    loadComponent: () =>
      import('./mfe-empleados/components/empleados/empleados.component').then(
        (m) => m.EmpleadosComponent
      )
  },

  // Ruta comodín (Wildcard): cualquier URL no reconocida redirige a /login
  {
    path: '**',
    redirectTo: 'login'
  }
];

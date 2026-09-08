// Inyección funcional moderna de Angular sin clases
import { inject } from '@angular/core';
// Tipos de Router y Guard funcional CanActivateFn
import { CanActivateFn, Router } from '@angular/router';
// Servicio de autenticación
import { AuthService } from '../services/auth.service';

/**
 * authGuard (Functional Guard)
 * Intercepta la navegación hacia rutas protegidas (como /asistencia).
 * Si el usuario no ha iniciado sesión, cancela la navegación y lo redirige a /login.
 */
export const authGuard: CanActivateFn = () => {
  // Inyección de servicios usando inject() dentro del contexto funcional
  const authService = inject(AuthService);
  const router = inject(Router);

  // Si está autenticado, permite el acceso a la ruta solicitada
  if (authService.isLoggedIn()) {
    return true;
  }

  // Si no está autenticado, redirige forzosamente a la pantalla de Login y bloquea la navegación
  router.navigate(['/login']);
  return false;
};

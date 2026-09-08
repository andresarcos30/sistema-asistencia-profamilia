// Importaciones de Angular Core para inyección de dependencias y reactividad con Signals
import { Injectable, signal } from '@angular/core';
// Router para redirecciones de navegación
import { Router } from '@angular/router';

/**
 * AuthService
 * Servicio global del Host Shell encargado de simular el estado de autenticación y sesión del usuario.
 * Utiliza Angular Signals para emitir cambios de estado reactivos a cualquier componente que lo consulte.
 */
@Injectable({
  providedIn: 'root' // Singleton disponible en toda la aplicación
})
export class AuthService {
  // Clave usada para persistir la sesión en el navegador (localStorage)
  private readonly STORAGE_KEY = 'profamilia_user';

  // Signal reactivo que almacena el nombre del usuario autenticado o null si no hay sesión
  currentUser = signal<string | null>(this.getStoredUser());

  // Inyección del Router de Angular
  constructor(private router: Router) {}

  /**
   * Valida credenciales e inicia sesión.
   * Guarda el usuario en localStorage y actualiza el Signal.
   */
  login(username: string, password: string): boolean {
    // Validación básica: que usuario y clave no vengan vacíos
    if (username.trim() && password.trim()) {
      localStorage.setItem(this.STORAGE_KEY, username.trim()); // Persiste en almacenamiento local
      this.currentUser.set(username.trim()); // Notifica el cambio al Signal reactivo
      return true; // Login exitoso
    }
    return false; // Login fallido
  }

  /**
   * Cierra la sesión activa.
   * Elimina la persistencia, limpia el Signal y redirige a la pantalla de Login.
   */
  logout(): void {
    localStorage.removeItem(this.STORAGE_KEY); // Limpia localStorage
    this.currentUser.set(null); // Actualiza el Signal a null
    this.router.navigate(['/login']); // Redirige a /login
  }

  /**
   * Consulta si existe una sesión activa actualmente.
   */
  isLoggedIn(): boolean {
    return this.currentUser() !== null;
  }

  /**
   * Método privado para recuperar el usuario guardado en caso de recargar la página en el navegador.
   */
  private getStoredUser(): string | null {
    return localStorage.getItem(this.STORAGE_KEY);
  }
}

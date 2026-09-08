// Importaciones de Angular Core
import { Component, inject } from '@angular/core';
// CommonModule para directivas comunes
import { CommonModule } from '@angular/common';
// Servicio de autenticación
import { AuthService } from '../../../core/services/auth.service';

/**
 * NavbarComponent
 * Componente visual de la barra de navegación superior del Host Shell.
 * Muestra la marca corporativa, el nombre del usuario conectado y el botón para cerrar sesión.
 */
@Component({
  selector: 'app-navbar', // Etiqueta HTML para instanciar el componente: <app-navbar>
  standalone: true, // Componente independiente (sin necesidad de NgModule)
  imports: [CommonModule], // Módulos requeridos en la plantilla HTML
  templateUrl: './navbar.component.html', // Ruta a la plantilla HTML
  styleUrl: './navbar.component.css' // Ruta a los estilos CSS
})
export class NavbarComponent {
  // Inyección del servicio de autenticación para consultar el usuario actual reactivamente
  authService = inject(AuthService);

  /**
   * Método ejecutado al hacer clic en "Cerrar Sesión"
   */
  onLogout(): void {
    this.authService.logout(); // Invoca el cierre de sesión y redirección
  }
}

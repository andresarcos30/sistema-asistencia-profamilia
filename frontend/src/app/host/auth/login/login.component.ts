// Importaciones de Angular
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

// Servicio de autenticación
import { AuthService } from '../../../core/services/auth.service';

/**
 * LoginComponent
 * Componente del Host Shell encargado de la vista y lógica de inicio de sesión.
 */
@Component({
  selector: 'app-login', // <app-login>
  standalone: true, // Componente Standalone
  imports: [CommonModule, FormsModule], // FormsModule permite el uso de [(ngModel)] en inputs
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  // Inyección de servicios requeridos
  private authService = inject(AuthService);
  private router = inject(Router);

  // Campos del formulario vacíos por defecto
  username = '';
  password = '';
  errorMessage = ''; // Variable para almacenar mensajes de alerta visual

  /**
   * Método ejecutado al enviar el formulario (submit)
   */
  onLogin(): void {
    // Valida que los campos no estén vacíos
    if (!this.username.trim() || !this.password.trim()) {
      this.errorMessage = 'Por favor ingrese usuario y contraseña';
      return;
    }

    // Intenta iniciar sesión con el AuthService
    const success = this.authService.login(this.username, this.password);
    if (success) {
      // Redirige al micro-frontend protegido de asistencia
      this.router.navigate(['/asistencia']);
    } else {
      this.errorMessage = 'Credenciales inválidas';
    }
  }
}

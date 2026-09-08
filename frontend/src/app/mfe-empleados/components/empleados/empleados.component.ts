// Importaciones de Angular Core
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Servicio y modelos del micro-frontend
import { EmpleadoService } from '../../services/empleado.service';
import { Empleado, EmpleadoRequest } from '../../models/empleado.model';

/**
 * EmpleadosComponent
 * Componente principal del Micro-Frontend de Asistencia.
 * Contiene el formulario de registro, las tarjetas de métricas en tiempo real y la tabla interactiva de asistencia.
 */
@Component({
  selector: 'app-empleados', // <app-empleados>
  standalone: true, // Componente independiente (facilitador de Micro-Frontend)
  imports: [CommonModule, FormsModule],
  templateUrl: './empleados.component.html',
  styleUrl: './empleados.component.css'
})
export class EmpleadosComponent implements OnInit {
  // Inyección del servicio de empleados
  private empleadoService = inject(EmpleadoService);
  // Inyección de ChangeDetectorRef para forzar actualización inmediata de la UI
  private cdr = inject(ChangeDetectorRef);

  // Arreglo reactivo con los colaboradores cargados desde la base de datos
  empleados: Empleado[] = [];
  
  // Variables para control de estado de la interfaz
  loading = false;          // Indica si hay una petición en curso (para deshabilitar botones y mostrar spinner)
  successMessage = '';      // Alerta visual de éxito (verde)
  errorMessage = '';        // Alerta visual de error (roja)

  // Modelo enlazado bidireccionalmente con el formulario HTML [(ngModel)]
  nuevoEmpleado: EmpleadoRequest = {
    fullName: '',
    position: ''
  };

  /**
   * Ciclo de vida OnInit: se ejecuta automáticamente al cargarse el componente
   */
  ngOnInit(): void {
    this.cargarEmpleados(); // Carga inicial de datos al abrir la pantalla
  }

  /**
   * Carga o refresca la lista de empleados desde la API REST de Spring Boot
   */
  cargarEmpleados(): void {
    this.loading = true; // Activa indicador de carga
    this.errorMessage = '';

    this.empleadoService.listar().subscribe({
      next: (data) => {
        // Asigna una nueva referencia inmutable del arreglo para refrescar la tabla
        this.empleados = [...data];
        this.loading = false;  // Desactiva el indicador
        this.cdr.detectChanges(); // Fuerza la renderización inmediata en la vista
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'No fue posible conectar con el backend (Spring Boot en http://localhost:8080).';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Registra un nuevo colaborador aplicando validaciones en el cliente
   */
  registrarEmpleado(): void {
    // Validación en el cliente: campos no vacíos
    if (!this.nuevoEmpleado.fullName.trim() || !this.nuevoEmpleado.position.trim()) {
      this.errorMessage = 'Por favor complete todos los campos requeridos.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Llama al servicio para realizar la petición POST
    this.empleadoService.crear(this.nuevoEmpleado).subscribe({
      next: (creado) => {
        this.successMessage = `Empleado ${creado.fullName} registrado con éxito (inicialmente Ausente).`;
        // Limpia los campos del formulario
        this.nuevoEmpleado = { fullName: '', position: '' };
        // Agrega el nuevo empleado inmediatamente a la lista local
        this.empleados = [...this.empleados, creado];
        this.loading = false;
        this.cdr.detectChanges(); // Notifica a Angular para actualizar la tabla y contadores al instante
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Error al registrar el empleado en el servidor.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Alterna el estado de asistencia de un colaborador (Presente <-> Ausente)
   */
  toggleAsistencia(empleado: Empleado): void {
    const nuevoEstado = !empleado.present; // Invierte el estado actual
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Llama al servicio para ejecutar la petición PUT
    this.empleadoService.cambiarEstado(empleado.id, nuevoEstado).subscribe({
      next: (actualizado) => {
        this.successMessage = `Estado de ${actualizado.fullName} actualizado a: ${actualizado.present ? 'Presente' : 'Ausente'}.`;
        // Actualiza el registro en la lista local de forma inmutable
        this.empleados = this.empleados.map(e => e.id === actualizado.id ? actualizado : e);
        this.loading = false;
        this.cdr.detectChanges(); // Refresca la tabla y los contadores en tiempo real
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Error al actualizar el estado de asistencia.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Getters calculados en tiempo real para las tarjetas de métricas (KPIs)
   */
  get totalEmpleados(): number {
    return this.empleados.length; // Total de colaboradores
  }

  get totalPresentes(): number {
    return this.empleados.filter(e => e.present).length; // Cantidad de colaboradores presentes
  }

  get totalAusentes(): number {
    return this.empleados.filter(e => !e.present).length; // Cantidad de colaboradores ausentes
  }
}

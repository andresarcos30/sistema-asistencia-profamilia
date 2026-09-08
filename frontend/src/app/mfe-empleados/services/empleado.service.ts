// Importaciones de Angular Core y Cliente HTTP
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Modelos del micro-frontend
import { Empleado, EmpleadoRequest } from '../models/empleado.model';

/**
 * EmpleadoService
 * Servicio del micro-frontend encargado de la comunicación HTTP REST con el Backend en Spring Boot.
 */
@Injectable({
  providedIn: 'root' // Singleton disponible para inyección
})
export class EmpleadoService {
  // Inyección de HttpClient para realizar peticiones HTTP asíncronas
  private http = inject(HttpClient);

  // URL base de la API REST expuesta por Spring Boot
  private readonly apiUrl = 'http://localhost:8080/api/employees';

  /**
   * Petición: GET /api/employees
   * Obtiene la lista completa de empleados registrados.
   * Retorna un Observable que emite el arreglo de empleados.
   */
  listar(): Observable<Empleado[]> {
    return this.http.get<Empleado[]>(this.apiUrl);
  }

  /**
   * Petición: POST /api/employees
   * Registra un nuevo colaborador en la base de datos.
   * @param request Objeto con fullName y position
   */
  crear(request: EmpleadoRequest): Observable<Empleado> {
    return this.http.post<Empleado>(this.apiUrl, request);
  }

  /**
   * Petición: PUT /api/employees/{id}/status
   * Modifica o alterna el estado de asistencia de un colaborador.
   * @param id Identificador único del empleado
   * @param nuevoEstado Booleano que define si está presente (true) o ausente (false)
   */
  cambiarEstado(id: number, nuevoEstado: boolean): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.apiUrl}/${id}/status`, nuevoEstado);
  }
}

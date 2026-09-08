/**
 * Empleado (Modelo de datos del Dominio)
 * Define el contrato de datos del colaborador recibido desde el Backend (EmpleadoResponseDTO).
 */
export interface Empleado {
  id: number;          // Identificador único
  fullName: string;    // Nombre completo
  position: string;    // Puesto o especialidad médica/administrativa
  present: boolean;    // Estado de asistencia (true = Presente, false = Ausente)
}

/**
 * EmpleadoRequest (Modelo de Creación)
 * Define los campos necesarios para enviar en la petición POST hacia el Backend (EmpleadoRequestDTO).
 */
export interface EmpleadoRequest {
  fullName: string;    // Nombre obligatorio
  position: string;    // Puesto obligatorio
}

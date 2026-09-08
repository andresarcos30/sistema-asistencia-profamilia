package com.example.backend.dto;

// Lombok para simplificar la creación de DTOs
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EmpleadoResponseDTO (Data Transfer Object)
 * Representa la respuesta JSON que el Backend devuelve al Frontend.
 * Desacopla la entidad JPA de base de datos de la API pública para no exponer detalles internos.
 */
@Data // Métodos getters y setters generados automáticamente
@NoArgsConstructor // Constructor sin parámetros
@AllArgsConstructor // Constructor con todos los atributos para instanciarlo rápidamente desde el Service
public class EmpleadoResponseDTO {

    // Identificador único asignado por la base de datos
    private Long id;

    // Nombre completo del colaborador
    private String fullName;

    // Cargo o puesto del colaborador
    private String position;

    // Estado actual de asistencia (true = Presente, false = Ausente)
    private Boolean present;
}

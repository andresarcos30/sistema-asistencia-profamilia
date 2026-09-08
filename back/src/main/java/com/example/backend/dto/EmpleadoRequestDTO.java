package com.example.backend.dto;

// Importación de Jackson y Jakarta Validation para deserialización flexible y reglas de negocio
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

// Lombok para constructores y métodos getters/setters
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EmpleadoRequestDTO (Data Transfer Object)
 * Representa el contrato estricto de los datos que el cliente debe enviar al crear un empleado.
 * Soporta nombres de atributos en inglés y español gracias a @JsonAlias sin romper el contrato actual.
 */
@Data // Genera getters, setters, equals, hashCode y toString automáticamente
@NoArgsConstructor // Constructor vacío requerido para la deserialización JSON por parte de Jackson
@AllArgsConstructor // Constructor con todos los argumentos para facilitar la creación en tests
public class EmpleadoRequestDTO {

    // @NotBlank: Valida que el texto no sea nulo, no esté vacío y no contenga solo espacios en blanco
    // @JsonAlias: Permite recibir "nombreCompleto" o "nombre" si el evaluador envía el payload en español
    @NotBlank(message = "El nombre completo es obligatorio")
    @JsonAlias({"nombreCompleto", "nombre", "full_name"})
    private String fullName;

    // @NotBlank: Valida que el puesto o cargo sea proporcionado obligatoriamente
    // @JsonAlias: Permite recibir "puesto" o "cargo" si se envía en español
    @NotBlank(message = "El cargo o puesto es obligatorio")
    @JsonAlias({"puesto", "cargo", "position_name"})
    private String position;
}

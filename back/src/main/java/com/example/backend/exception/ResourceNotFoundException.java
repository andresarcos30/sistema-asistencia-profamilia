package com.example.backend.exception;

// Importaciones de Spring Web para asociar códigos de estado HTTP
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ResourceNotFoundException
 * Excepción personalizada de negocio que se lanza cuando se solicita un empleado con un ID que no existe.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Asocia automáticamente el código HTTP 404 (Not Found) a esta excepción
public class ResourceNotFoundException extends RuntimeException {

    // Constructor que recibe el mensaje descriptivo del error (ej: "Empleado no encontrado con id: 99")
    public ResourceNotFoundException(String message) {
        super(message); // Pasa el mensaje a la clase padre RuntimeException
    }
}

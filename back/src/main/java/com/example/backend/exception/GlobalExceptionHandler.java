package com.example.backend.exception;

// Importaciones para manejo de fechas y estructuras de datos para las respuestas JSON
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Importaciones de Spring Framework para interceptar excepciones en controladores REST
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler
 * Interceptor centralizado de errores para toda la aplicación.
 * Transforma cualquier excepción no controlada en una respuesta JSON estructurada y con el código HTTP apropiado.
 */
@RestControllerAdvice // Aplica este manejador de excepciones globalmente a todos los @RestController
public class GlobalExceptionHandler {

    /**
     * Captura cuando no se encuentra un empleado y retorna HTTP 404 (Not Found)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now()); // Marca de tiempo en que ocurrió el error
        error.put("status", HttpStatus.NOT_FOUND.value()); // 404
        error.put("error", "Not Found"); // Nombre legible del error
        error.put("message", ex.getMessage()); // Mensaje específico enviado por la excepción
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // Retorna el cuerpo JSON con status 404
    }

    /**
     * Captura los errores de validación de Jakarta (@NotBlank) y retorna HTTP 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Itera sobre cada campo que falló en la validación y extrae su mensaje de error
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now()); // Marca de tiempo
        response.put("status", HttpStatus.BAD_REQUEST.value()); // 400
        response.put("error", "Bad Request"); // Nombre del error
        response.put("validations", errors); // Mapa con los campos que fallaron y por qué
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // Retorna el JSON con status 400
    }

    /**
     * Capturador genérico para cualquier otro error imprevisto (HTTP 500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // 500
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

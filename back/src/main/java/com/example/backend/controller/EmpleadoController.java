package com.example.backend.controller;

// Importaciones de Java Utilities
import java.util.List;

// Importaciones de Spring MVC para endpoints REST y códigos HTTP
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Importaciones de DTOs y Capa de Servicio
import com.example.backend.dto.EmpleadoRequestDTO;
import com.example.backend.dto.EmpleadoResponseDTO;
import com.example.backend.service.EmpleadoService;

// Importaciones de validación y Lombok
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * EmpleadoController
 * Capa de Exposición REST (Presentation Layer).
 * Recibe peticiones HTTP, delega la lógica al Service y responde códigos HTTP estándar con cuerpos JSON.
 */
@RestController // Define esta clase como un controlador REST (retorna datos directamente en formato JSON)
@RequestMapping({"/api/empleados", "/api/employees"}) // Soporta tanto la ruta oficial del reto (/api/empleados) como (/api/employees)
@CrossOrigin(origins = "http://localhost:4200") // Permite peticiones desde la aplicación Angular en puerto 4200 (CORS)
@RequiredArgsConstructor // Inyecta las dependencias marcadas como final a través del constructor
public class EmpleadoController {

    // Dependencia del servicio de empleados inyectada automáticamente
    private final EmpleadoService empleadoService;

    /**
     * Endpoint: GET /api/empleados (y /api/employees)
     * Devuelve la lista completa de todos los empleados registrados.
     * Retorna código HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<EmpleadoResponseDTO>> listar() {
        return ResponseEntity.ok(empleadoService.listar());
    }

    /**
     * Endpoint: POST /api/empleados (y /api/employees)
     * Registra un nuevo colaborador en la base de datos.
     * @Valid: Dispara la validación automática de Jakarta (@NotBlank en los campos del DTO).
     * @RequestBody: Deserializa el cuerpo JSON de la petición entrante al objeto EmpleadoRequestDTO.
     * Retorna código HTTP 201 (Created) con el empleado creado y su ID.
     */
    @PostMapping
    public ResponseEntity<EmpleadoResponseDTO> crear(@Valid @RequestBody EmpleadoRequestDTO request) {
        EmpleadoResponseDTO creado = empleadoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Endpoint: PUT /api/empleados/{id}/asistencia (y /api/employees/{id}/status)
     * Modifica o alterna el estado de asistencia de un colaborador.
     * @PathVariable: Extrae el ID numérico presente en la URL de la petición.
     * @RequestBody(required = false): Permite recibir opcionalmente un booleano (true/false) o un body vacío para alternar.
     * Retorna código HTTP 200 (OK) con el empleado actualizado.
     */
    @PutMapping({"/{id}/asistencia", "/{id}/status"})
    public ResponseEntity<EmpleadoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestBody(required = false) Boolean present) {
        return ResponseEntity.ok(empleadoService.cambiarEstado(id, present));
    }
}

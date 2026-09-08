package com.example.backend.service;

// Importaciones de Java Utilities
import java.util.List;

// Importaciones de Spring Framework para servicios y transaccionalidad
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Importaciones de DTOs, Entidades y Excepciones
import com.example.backend.dto.EmpleadoRequestDTO;
import com.example.backend.dto.EmpleadoResponseDTO;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Empleado;
import com.example.backend.repository.EmpleadoRepository;

// Lombok para inyección de dependencias por constructor
import lombok.RequiredArgsConstructor;

/**
 * EmpleadoService
 * Capa de lógica de negocio (Business Logic Layer).
 * Contiene las reglas del sistema, manipula los datos y transforma Entidades JPA en DTOs.
 */
@Service // Registra esta clase como un Bean de Servicio en el contenedor de Spring
@RequiredArgsConstructor // Lombok genera automáticamente el constructor para inyectar los campos 'final'
public class EmpleadoService {

    // Inyección de dependencia inmutable del repositorio de persistencia
    private final EmpleadoRepository empleadoRepository;

    /**
     * Lista todos los empleados registrados en el sistema.
     * @Transactional(readOnly = true): Optimiza el rendimiento de Hibernate al evitar comprobaciones de cambios.
     */
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> listar() {
        return empleadoRepository.findAll() // Consulta todas las filas de la tabla empleados
                .stream() // Convierte la lista en un flujo de datos (Stream API)
                .map(this::mapToResponse) // Transforma cada Entidad Empleado a su respectivo EmpleadoResponseDTO
                .toList(); // Agrupa los DTOs resultantes en una lista inmutable
    }

    /**
     * Registra un nuevo empleado aplicando la regla de negocio fundamental.
     * @Transactional: Ejecuta la operación dentro de una transacción ACID de base de datos.
     */
    @Transactional
    public EmpleadoResponseDTO crear(EmpleadoRequestDTO request) {
        // Se crea una nueva entidad de base de datos
        Empleado empleado = new Empleado();
        
        // Se limpian espacios en blanco laterales y se asigna el nombre
        empleado.setFullName(request.getFullName().trim());
        
        // Se asigna el cargo o puesto
        empleado.setPosition(request.getPosition().trim());
        
        // REGLA DE NEGOCIO OBLIGATORIA: Todo empleado nuevo nace estrictamente como Ausente (false)
        empleado.setPresent(false);

        // Se guarda físicamente la entidad en la base de datos SQL (generando el ID automático)
        Empleado guardado = empleadoRepository.save(empleado);

        // Se retorna el DTO de respuesta con el ID generado
        return mapToResponse(guardado);
    }

    /**
     * Modifica o alterna el estado de asistencia de un colaborador existente.
     * @param id: Identificador único del empleado
     * @param nuevoEstado: Booleano con el nuevo estado (opcional; si es null, alterna el estado actual)
     */
    @Transactional
    public EmpleadoResponseDTO cambiarEstado(Long id, Boolean nuevoEstado) {
        // Busca el empleado por ID; si no existe, lanza la excepción personalizada 404
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + id));

        // Si el cliente envió un estado booleano explícito se asigna; de lo contrario se invierte el actual
        if (nuevoEstado != null) {
            empleado.setPresent(nuevoEstado);
        } else {
            empleado.setPresent(!empleado.getPresent()); // Alterna: true -> false, o false -> true
        }

        // Se guarda el registro actualizado en la base de datos
        Empleado actualizado = empleadoRepository.save(empleado);

        // Se retorna el DTO con el nuevo estado actualizado
        return mapToResponse(actualizado);
    }

    /**
     * Método auxiliar privado (Mapper) para desacoplar y convertir la entidad Empleado a EmpleadoResponseDTO.
     */
    private EmpleadoResponseDTO mapToResponse(Empleado empleado) {
        return new EmpleadoResponseDTO(
                empleado.getId(),
                empleado.getFullName(),
                empleado.getPosition(),
                empleado.getPresent()
        );
    }
}

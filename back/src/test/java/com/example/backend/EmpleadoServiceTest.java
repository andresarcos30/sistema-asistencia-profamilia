package com.example.backend;

// Aserciones de JUnit 5 para validar los resultados esperados
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Métodos de Mockito para simular y verificar comportamientos
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Utilidades de Java
import java.util.List;
import java.util.Optional;

// Anotaciones del ciclo de vida de tests en JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Anotaciones de Mockito para inyectar mocks
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Clases bajo prueba
import com.example.backend.dto.EmpleadoRequestDTO;
import com.example.backend.dto.EmpleadoResponseDTO;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Empleado;
import com.example.backend.repository.EmpleadoRepository;
import com.example.backend.service.EmpleadoService;

/**
 * EmpleadoServiceTest
 * Pruebas unitarias sobre la lógica de negocio (EmpleadoService) usando JUnit 5 y Mockito.
 * No requiere levantar la base de datos ni Spring Boot completo; ejecuta en pocos milisegundos.
 */
@ExtendWith(MockitoExtension.class) // Habilita el soporte de anotaciones de Mockito en JUnit 5
class EmpleadoServiceTest {

    // @Mock: Crea una versión simulada (falsa) del repositorio para controlar lo que responde
    @Mock
    private EmpleadoRepository empleadoRepository;

    // @InjectMocks: Instancia el servicio real e inyecta el mock del repositorio en su constructor
    @InjectMocks
    private EmpleadoService empleadoService;

    // Objeto de prueba reusable
    private Empleado empleadoEjemplo;

    // Se ejecuta antes de cada método @Test para inicializar el estado
    @BeforeEach
    void setUp() {
        empleadoEjemplo = new Empleado(1L, "Juan Pérez", "Desarrollador", false);
    }

    /**
     * Test 1: Comprueba que el servicio liste empleados y los transforme adecuadamente a DTOs.
     */
    @Test
    void debeListarEmpleados() {
        // Simulación: cuando el repositorio llame a findAll(), retorna una lista con empleadoEjemplo
        when(empleadoRepository.findAll()).thenReturn(List.of(empleadoEjemplo));

        // Ejecución del método a probar
        List<EmpleadoResponseDTO> resultado = empleadoService.listar();

        // Aserciones: validar que el tamaño sea 1 y el nombre coincida
        assertEquals(1, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getFullName());

        // Verificación: comprueba que el método findAll() del repositorio fue llamado exactamente una vez
        verify(empleadoRepository).findAll();
    }

    /**
     * Test 2: Valida la regla de negocio obligatoria: el empleado nuevo nace estrictamente como Ausente (false).
     */
    @Test
    void debeCrearEmpleadoComoAusente() {
        // Datos de entrada simulados
        EmpleadoRequestDTO request = new EmpleadoRequestDTO("Juan Pérez", "Desarrollador");

        // Simulación: al guardar cualquier Empleado, simula que la BD le asigna el ID 1 y lo devuelve
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(invocation -> {
            Empleado e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        // Ejecución
        EmpleadoResponseDTO resultado = empleadoService.crear(request);

        // Aserciones
        assertNotNull(resultado.getId());
        assertEquals("Juan Pérez", resultado.getFullName());
        assertEquals("Desarrollador", resultado.getPosition());
        // REGLA CRÍTICA: debe ser false (Ausente)
        assertFalse(resultado.getPresent(), "El nuevo empleado debe registrarse inicialmente como Ausente (false)");

        // Verifica que se llamó a save()
        verify(empleadoRepository).save(any(Empleado.class));
    }

    /**
     * Test 3: Comprueba que se pueda modificar el estado de asistencia a Presente (true).
     */
    @Test
    void debeCambiarEstadoAsistencia() {
        // Simulación: findById(1L) encuentra el empleadoEjemplo existente
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoEjemplo));
        // Simulación: save retorna el mismo objeto guardado
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecución: cambiar estado a true
        EmpleadoResponseDTO resultado = empleadoService.cambiarEstado(1L, true);

        // Aserción: validar que el estado cambió a true
        assertTrue(resultado.getPresent());
        verify(empleadoRepository).findById(1L);
        verify(empleadoRepository).save(empleadoEjemplo);
    }

    /**
     * Test 4: Comprueba que se lance ResourceNotFoundException si el ID buscado no existe.
     */
    @Test
    void debeLanzarExcepcionSiEmpleadoNoExiste() {
        // Simulación: el ID 999 no existe en la base de datos (Optional.empty)
        when(empleadoRepository.findById(999L)).thenReturn(Optional.empty());

        // Aserción: comprueba que se dispare ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> empleadoService.cambiarEstado(999L, true));

        // Verifica que se buscó por ID pero NUNCA se intentó guardar nada (evita escrituras erróneas)
        verify(empleadoRepository).findById(999L);
        verify(empleadoRepository, never()).save(any());
    }
}

package com.example.backend.model;

// Importaciones de Jakarta Persistence (estándar JPA para mapeo objeto-relacional)
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Importaciones de Lombok para reducir código repetitivo (boiler-plate)
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Entity: Indica a JPA/Hibernate que esta clase representa una tabla en la base de datos
@Entity
// @Table: Define el nombre explícito de la tabla en la base de datos SQL
@Table(name = "empleados")
// @Data: Anotación de Lombok que genera automáticamente Getters, Setters,
// toString, equals y hashCode
@Data
// @NoArgsConstructor: Genera el constructor sin argumentos, requerido
// obligatoriamente por JPA
@NoArgsConstructor
// @AllArgsConstructor: Genera un constructor con todos los atributos de la
// clase
@AllArgsConstructor
public class Empleado {

    // @Id: Define este campo como la Clave Primaria (Primary Key) de la entidad
    @Id
    // @GeneratedValue: Define la estrategia de generación automática; IDENTITY usa
    // el auto-incremento nativo del motor SQL
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(nullable = false): Establece la restricción NOT NULL a nivel de
    // esquema en la base de datos
    @Column(nullable = false)
    private String fullName;

    // Columna para almacenar el cargo o puesto del colaborador (ej: Médico,
    // Enfermera), no puede ser nula
    @Column(nullable = false)
    private String position;

    // Columna para almacenar el estado de asistencia; por defecto se inicializa en
    // false (Ausente)
    @Column(nullable = false)
    private Boolean present = false;

    // Constructor personalizado para instanciar empleados nuevos sin necesidad de
    // especificar ID ni asistencia
    public Empleado(String fullName, String position) {
        this.fullName = fullName;
        this.position = position;
        this.present = false; // Regla de negocio de Profamilia: todo empleado nuevo nace siempre Ausente
    }
}

package com.example.backend.repository;

// JpaRepository: Provee métodos listos para usar (save, findById, findAll, delete, etc.) sin escribir SQL
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Modelo entidad que gestionará este repositorio
import com.example.backend.model.Empleado;

/**
 * EmpleadoRepository
 * Capa de persistencia que conecta la aplicación con la base de datos SQL (H2).
 * Al extender JpaRepository<Empleado, Long>, Spring Data JPA genera automáticamente las consultas SQL en memoria.
 */
@Repository // Marca esta interfaz como un componente de acceso a datos en el contenedor de Spring
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    // No requiere código manual: hereda todos los métodos CRUD básicos de Spring Data
}

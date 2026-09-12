package com.example.store.categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNombreAndPadreId(String nombre, Long padreId);
    boolean existsByNombreAndPadreIsNull(String nombre);
    List<Categoria> findByPadreId(Long padreId);
    List<Categoria> findByPadreIsNull();
    long countByPadreId(Long padreId);
}

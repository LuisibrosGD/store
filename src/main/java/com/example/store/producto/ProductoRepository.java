package com.example.store.producto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    boolean existsByCategoriaId(Long categoriaId);
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<Producto> findByCategoriaId(Long categoriaId, Pageable pageable);
    Page<Producto> findByPrecioBetween(Double min, Double max, Pageable pageable);
    Page<Producto> findByCategoriaIdAndPrecioBetween(Long categoriaId, Double min, Double max, Pageable pageable);
    Page<Producto> findByNombreContainingIgnoreCaseAndCategoriaId(String nombre, Long categoriaId, Pageable pageable);
    Page<Producto> findByNombreContainingIgnoreCaseAndPrecioBetween(String nombre, Double min, Double max, Pageable pageable);
    Page<Producto> findByNombreContainingIgnoreCaseAndCategoriaIdAndPrecioBetween(
            String nombre, Long categoriaId, Double min, Double max, Pageable pageable);
}

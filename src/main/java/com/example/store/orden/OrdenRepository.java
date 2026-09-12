package com.example.store.orden;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    List<Orden> findAllByOrderByFechaCreacionDesc();
}

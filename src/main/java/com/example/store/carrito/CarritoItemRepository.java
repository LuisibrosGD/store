package com.example.store.carrito;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    java.util.List<CarritoItem> findByCarritoId(Long carritoId);
    CarritoItem findByCarritoIdAndProductoId(Long carritoId, Long productoId);
    void deleteByCarritoId(Long carritoId);
}

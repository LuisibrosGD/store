package com.example.store.carrito;

import com.example.store.carrito.dto.*;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.producto.Producto;
import com.example.store.producto.ProductoRepository;
import com.example.store.usuario.Usuario;
import com.example.store.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarritoService(CarritoRepository carritoRepository,
                          CarritoItemRepository carritoItemRepository,
                          ProductoRepository productoRepository,
                          UsuarioRepository usuarioRepository) {
        this.carritoRepository = carritoRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public CarritoResponse obtenerCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        List<CarritoItem> items = carritoItemRepository.findByCarritoId(carrito.getId());
        return toResponse(carrito, items);
    }

    @Transactional
    public CarritoResponse agregarItem(Long usuarioId, AgregarItemRequest request) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + request.getProductoId()));

        if (producto.getStock() <= 0) {
            throw new IllegalArgumentException("El producto no tiene stock disponible");
        }

        CarritoItem existente = carritoItemRepository.findByCarritoIdAndProductoId(carrito.getId(), producto.getId());
        if (existente != null) {
            int nuevaCantidad = existente.getCantidad() + request.getCantidad();
            if (nuevaCantidad > producto.getStock()) {
                throw new IllegalArgumentException("No hay stock suficiente");
            }
            existente.setCantidad(nuevaCantidad);
            carritoItemRepository.save(existente);
        } else {
            if (request.getCantidad() > producto.getStock()) {
                throw new IllegalArgumentException("No hay stock suficiente");
            }
            CarritoItem item = new CarritoItem(carrito, producto, request.getCantidad());
            carritoItemRepository.save(item);
        }

        return obtenerCarrito(usuarioId);
    }

    @Transactional
    public CarritoResponse cambiarCantidad(Long usuarioId, Long productoId, Integer nuevaCantidad) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        CarritoItem item = carritoItemRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId);

        if (item == null) {
            throw new ResourceNotFoundException("El producto no está en el carrito");
        }

        if (nuevaCantidad == 0) {
            carritoItemRepository.delete(item);
        } else {
            Producto producto = item.getProducto();
            if (nuevaCantidad > producto.getStock()) {
                throw new IllegalArgumentException("No hay stock suficiente");
            }
            item.setCantidad(nuevaCantidad);
            carritoItemRepository.save(item);
        }

        return obtenerCarrito(usuarioId);
    }

    @Transactional
    public void quitarItem(Long usuarioId, Long productoId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        CarritoItem item = carritoItemRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId);

        if (item == null) {
            throw new ResourceNotFoundException("El producto no está en el carrito");
        }

        carritoItemRepository.delete(item);
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        carritoItemRepository.deleteByCarritoId(carrito.getId());
    }

    private Carrito obtenerOCrearCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId);
        if (carrito == null) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));
            carrito = new Carrito(usuario);
            carrito = carritoRepository.save(carrito);
        }
        return carrito;
    }

    private CarritoResponse toResponse(Carrito carrito, List<CarritoItem> items) {
        List<CarritoItemResponse> itemResponses = items.stream()
                .map(i -> new CarritoItemResponse(
                        i.getId(),
                        i.getProducto().getId(),
                        i.getProducto().getNombre(),
                        i.getProducto().getPrecio(),
                        i.getCantidad(),
                        i.getProducto().getPrecio() * i.getCantidad()
                ))
                .collect(Collectors.toList());

        double total = itemResponses.stream()
                .mapToDouble(CarritoItemResponse::getSubtotal)
                .sum();

        return new CarritoResponse(carrito.getId(), itemResponses, total);
    }
}

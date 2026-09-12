package com.example.store.producto;

import com.example.store.categoria.Categoria;
import com.example.store.categoria.CategoriaRepository;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.producto.dto.ProductoRequest;
import com.example.store.producto.dto.ProductoResponse;
import com.example.store.common.PaginaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public PaginaResponse<ProductoResponse> listar(Pageable pageable, int currentPage, int size) {
        return new PaginaResponse<>(productoRepository.findAll(pageable).map(this::toResponse), currentPage, size);
    }

    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return toResponse(producto);
    }

    public ProductoResponse crear(ProductoRequest request) {
        if (request.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoriaId()));
        Producto producto = new Producto(request.getNombre(), request.getPrecio(), request.getStock(), categoria);
        Producto guardado = productoRepository.save(producto);
        return toResponse(guardado);
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        if (request.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoriaId()));
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);
        Producto guardado = productoRepository.save(producto);
        return toResponse(guardado);
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }

    public ProductoResponse actualizarStock(Long id, Integer nuevoStock) {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        producto.setStock(nuevoStock);
        Producto guardado = productoRepository.save(producto);
        return toResponse(guardado);
    }

    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(p.getId(), p.getNombre(), p.getPrecio(), p.getStock(),
                p.getCategoria().getId(), p.getCategoria().getNombre());
    }
}

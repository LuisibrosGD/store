package com.example.store.producto;

import com.example.store.producto.dto.ProductoResponse;
import com.example.store.common.PaginaResponse;
import com.example.store.common.Paginacion;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class BusquedaController {

    private final ProductoRepository productoRepository;

    public BusquedaController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/buscar")
    public PaginaResponse<ProductoResponse> buscar(@RequestParam String nombre,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer pageIndex,
                                                   @RequestParam(required = false) Integer size) {
        Page<ProductoResponse> resultado = productoRepository.findByNombreContainingIgnoreCase(nombre,
                Paginacion.crear(page, pageIndex, size)).map(this::toResponse);
        return new PaginaResponse<>(resultado, Paginacion.actual(page, pageIndex),
                size == null ? Paginacion.DEFAULT_SIZE : size);
    }

    @GetMapping("/filtrar")
    public PaginaResponse<ProductoResponse> filtrar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageIndex,
            @RequestParam(required = false) Integer size) {
        if (precioMin != null && precioMax != null && precioMin > precioMax) {
            throw new IllegalArgumentException("El precio mínimo no puede ser mayor que el máximo");
        }
        Specification<Producto> especificacion = (root, query, cb) -> cb.conjunction();
        if (nombre != null) especificacion = especificacion.and((root, query, cb) ->
                cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
        if (categoriaId != null) especificacion = especificacion.and((root, query, cb) ->
                cb.equal(root.get("categoria").get("id"), categoriaId));
        if (precioMin != null) especificacion = especificacion.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("precio"), precioMin));
        if (precioMax != null) especificacion = especificacion.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("precio"), precioMax));
        Page<ProductoResponse> resultado = productoRepository.findAll(especificacion,
                Paginacion.crear(page, pageIndex, size)).map(this::toResponse);
        return new PaginaResponse<>(resultado, Paginacion.actual(page, pageIndex),
                size == null ? Paginacion.DEFAULT_SIZE : size);
    }

    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(p.getId(), p.getNombre(), p.getPrecio(), p.getStock(),
                p.getCategoria().getId(), p.getCategoria().getNombre());
    }
}

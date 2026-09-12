package com.example.store.producto;

import com.example.store.producto.dto.ProductoRequest;
import com.example.store.producto.dto.ProductoResponse;
import com.example.store.producto.dto.StockRequest;
import com.example.store.common.PaginaResponse;
import com.example.store.common.Paginacion;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public PaginaResponse<ProductoResponse> listar(@RequestParam(required = false) Integer page,
                                                    @RequestParam(required = false) Integer pageIndex,
                                                    @RequestParam(required = false) Integer size) {
        return productoService.listar(Paginacion.crear(page, pageIndex, size), Paginacion.actual(page, pageIndex),
                size == null ? Paginacion.DEFAULT_SIZE : size);
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
        return productoService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductoResponse actualizarStock(@PathVariable Long id, @Valid @RequestBody StockRequest request) {
        return productoService.actualizarStock(id, request.getStock());
    }
}

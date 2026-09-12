package com.example.store.categoria;

import com.example.store.categoria.dto.CategoriaRequest;
import com.example.store.categoria.dto.CategoriaResponse;
import com.example.store.common.PaginaResponse;
import com.example.store.common.Paginacion;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaResponse> listarArbol() {
        return categoriaService.listarArbol();
    }

    @GetMapping("/{id}")
    public CategoriaResponse obtener(@PathVariable Long id) {
        return categoriaService.obtenerPorId(id);
    }

    @GetMapping("/paginadas")
    public PaginaResponse<CategoriaResponse> listarPaginadas(@RequestParam(required = false) Integer page,
                                                              @RequestParam(required = false) Integer pageIndex,
                                                              @RequestParam(required = false) Integer size) {
        return categoriaService.listarPaginadas(Paginacion.crear(page, pageIndex, size), Paginacion.actual(page, pageIndex),
                size == null ? Paginacion.DEFAULT_SIZE : size);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse crear(@Valid @RequestBody CategoriaRequest request) {
        return categoriaService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return categoriaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
    }
}

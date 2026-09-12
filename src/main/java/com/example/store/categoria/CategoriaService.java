package com.example.store.categoria;

import com.example.store.categoria.dto.CategoriaRequest;
import com.example.store.categoria.dto.CategoriaResponse;
import com.example.store.exception.DuplicateResourceException;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.producto.ProductoRepository;
import com.example.store.common.PaginaResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    public List<CategoriaResponse> listarArbol() {
        List<Categoria> raices = categoriaRepository.findByPadreIsNull();
        return raices.stream()
                .map(this::toArbol)
                .collect(Collectors.toList());
    }

    public CategoriaResponse obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        List<CategoriaResponse> hijos = categoriaRepository.findByPadreId(id).stream()
                .map(this::toArbol)
                .collect(Collectors.toList());
        Long padreId = categoria.getPadre() != null ? categoria.getPadre().getId() : null;
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), padreId, hijos);
    }

    public PaginaResponse<CategoriaResponse> listarPaginadas(Pageable pageable, int currentPage, int size) {
        return new PaginaResponse<>(categoriaRepository.findAll(pageable).map(c ->
                new CategoriaResponse(c.getId(), c.getNombre(), c.getPadre() == null ? null : c.getPadre().getId(), List.of())),
                currentPage, size);
    }

    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria padre = null;
        if (request.getPadreId() != null) {
            padre = categoriaRepository.findById(request.getPadreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría padre no encontrada con id: " + request.getPadreId()));
        }

        if (padre != null && padre.getPadre() != null) {
            throw new IllegalArgumentException("La estructura de categorías permite máximo 2 niveles de profundidad");
        }

        boolean existe;
        if (padre != null) {
            existe = categoriaRepository.existsByNombreAndPadreId(request.getNombre(), padre.getId());
        } else {
            existe = categoriaRepository.existsByNombreAndPadreIsNull(request.getNombre());
        }

        if (existe) {
            throw new DuplicateResourceException("Ya existe una categoría con ese nombre bajo el mismo padre");
        }

        Categoria categoria = new Categoria(request.getNombre(), padre);
        Categoria guardada = categoriaRepository.save(categoria);
        return new CategoriaResponse(guardada.getId(), guardada.getNombre(),
                padre != null ? padre.getId() : null, new ArrayList<>());
    }

    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));

        Long padreIdActual = categoria.getPadre() != null ? categoria.getPadre().getId() : null;
        boolean existeOtra;
        if (padreIdActual != null) {
            existeOtra = categoriaRepository.existsByNombreAndPadreId(request.getNombre(), padreIdActual);
        } else {
            existeOtra = categoriaRepository.existsByNombreAndPadreIsNull(request.getNombre());
        }

        if (existeOtra && !categoria.getNombre().equals(request.getNombre())) {
            throw new DuplicateResourceException("Ya existe una categoría con ese nombre");
        }

        categoria.setNombre(request.getNombre());
        Categoria guardada = categoriaRepository.save(categoria);
        List<CategoriaResponse> hijos = categoriaRepository.findByPadreId(id).stream()
                .map(this::toArbol)
                .collect(Collectors.toList());
        return new CategoriaResponse(guardada.getId(), guardada.getNombre(), padreIdActual, hijos);
    }

    public void eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));

        if (categoriaRepository.countByPadreId(id) > 0) {
            throw new IllegalArgumentException("No se puede eliminar porque tiene subcategorías dependientes");
        }
        if (productoRepository.existsByCategoriaId(id)) {
            throw new IllegalArgumentException("No se puede eliminar porque tiene productos asociados");
        }
        categoriaRepository.deleteById(id);
    }

    private CategoriaResponse toArbol(Categoria categoria) {
        List<CategoriaResponse> hijos = categoriaRepository.findByPadreId(categoria.getId()).stream()
                .map(this::toArbol)
                .collect(Collectors.toList());
        Long padreId = categoria.getPadre() != null ? categoria.getPadre().getId() : null;
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), padreId, hijos);
    }
}

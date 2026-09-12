package com.example.store.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class Paginacion {
    public static final int DEFAULT_SIZE = 10;

    private Paginacion() { }

    public static Pageable crear(Integer page, Integer pageIndex, Integer size) {
        int tamanio = size == null ? DEFAULT_SIZE : size;
        if (tamanio < 1) throw new IllegalArgumentException("El tamaño de página debe ser mayor que cero");
        if (pageIndex != null) {
            if (pageIndex < 0) throw new IllegalArgumentException("pageIndex no puede ser negativo");
            return PageRequest.of(pageIndex, tamanio);
        }
        int pagina = page == null ? 1 : page;
        if (pagina < 0) throw new IllegalArgumentException("La página no puede ser negativa");
        return PageRequest.of(pagina == 0 ? 0 : pagina - 1, tamanio);
    }

    public static int actual(Integer page, Integer pageIndex) {
        if (pageIndex != null) return pageIndex + 1;
        return page == null || page == 0 ? 1 : page;
    }
}

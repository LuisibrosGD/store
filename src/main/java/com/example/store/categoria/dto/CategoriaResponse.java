package com.example.store.categoria.dto;

import java.util.List;

public class CategoriaResponse {

    private Long id;
    private String nombre;
    private Long padreId;
    private List<CategoriaResponse> subcategorias;

    public CategoriaResponse(Long id, String nombre, Long padreId, List<CategoriaResponse> subcategorias) {
        this.id = id;
        this.nombre = nombre;
        this.padreId = padreId;
        this.subcategorias = subcategorias;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Long getPadreId() { return padreId; }
    public void setPadreId(Long padreId) { this.padreId = padreId; }
    public List<CategoriaResponse> getSubcategorias() { return subcategorias; }
    public void setSubcategorias(List<CategoriaResponse> subcategorias) { this.subcategorias = subcategorias; }
}

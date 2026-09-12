package com.example.store.orden.dto;

import java.util.List;

public class OrdenResponse {

    private Long id;
    private Long usuarioId;
    private String estado;
    private Double total;
    private String fechaCreacion;
    private List<OrdenItemResponse> items;

    public OrdenResponse(Long id, Long usuarioId, String estado, Double total,
                         String fechaCreacion, List<OrdenItemResponse> items) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.estado = estado;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
        this.items = items;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<OrdenItemResponse> getItems() { return items; }
    public void setItems(List<OrdenItemResponse> items) { this.items = items; }
}

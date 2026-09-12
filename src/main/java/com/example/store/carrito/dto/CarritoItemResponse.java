package com.example.store.carrito.dto;

public class CarritoItemResponse {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private Double productoPrecio;
    private Integer cantidad;
    private Double subtotal;

    public CarritoItemResponse(Long id, Long productoId, String productoNombre,
                               Double productoPrecio, Integer cantidad, Double subtotal) {
        this.id = id;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.productoPrecio = productoPrecio;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public Double getProductoPrecio() { return productoPrecio; }
    public void setProductoPrecio(Double productoPrecio) { this.productoPrecio = productoPrecio; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}

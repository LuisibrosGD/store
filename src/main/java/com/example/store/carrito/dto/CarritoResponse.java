package com.example.store.carrito.dto;

import java.util.List;

public class CarritoResponse {

    private Long id;
    private List<CarritoItemResponse> items;
    private Double total;

    public CarritoResponse(Long id, List<CarritoItemResponse> items, Double total) {
        this.id = id;
        this.items = items;
        this.total = total;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public List<CarritoItemResponse> getItems() { return items; }
    public void setItems(List<CarritoItemResponse> items) { this.items = items; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}

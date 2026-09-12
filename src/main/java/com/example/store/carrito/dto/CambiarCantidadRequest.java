package com.example.store.carrito.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class CambiarCantidadRequest {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}

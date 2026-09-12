package com.example.store.carrito;

import com.example.store.carrito.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public CarritoResponse obtener(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return carritoService.obtenerCarrito(usuarioId);
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CarritoResponse agregarItem(Authentication authentication,
                                       @Valid @RequestBody AgregarItemRequest request) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return carritoService.agregarItem(usuarioId, request);
    }

    @PutMapping("/items/{productoId}")
    public CarritoResponse cambiarCantidad(Authentication authentication,
                                           @PathVariable Long productoId,
                                           @Valid @RequestBody CambiarCantidadRequest request) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return carritoService.cambiarCantidad(usuarioId, productoId, request.getCantidad());
    }

    @DeleteMapping("/items/{productoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarItem(Authentication authentication, @PathVariable Long productoId) {
        Long usuarioId = (Long) authentication.getPrincipal();
        carritoService.quitarItem(usuarioId, productoId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void vaciar(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        carritoService.vaciarCarrito(usuarioId);
    }
}

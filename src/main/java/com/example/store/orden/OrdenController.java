package com.example.store.orden;

import com.example.store.orden.dto.CambiarEstadoRequest;
import com.example.store.orden.dto.OrdenResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenResponse checkout(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ordenService.checkout(usuarioId);
    }

    @GetMapping("/{id}")
    public OrdenResponse obtener(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ordenService.obtenerPorId(id, usuarioId, esAdmin(authentication));
    }

    @GetMapping
    public List<OrdenResponse> historial(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ordenService.historial(usuarioId);
    }

    @GetMapping("/admin/todas")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrdenResponse> listarTodas() {
        return ordenService.listarTodas();
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public OrdenResponse cambiarEstado(@PathVariable Long id,
                                       @Valid @RequestBody CambiarEstadoRequest request) {
        return ordenService.cambiarEstado(id, request.getEstado());
    }

    @PostMapping("/{id}/cancelar")
    public OrdenResponse cancelar(@PathVariable Long id, Authentication authentication) {
        return ordenService.cancelar(id, (Long) authentication.getPrincipal());
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}

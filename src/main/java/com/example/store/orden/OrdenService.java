package com.example.store.orden;

import com.example.store.carrito.Carrito;
import com.example.store.carrito.CarritoItem;
import com.example.store.carrito.CarritoItemRepository;
import com.example.store.carrito.CarritoRepository;
import com.example.store.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import com.example.store.orden.dto.OrdenItemResponse;
import com.example.store.orden.dto.OrdenResponse;
import com.example.store.producto.Producto;
import com.example.store.producto.ProductoRepository;
import com.example.store.usuario.Usuario;
import com.example.store.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrdenService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("PENDIENTE", "PAGADA", "ENVIADA", "ENTREGADA", "CANCELADA");

    private final OrdenRepository ordenRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public OrdenService(OrdenRepository ordenRepository,
                        CarritoRepository carritoRepository,
                        CarritoItemRepository carritoItemRepository,
                        ProductoRepository productoRepository,
                        UsuarioRepository usuarioRepository) {
        this.ordenRepository = ordenRepository;
        this.carritoRepository = carritoRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public OrdenResponse checkout(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId);
        if (carrito == null) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        List<CarritoItem> items = carritoItemRepository.findByCarritoId(carrito.getId());
        if (items.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        for (CarritoItem item : items) {
            Producto producto = item.getProducto();
            if (item.getCantidad() > producto.getStock()) {
                throw new IllegalArgumentException("No hay stock suficiente para el producto: " + producto.getNombre());
            }
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        double total = 0.0;
        Orden orden = new Orden(usuario, "PENDIENTE", 0.0);
        orden = ordenRepository.save(orden);

        for (CarritoItem item : items) {
            Producto producto = item.getProducto();
            double subtotal = producto.getPrecio() * item.getCantidad();
            total += subtotal;

            OrdenItem ordenItem = new OrdenItem(orden, producto, item.getCantidad(), producto.getPrecio());
            orden.getItems().add(ordenItem);

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        orden.setTotal(total);
        ordenRepository.save(orden);

        carritoItemRepository.deleteByCarritoId(carrito.getId());

        return toResponse(orden);
    }

    @Transactional(readOnly = true)
    public OrdenResponse obtenerPorId(Long ordenId, Long usuarioId, boolean esAdmin) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + ordenId));
        if (!esAdmin && !orden.getUsuario().getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Orden no encontrada con id: " + ordenId);
        }
        return toResponse(orden);
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> historial(Long usuarioId) {
        return ordenRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrdenResponse cambiarEstado(Long ordenId, String nuevoEstado) {
        if (!ESTADOS_VALIDOS.contains(nuevoEstado)) {
            throw new IllegalArgumentException("El estado proporcionado no es válido: " + nuevoEstado);
        }

        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + ordenId));

        if ("CANCELADA".equals(nuevoEstado)) {
            throw new IllegalArgumentException("La cancelación debe realizarse mediante el endpoint de cancelación");
        }
        if (!esTransicionAdministrativaValida(orden.getEstado(), nuevoEstado)) {
            throw new IllegalArgumentException("La transición de estado no es válida");
        }

        orden.setEstado(nuevoEstado);
        ordenRepository.save(orden);
        return toResponse(orden);
    }

    @Transactional
    public OrdenResponse cancelar(Long ordenId, Long usuarioId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + ordenId));
        if (!orden.getUsuario().getId().equals(usuarioId)) {
            throw new AccessDeniedException("No tiene permisos para cancelar esta orden");
        }
        if (!"PENDIENTE".equals(orden.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden cancelar órdenes pendientes");
        }
        for (OrdenItem item : orden.getItems()) {
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() + item.getCantidad());
            productoRepository.save(producto);
        }
        orden.setEstado("CANCELADA");
        return toResponse(ordenRepository.save(orden));
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> listarTodas() {
        return ordenRepository.findAllByOrderByFechaCreacionDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private boolean esTransicionAdministrativaValida(String actual, String nuevo) {
        return ("PENDIENTE".equals(actual) && "PAGADA".equals(nuevo))
                || ("PAGADA".equals(actual) && "ENVIADA".equals(nuevo))
                || ("ENVIADA".equals(actual) && "ENTREGADA".equals(nuevo));
    }

    private OrdenResponse toResponse(Orden orden) {
        List<OrdenItemResponse> itemResponses = orden.getItems().stream()
                .map(i -> new OrdenItemResponse(
                        i.getId(),
                        i.getProducto().getId(),
                        i.getProducto().getNombre(),
                        i.getCantidad(),
                        i.getPrecioUnitario(),
                        i.getPrecioUnitario() * i.getCantidad()
                ))
                .collect(Collectors.toList());

        return new OrdenResponse(
                orden.getId(),
                orden.getUsuario().getId(),
                orden.getEstado(),
                orden.getTotal(),
                orden.getFechaCreacion() != null ? orden.getFechaCreacion().toString() : null,
                itemResponses
        );
    }
}

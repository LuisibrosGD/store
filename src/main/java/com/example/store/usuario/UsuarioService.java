package com.example.store.usuario;

import com.example.store.usuario.dto.ActualizarPerfilRequest;
import com.example.store.usuario.dto.UsuarioRequest;
import com.example.store.usuario.dto.UsuarioResponse;
import com.example.store.exception.DuplicateResourceException;
import com.example.store.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRole()))
                .collect(Collectors.toList());
    }

    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRole());
    }

    public UsuarioResponse crear(UsuarioRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("El email ya está registrado: " + request.getEmail());
        }
        String role = esAdmin && request.getRole() != null && !request.getRole().isEmpty()
                ? request.getRole() : "CLIENTE";
        Usuario usuario = new Usuario(request.getNombre(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()), role);
        Usuario guardado = usuarioRepository.save(usuario);
        return new UsuarioResponse(guardado.getId(), guardado.getNombre(), guardado.getEmail(), guardado.getRole());
    }

    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (usuarioRepository.existsByEmail(request.getEmail())
                && !usuario.getEmail().equals(request.getEmail())) {
            throw new DuplicateResourceException("El email ya está registrado: " + request.getEmail());
        }

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            usuario.setRole(request.getRole());
        }
        Usuario actualizado = usuarioRepository.save(usuario);
        return new UsuarioResponse(actualizado.getId(), actualizado.getNombre(), actualizado.getEmail(), actualizado.getRole());
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private Long obtenerUserIdActual() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public UsuarioResponse obtenerMiPerfil() {
        Long userId = obtenerUserIdActual();
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRole());
    }

    public UsuarioResponse actualizarMiPerfil(ActualizarPerfilRequest request) {
        Long userId = obtenerUserIdActual();
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (usuarioRepository.existsByEmail(request.getEmail())
                && !usuario.getEmail().equals(request.getEmail())) {
            throw new DuplicateResourceException("El email ya está registrado: " + request.getEmail());
        }

        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        Usuario actualizado = usuarioRepository.save(usuario);
        return new UsuarioResponse(actualizado.getId(), actualizado.getNombre(), actualizado.getEmail(), actualizado.getRole());
    }

    public void eliminarMiCuenta() {
        Long userId = obtenerUserIdActual();
        if (!usuarioRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(userId);
    }
}

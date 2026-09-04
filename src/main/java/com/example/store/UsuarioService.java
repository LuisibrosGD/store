package com.example.store;

import com.example.store.dto.LoginRequest;
import com.example.store.dto.UsuarioRequest;
import com.example.store.dto.UsuarioResponse;
import com.example.store.exception.DuplicateResourceException;
import com.example.store.exception.InvalidCredentialsException;
import com.example.store.exception.ResourceNotFoundException;
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
                .map(u -> new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail()))
                .collect(Collectors.toList());
    }

    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }

    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("El email ya está registrado: " + request.getEmail());
        }
        Usuario usuario = new Usuario(request.getNombre(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()));
        Usuario guardado = usuarioRepository.save(usuario);
        return new UsuarioResponse(guardado.getId(), guardado.getNombre(), guardado.getEmail());
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
        Usuario actualizado = usuarioRepository.save(usuario);
        return new UsuarioResponse(actualizado.getId(), actualizado.getNombre(), actualizado.getEmail());
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public UsuarioResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("El email no está registrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new InvalidCredentialsException("Las credenciales son incorrectas");
        }

        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }
}

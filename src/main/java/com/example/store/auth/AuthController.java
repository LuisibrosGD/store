package com.example.store.auth;

import com.example.store.auth.dto.AuthRequest;
import com.example.store.auth.dto.AuthResponse;
import com.example.store.auth.jwt.JwtTokenProvider;
import com.example.store.exception.InvalidCredentialsException;
import com.example.store.usuario.Usuario;
import com.example.store.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping({"/api/auth/login", "/api/usuarios/login"})
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("El email no está registrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new InvalidCredentialsException("Las credenciales son incorrectas");
        }

        return respuesta(usuario);
    }

    @PostMapping("/api/auth/refresh")
    public AuthResponse refresh(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new InvalidCredentialsException("El usuario ya no existe"));
        usuario.setTokenVersion(usuario.getTokenVersion() + 1);
        usuarioRepository.save(usuario);
        return respuesta(usuario);
    }

    private AuthResponse respuesta(Usuario usuario) {
        String token = tokenProvider.generateToken(usuario.getId(), usuario.getEmail(), usuario.getRole(), usuario.getTokenVersion());
        return new AuthResponse(token, usuario.getId(), usuario.getEmail(), usuario.getRole());
    }
}

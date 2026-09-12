package com.example.store.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.store.usuario.Usuario;
import com.example.store.usuario.UsuarioRepository;

import java.io.IOException;
import java.util.List;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UsuarioRepository usuarioRepository) {
        this.tokenProvider = tokenProvider;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                tokenProvider.parseToken(token);
                String email = tokenProvider.getEmailFromToken(token);
                String role = tokenProvider.getRoleFromToken(token);
                Long userId = tokenProvider.getUserIdFromToken(token);
                Long tokenVersion = tokenProvider.getTokenVersionFromToken(token);
                Usuario usuario = usuarioRepository.findById(userId).orElse(null);

                if (usuario != null && usuario.getTokenVersion().equals(tokenVersion)) {
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    request.setAttribute("jwtError", "El token es inválido");
                }
            } catch (ExpiredJwtException ex) {
                request.setAttribute("jwtError", "El token ha expirado");
            } catch (JwtException | IllegalArgumentException ex) {
                request.setAttribute("jwtError", "El token es inválido");
            }
        }

        filterChain.doFilter(request, response);
    }
}

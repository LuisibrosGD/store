package com.example.store;

import com.example.store.auth.jwt.JwtTokenProvider;
import com.example.store.categoria.Categoria;
import com.example.store.categoria.CategoriaRepository;
import com.example.store.producto.Producto;
import com.example.store.producto.ProductoRepository;
import com.example.store.usuario.Usuario;
import com.example.store.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StoreApplicationTests {
    @DynamicPropertySource
    static void configurarJwtDePrueba(DynamicPropertyRegistry registry) {
        registry.add("jwt.secret", () -> "a".repeat(64));
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider tokenProvider;

    private Usuario admin;
    private Usuario cliente;
    private Categoria categoria;

    @BeforeEach
    void prepararDatos() {
        admin = usuarioRepository.save(new Usuario("Admin", "admin@test.com", passwordEncoder.encode("clave123"), "ADMIN"));
        cliente = usuarioRepository.save(new Usuario("Cliente", "cliente@test.com", passwordEncoder.encode("clave123"), "CLIENTE"));
        categoria = categoriaRepository.save(new Categoria("Tecnología", null));
    }

    @Test
    void registroPublicoFuerzaClienteYLoginTieneAlias() throws Exception {
        mockMvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Nueva\",\"email\":\"nueva@test.com\",\"password\":\"clave123\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("CLIENTE"));

        mockMvc.perform(post("/api/usuarios").header("Authorization", bearer(cliente))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Otro\",\"email\":\"otro@test.com\",\"password\":\"clave123\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/usuarios").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Otro Admin\",\"email\":\"otro-admin@test.com\",\"password\":\"clave123\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("ADMIN"));

        mockMvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nueva@test.com\",\"password\":\"clave123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void permisosYPaginacionDeCatalogoSeCumplen() throws Exception {
        productoRepository.save(new Producto("Phone Uno", 100.0, 4, categoria));
        productoRepository.save(new Producto("Phone Dos", 200.0, 4, categoria));
        mockMvc.perform(post("/api/productos").header("Authorization", bearer(cliente))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Prohibido\",\"precio\":1,\"stock\":1,\"categoriaId\":" + categoria.getId() + "}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/productos").param("page", "1").param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.size").value(1)).andExpect(jsonPath("$.content.length()").value(1));
        mockMvc.perform(get("/api/productos/filtrar").param("categoriaId", categoria.getId().toString())
                        .param("precioMax", "150"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void refreshInvalidaElTokenAnteriorYProtegidosRechazanAusencia() throws Exception {
        String original = bearer(cliente);
        mockMvc.perform(get("/api/carrito")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Se requiere autenticación"));
        String body = mockMvc.perform(post("/api/auth/refresh").header("Authorization", original))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        String nuevo = body.replaceAll(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");
        mockMvc.perform(get("/api/carrito").header("Authorization", original)).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer " + nuevo)).andExpect(status().isOk());
    }

    @Test
    void categoriasAceptanParentIdYExigenAdministrador() throws Exception {
        mockMvc.perform(post("/api/categorias").header("Authorization", bearer(cliente))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"No permitida\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/categorias").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Subcategoría\",\"parentId\":" + categoria.getId() + "}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.padreId").value(categoria.getId()));
        mockMvc.perform(get("/api/categorias/paginadas").param("page", "1").param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void checkoutYCancellationDelDuenoRestauranStock() throws Exception {
        Producto producto = productoRepository.save(new Producto("Producto", 50.0, 5, categoria));
        mockMvc.perform(post("/api/carrito/items").header("Authorization", bearer(cliente))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + producto.getId() + ",\"cantidad\":2}"))
                .andExpect(status().isCreated());
        String body = mockMvc.perform(post("/api/ordenes/checkout").header("Authorization", bearer(cliente)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andReturn().getResponse().getContentAsString();
        String ordenId = body.replaceAll(".*\\\"id\\\":(\\d+).*", "$1");
        mockMvc.perform(patch("/api/ordenes/" + ordenId + "/estado").header("Authorization", bearer(cliente))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"PAGADA\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/ordenes/" + ordenId + "/cancelar").header("Authorization", bearer(cliente)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("CANCELADA"));
        org.junit.jupiter.api.Assertions.assertEquals(5, productoRepository.findById(producto.getId()).orElseThrow().getStock());
    }

    private String bearer(Usuario usuario) {
        return "Bearer " + tokenProvider.generateToken(usuario.getId(), usuario.getEmail(), usuario.getRole(), usuario.getTokenVersion());
    }
}

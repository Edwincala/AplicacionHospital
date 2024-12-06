package com.hospital.proyectoHospital;

import com.hospital.proyectoHospital.controllers.LoginRequest;
import com.hospital.proyectoHospital.controllers.TokenResponse;
import com.hospital.proyectoHospital.models.Usuario;
import com.hospital.proyectoHospital.repositories.UsuarioRepository;
import com.hospital.proyectoHospital.services.AuthService;
import com.hospital.proyectoHospital.services.JwtService;
import com.hospital.proyectoHospital.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    public void testRegisterUUserSuccess() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("test");
        usuario.setPassword("password_123");

        Mockito.when(usuarioService.existsByUsername("testuser")).thenReturn(false);
        Mockito.when(authService.registerUser(Mockito.any(Usuario.class))).thenReturn(usuario);
        Mockito.when(jwtService.generateToken(usuario)).thenReturn("mockAccessToken");
        Mockito.when(jwtService.generateRefreshToken(usuario)).thenReturn("mockRefreshToken");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mockAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("mockRefreshToken"));
    }

    @Test
    public void testRegisterUserConflict() throws Exception {
        Mockito.when(usuarioService.existsByUsername("testuser")).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"password123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        TokenResponse tokenResponse = new TokenResponse("mockAccessToken", "mockRefreshToken");

        Mockito.when(authService.login(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(tokenResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mockAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("mockRefreshToken"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        Mockito.when(authService.login(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Invalid credentials") {});

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRefreshTokenSuccess() throws Exception {
        TokenResponse tokenResponse = new TokenResponse("newAccessToken", "newRefreshToken");

        Mockito.when(authService.refreshToken("validRefreshToken")).thenReturn(tokenResponse);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refresh_token\": \"validRefreshToken\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("newRefreshToken"));
    }

    @Test
    public void testRefreshTokenFailure() throws Exception {
        Mockito.when(authService.refreshToken("invalidRefreshToken"))
                .thenThrow(new IllegalArgumentException("Invalid refresh token"));

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refresh_token\": \"invalidRefreshToken\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRefreshTokenMissing() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.refreshToken").value("Refresh token is missing or empty"));
    }

    @Test
    public void testGetCurrentUserSuccess() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setRol(Usuario.Rol.valueOf("ADMINISTRATIVO"));

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("testuser");
        Mockito.when(usuarioRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/auth/me")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.roles").value("ADMIN"));
    }

    @Test
    public void testGetCurrentUserNotFound() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("unknownUser");
        Mockito.when(usuarioRepository.findByUsername("unknownUser"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/auth/me")
                        .principal(authentication))
                .andExpect(status().isNotFound());
    }


}

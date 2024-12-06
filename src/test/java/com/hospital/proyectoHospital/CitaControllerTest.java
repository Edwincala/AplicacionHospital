package com.hospital.proyectoHospital;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.proyectoHospital.controllers.CitaController;
import com.hospital.proyectoHospital.models.Cita;
import com.hospital.proyectoHospital.services.CitaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CitaController.class)
public class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CitaService citaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAgendarCitaExito() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        UUID pacienteId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        LocalDateTime fechaHora = LocalDateTime.now();

        when(citaService.agendarCita(usuarioId, pacienteId, doctorId, fechaHora)).thenReturn(true);

        mockMvc.perform(post("/citas/agendar")
                        .param("usuarioId", usuarioId.toString())
                        .param("pacienteId", pacienteId.toString())
                        .param("doctorId", doctorId.toString())
                        .param("fechaHora", fechaHora.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Cita agendada exitosamente."));
    }

    @Test
    public void testAgendarCitaFallo() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        UUID pacienteId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        LocalDateTime fechaHora = LocalDateTime.now();

        when(citaService.agendarCita(usuarioId, pacienteId, doctorId, fechaHora)).thenReturn(false);

        mockMvc.perform(post("/citas/agendar")
                        .param("usuarioId", usuarioId.toString())
                        .param("pacienteId", pacienteId.toString())
                        .param("doctorId", doctorId.toString())
                        .param("fechaHora", fechaHora.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No se pudo agendar la cita. Verifica los datos ingresados o la disponibilidad del doctor."));
    }

    @Test
    public void testObtenerCitasPorPacienteConResultados() throws Exception {
        UUID pacienteId = UUID.randomUUID();
        Cita cita = new Cita();
        cita.setId(UUID.randomUUID());
        cita.setFechaHora(LocalDateTime.now());

        List<Cita> citas = Collections.singletonList(cita);

        when(citaService.obtenerCitasPorPaciente(pacienteId)).thenReturn(citas);

        mockMvc.perform(get("/citas/paciente/{pacienteId}", pacienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(cita.getId().toString()));
    }

    @Test
    public void testObtenerCitasPorPacienteSinResultados() throws Exception {
        UUID pacienteId = UUID.randomUUID();

        when(citaService.obtenerCitasPorPaciente(pacienteId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/citas/paciente/{pacienteId}", pacienteId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCancelarCitaExito() throws Exception {
        UUID citaId = UUID.randomUUID();

        when(citaService.cancelarCita(citaId)).thenReturn(true);

        mockMvc.perform(delete("/citas/cancelar/{citaId}", citaId))
                .andExpect(status().isOk())
                .andExpect(content().string("Cita cancelada exitosamente."));
    }

    @Test
    public void testCancelarCitaFallo() throws Exception {
        UUID citaId = UUID.randomUUID();

        when(citaService.cancelarCita(citaId)).thenReturn(false);

        mockMvc.perform(delete("/citas/cancelar/{citaId}", citaId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No se pudo cancelar la cita. Verifica si existe o su estado actual."));
    }
}

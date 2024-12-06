package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.models.HistoriaClinica;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ConsejoSaludService {

    @Autowired
    private OpenAiService openAiService;

    private final static Logger log = LoggerFactory.getLogger(ConsejoSaludService.class);

    public String generarConsejoDeSalud(HistoriaClinica historiaClinica) {
        try {
            String prompt = construirPrompt(historiaClinica);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Arrays.asList(
                            new ChatMessage("system", "Eres un asistente médico profesional que proporciona consejos de salud basados en historias clínicas."),
                            new ChatMessage("user", prompt)
                    ))
                    .maxTokens(500)
                    .temperature(0.7)
                    .build();

            ChatCompletionResult response = openAiService.createChatCompletion(request);

            if (!response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }

            return "No se pudo generar un consejo de salud en este momento.";

        } catch (Exception e) {
            log.error("Error al generar consejo de salud", e);
            return "Error al generar el consejo de salud. Por favor, intente más tarde.";
        }
    }

    private String construirPrompt(HistoriaClinica historiaClinica) {
        return String.format(
                "Basándome en la siguiente historia clínica, proporciona un consejo de salud personalizado y preventivo:" +
                        "\n\nDetalles de la historia clínica: %s" +
                        "\n\nPor favor, proporciona un consejo que sea:" +
                        "\n- Específico para la condición del paciente" +
                        "\n- Práctico y aplicable en la vida diaria" +
                        "\n- Enfocado en la prevención y el bienestar" +
                        "\n- Expresado en lenguaje claro y comprensible",
                historiaClinica.getDetalles()
        );
    }
}

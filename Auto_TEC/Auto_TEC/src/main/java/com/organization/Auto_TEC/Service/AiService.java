package com.organization.Auto_TEC.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.organization.Auto_TEC.DTO.AiResponse;
import com.organization.Auto_TEC.exception.AiServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    @Value("${OPENAI_API_KEY:}")
    private String openaiKey;

    @Value("${OPENAI_MODEL:gpt-3.5-turbo}")
    private String openaiModel;

    private final ObjectMapper mapper = new ObjectMapper();

    private final HttpClient httpClient;

    public AiService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public AiResponse ask(String prompt) {
        String trimmed = prompt == null ? "" : prompt.trim();
        if (trimmed.isEmpty()) {
            return new AiResponse("");
        }

        if (openaiKey == null || openaiKey.isBlank()) {
            return new AiResponse(localAnswer(trimmed));
        }

        try {
            String modelToUse = (openaiModel == null || openaiModel.isBlank()) ? "gpt-3.5-turbo" : openaiModel;
            String body = mapper.writeValueAsString(Map.of(
                    "model", modelToUse,
                    "messages", new Object[]{Map.of("role", "user", "content", trimmed)},
                    "max_tokens", 300
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .timeout(Duration.ofSeconds(6))
                    .header("Authorization", "Bearer " + openaiKey)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int code = response.statusCode();
            String sb = response.body();

            if (code >= 200 && code < 300) {
                Map<?, ?> resp = mapper.readValue(sb, Map.class);
                try {
                    var choices = (java.util.List<?>) resp.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Object first = choices.get(0);
                        if (first instanceof Map) {
                            Object message = ((Map<?, ?>) first).get("message");
                            if (message instanceof Map) {
                                Object content = ((Map<?, ?>) message).get("content");
                                if (content != null) return new AiResponse(content.toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Could not extract AI answer, falling back to local responder", e);
                }
                return new AiResponse(localAnswer(trimmed));
            } else {
                log.error("AI provider returned code {}: {}", code, sb);
                throw new AiServiceException("ai provider error", sb);
            }

        } catch (AiServiceException ase) {
            throw ase;
        } catch (Exception ex) {
            log.error("AI backend call failed", ex);
            return new AiResponse(localAnswer(trimmed), "ai backend failed");
        }
    }

    private String localAnswer(String q) {
        String lower = q.toLowerCase();
        if (lower.contains("financ") || lower.contains("cuota") || lower.contains("credito")) {
            String calc = tryCalculateCuota(lower);
            if (calc != null) return calc;
            return "Ofrecemos financiamientos desde 6.9% anual. ¿Quieres que calculemos una cuota estimada?";
        }
        if (lower.contains("hora") || lower.contains("horario") || lower.contains("abre")) return "Nuestro horario es Lun-Vie 9:00 - 19:00 y Sáb 10:00 - 14:00.";
        if (lower.contains("visita") || lower.contains("sede") || lower.contains("donde")) return "Puedes visitarnos en nuestra sede — escríbenos y coordinamos una visita.";
        if (lower.contains("hola") || lower.contains("buen")) return "¡Hola! ¿En qué puedo ayudarte hoy? Puedo ayudarte con cotizaciones, horarios o detalles de modelos.";
        return "Lo siento, no entiendo completamente. ¿Quieres que te conecte con un asesor humano?";
    }

    private String tryCalculateCuota(String lower) {
        try {
            java.util.regex.Pattern pNum = java.util.regex.Pattern.compile("([0-9]{3,}(?:[.,][0-9]{1,2})?)");
            java.util.regex.Matcher m = pNum.matcher(lower.replaceAll("\\s", ""));
            Double importe = null;
            if (m.find()) {
                String s = m.group(1).replace(',', '.');
                importe = Double.parseDouble(s);
            }
            java.util.regex.Pattern pMeses = java.util.regex.Pattern.compile("(\\d{1,2})\\s*(?:meses?|m)");
            java.util.regex.Matcher mm = pMeses.matcher(lower);
            Integer meses = null;
            if (mm.find()) {
                String g = mm.group(1);
                if (g != null) meses = Integer.parseInt(g);
            }
            if (importe != null && meses != null && meses > 0) {
                double anual = 6.9 / 100.0;
                double i = anual / 12.0;
                double n = meses;
                double cuota = (importe * i) / (1 - Math.pow(1 + i, -n));
                long rounded = Math.round(cuota);
                return String.format("Estimación: para %.0f en %d meses (TNA 6.9%%) la cuota aproximada es %d por mes.", importe, meses, rounded);
            }
        } catch (Exception e) {

        }
        return null;
    }
}

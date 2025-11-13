package com.organization.Auto_TEC.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.organization.Auto_TEC.DTO.AiResponse;
import com.organization.Auto_TEC.DTO.ErrorResponse;
import com.organization.Auto_TEC.DTO.PromptRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ai-chat")
public class AiChatController {

    private static final Logger log = LoggerFactory.getLogger(AiChatController.class);

    @Value("${OPENAI_API_KEY:}")
    private String openaiKey;

    @Value("${OPENAI_MODEL:gpt-3.5-turbo}")
    private String openaiModel;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<?> chat(@Valid @RequestBody PromptRequest request) {
        String prompt = request.getPrompt().trim();

        if (openaiKey == null || openaiKey.isBlank()) {
            return ResponseEntity.ok(new AiResponse(localAnswer(prompt)));
        }

        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3500);
            conn.setReadTimeout(6000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + openaiKey);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            String modelToUse = (openaiModel == null || openaiModel.isBlank()) ? "gpt-3.5-turbo" : openaiModel;
            String body = mapper.writeValueAsString(Map.of(
                    "model", modelToUse,
                    "messages", new Object[]{Map.of("role", "user", "content", prompt)},
                    "max_tokens", 300
            ));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');

            if (code >= 200 && code < 300) {
                Map<?, ?> resp = mapper.readValue(sb.toString(), Map.class);
                try {
                    var choices = (java.util.List<?>) resp.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Object first = choices.get(0);
                        if (first instanceof Map) {
                            Object message = ((Map<?, ?>) first).get("message");
                            if (message instanceof Map) {
                                Object content = ((Map<?, ?>) message).get("content");
                                if (content != null) return ResponseEntity.ok(new AiResponse(content.toString()));
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Could not extract AI answer, falling back to local responder", e);
                }
                return ResponseEntity.ok(new AiResponse(localAnswer(prompt)));
            } else {
                log.error("AI provider returned code {}: {}", code, sb.toString());
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorResponse("ai_provider_error", sb.toString()));
            }

        } catch (Exception ex) {
            log.error("AI backend call failed", ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new AiResponse(localAnswer(prompt), "ai backend failed"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        try {
            return ResponseEntity.ok(new AiResponse(localAnswer("hola")));
        } catch (Exception e) {
            log.warn("Health check failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorResponse("error", "health check failed"));
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

package com.example.api.trabajofinal.services;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmotionAIService {

    private final Map<String, String> emocionesPalabrasClave = Map.ofEntries(
            Map.entry("estres", "ESTRES"),
            Map.entry("estrés", "ESTRES"),
            Map.entry("ansiedad", "ANSIEDAD"),
            Map.entry("ansioso", "ANSIEDAD"),
            Map.entry("triste", "TRISTEZA"),
            Map.entry("tristeza", "TRISTEZA"),
            Map.entry("enojo", "ENOJO"),
            Map.entry("enojado", "ENOJO"),
            Map.entry("miedo", "MIEDO"),
            Map.entry("asustado", "MIEDO"),
            Map.entry("feliz", "FELICIDAD"),
            Map.entry("contento", "FELICIDAD"),
            Map.entry("calma", "CALMA"),
            Map.entry("tranquilo", "CALMA")
    );

    private final Set<String> palabrasCriticas = Set.of(
            "suicidio", "matar", "morir", "no quiero vivir", "acabar con todo","suicidar"
    );

    private final Map<String, List<String>> tecnicasPorEmocion = Map.ofEntries(
            Map.entry("ESTRES", Arrays.asList("Ejercicio de respiración 4-7-8", "Visualización guiada", "Relajación muscular progresiva")),
            Map.entry("ANSIEDAD", Arrays.asList("Técnica 5-4-3-2-1", "Ejercicio de grounding", "Meditación mindfulness")),
            Map.entry("TRISTEZA", Arrays.asList("Ejercicio de gratitud", "Actividad física suave", "Escritura expresiva")),
            Map.entry("ENOJO", Arrays.asList("Técnica del semáforo", "Respiración profunda", "Tiempo fuera")),
            Map.entry("MIEDO", Arrays.asList("Exposición gradual", "Diálogo interno positivo", "Técnica de la caja"))
    );

    public AnalisisEmocional analizarTexto(String texto) {
        String textoLower = texto.toLowerCase();

        for (String palabra : palabrasCriticas) {
            if (textoLower.contains(palabra)) {
                return new AnalisisEmocional("CRITICO", 90,
                        "Es importante que hables con un profesional de inmediato. Contacta a tu psicólogo.",
                        true, LocalDateTime.now());
            }
        }

        String emocionDetectada = "NEUTRAL";
        Integer confianza = 0;

        for (Map.Entry<String, String> entry : emocionesPalabrasClave.entrySet()) {
            if (textoLower.contains(entry.getKey())) {
                emocionDetectada = entry.getValue();
                confianza = 75;
                break;
            }
        }

        if (emocionDetectada.equals("NEUTRAL")) {
            emocionDetectada = analizarContexto(textoLower);
            confianza = 50;
        }

        return new AnalisisEmocional(emocionDetectada, confianza,
                generarRecomendacion(emocionDetectada),
                false, LocalDateTime.now());
    }

    private String analizarContexto(String texto) {
        if (texto.contains("no puedo") || texto.contains("difícil") || texto.contains("problema")) {
            return "ESTRES";
        } else if (texto.contains("preocupado") || texto.contains("nervioso") || texto.contains("qué pasará")) {
            return "ANSIEDAD";
        } else if (texto.contains("solo") || texto.contains("vacío") || texto.contains("llorar")) {
            return "TRISTEZA";
        }
        return "NEUTRAL";
    }

    private String generarRecomendacion(String emocion) {
        if (emocion.equals("NEUTRAL")) {
            return "Continúa con la sesión estándar. Mantén la comunicación abierta.";
        }

        List<String> tecnicas = tecnicasPorEmocion.getOrDefault(emocion,
                Arrays.asList("Respira profundamente", "Habla sobre lo que sientes"));

        Random random = new Random();
        return tecnicas.get(random.nextInt(tecnicas.size()));
    }

    public static class AnalisisEmocional {
        private String emocionDetectada;
        private Integer confianza;
        private String recomendacion;
        private boolean critico;
        private LocalDateTime timestamp;

        public AnalisisEmocional(String emocionDetectada, Integer confianza, String recomendacion, boolean critico, LocalDateTime timestamp) {
            this.emocionDetectada = emocionDetectada;
            this.confianza = confianza;
            this.recomendacion = recomendacion;
            this.critico = critico;
            this.timestamp = timestamp;
        }

        public String getEmocionDetectada() { return emocionDetectada; }
        public Integer getConfianza() { return confianza; }
        public String getRecomendacion() { return recomendacion; }
        public boolean isCritico() { return critico; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
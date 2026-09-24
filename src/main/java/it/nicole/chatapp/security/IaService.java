package it.nicole.chatapp.services;

import it.nicole.chatapp.entities.Messaggio;
import it.nicole.chatapp.entities.Utente;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class IaService {

    private static final String INDIRIZZO_GEMINI =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent";

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ChatService chatService;
    private final RestClient telefono = RestClient.create();

    public IaService(ChatService chatService) {
        this.chatService = chatService;
    }

    public String suggerisciMessaggio(Long chatId, Utente io) {
        // 1. prendo i messaggi (e controllo che io faccia parte della chat)
        List<Messaggio> messaggi = chatService.messaggiDellaChat(chatId, io);

        // 2. scrivo la conversazione su un "foglio", un messaggio per riga
        StringBuilder foglio = new StringBuilder();
        for (Messaggio m : messaggi) {
            foglio.append(m.getMittente().getUsername())
                    .append(": ")
                    .append(m.getTesto())
                    .append("\n");
        }

        String richiesta = "Questa è una conversazione in una chat. Io sono " + io.getUsername() + ".\n"
                + "Proponi UN solo messaggio breve che potrei inviare per continuare la conversazione. "
                + "Rispondi solo con il testo del messaggio, senza spiegazioni.\n\n"
                + foglio;

        // 3. preparo la richiesta nel formato che vuole Gemini
        Map<String, Object> corpo = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", richiesta)
                        ))
                )
        );

        try {
            // 4. telefono a Gemini
            Map<?, ?> risposta = telefono.post()
                    .uri(INDIRIZZO_GEMINI)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(corpo)
                    .retrieve()
                    .body(Map.class);

            // 5. apro le "scatole" della risposta fino ad arrivare al testo
            List<?> candidati = (List<?>) risposta.get("candidates");
            Map<?, ?> primoCandidato = (Map<?, ?>) candidati.get(0);
            Map<?, ?> contenuto = (Map<?, ?>) primoCandidato.get("content");
            List<?> parti = (List<?>) contenuto.get("parts");
            Map<?, ?> primaParte = (Map<?, ?>) parti.get(0);

            return (String) primaParte.get("text");

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "L'IA non è disponibile al momento: " + e.getMessage());
        }
    }
}
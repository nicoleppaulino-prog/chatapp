package it.nicole.chatapp.controllers;

import it.nicole.chatapp.entities.Chat;
import it.nicole.chatapp.entities.Messaggio;
import it.nicole.chatapp.entities.Utente;
import it.nicole.chatapp.services.ChatService;
import it.nicole.chatapp.services.IaService;
import it.nicole.chatapp.services.MessaggioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    // il modulo per inviare un messaggio: contiene solo il testo
    public record NuovoMessaggio(String testo) {
    }

    // la risposta con il suggerimento dell'IA
    public record Suggerimento(String testo) {
    }

    private final ChatService chatService;
    private final MessaggioService messaggioService;
    private final IaService iaService;

    public ChatController(ChatService chatService, MessaggioService messaggioService, IaService iaService) {
        this.chatService = chatService;
        this.messaggioService = messaggioService;
        this.iaService = iaService;
    }

    @PostMapping("/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    public Chat creaChat(@AuthenticationPrincipal Utente io, @PathVariable String username) {
        return chatService.creaChat(io, username);
    }

    @GetMapping
    public List<Chat> mieChat(@AuthenticationPrincipal Utente io) {
        return chatService.mieChat(io);
    }

    @GetMapping("/{chatId}/messaggi")
    public List<Messaggio> messaggi(@AuthenticationPrincipal Utente io, @PathVariable Long chatId) {
        return chatService.messaggiDellaChat(chatId, io);
    }

    @PostMapping("/{chatId}/messaggi")
    @ResponseStatus(HttpStatus.CREATED)
    public Messaggio invia(@AuthenticationPrincipal Utente io,
                           @PathVariable Long chatId,
                           @RequestBody NuovoMessaggio corpo) {
        return messaggioService.invia(chatId, io, corpo.testo());
    }

    @GetMapping("/{chatId}/suggerimento")
    public Suggerimento suggerimento(@AuthenticationPrincipal Utente io, @PathVariable Long chatId) {
        String testo = iaService.suggerisciMessaggio(chatId, io);
        return new Suggerimento(testo);
    }
}
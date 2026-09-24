package it.nicole.chatapp.services;

import it.nicole.chatapp.entities.Chat;
import it.nicole.chatapp.entities.Messaggio;
import it.nicole.chatapp.entities.Utente;
import it.nicole.chatapp.repositories.MessaggioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class MessaggioService {

    private final MessaggioRepository messaggioRepository;
    private final ChatService chatService;
    private final SimpMessagingTemplate radio;

    public MessaggioService(MessaggioRepository messaggioRepository,
                            ChatService chatService,
                            SimpMessagingTemplate radio) {
        this.messaggioRepository = messaggioRepository;
        this.chatService = chatService;
        this.radio = radio;
    }

    public Messaggio invia(Long chatId, Utente mittente, String testo) {
        if (testo == null || testo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il messaggio non può essere vuoto");
        }

        // 1. controllo che il mittente faccia parte della chat
        Chat chat = chatService.trovaChatDellUtente(chatId, mittente);

        // 2. preparo e archivio il messaggio
        Messaggio messaggio = new Messaggio();
        messaggio.setTesto(testo);
        messaggio.setDataInvio(LocalDateTime.now());
        messaggio.setMittente(mittente);
        messaggio.setChat(chat);
        Messaggio salvato = messaggioRepository.save(messaggio);

        // 3. lo trasmetto sul canale della chat
        radio.convertAndSend("/topic/chat/" + chatId, salvato);

        return salvato;
    }
}
package it.nicole.chatapp.services;

import it.nicole.chatapp.entities.Chat;
import it.nicole.chatapp.entities.Messaggio;
import it.nicole.chatapp.entities.Utente;
import it.nicole.chatapp.repositories.ChatRepository;
import it.nicole.chatapp.repositories.MessaggioRepository;
import it.nicole.chatapp.repositories.UtenteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UtenteRepository utenteRepository;
    private final MessaggioRepository messaggioRepository;

    public ChatService(ChatRepository chatRepository,
                       UtenteRepository utenteRepository,
                       MessaggioRepository messaggioRepository) {
        this.chatRepository = chatRepository;
        this.utenteRepository = utenteRepository;
        this.messaggioRepository = messaggioRepository;
    }

    // apre una nuova chat tra me e un altro utente
    public Chat creaChat(Utente io, String usernameAltro) {
        Utente altro = utenteRepository.findByUsername(usernameAltro)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        if (altro.getId().equals(io.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Non puoi aprire una chat con te stessa");
        }

        Chat chat = new Chat();
        chat.getPartecipanti().add(io);
        chat.getPartecipanti().add(altro);
        return chatRepository.save(chat);
    }

    // tutte le chat di cui faccio parte
    public List<Chat> mieChat(Utente io) {
        return chatRepository.findByPartecipantiId(io.getId());
    }

    // cerca una chat e controlla che io ne faccia parte
    public Chat trovaChatDellUtente(Long chatId, Utente io) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat non trovata"));

        for (Utente partecipante : chat.getPartecipanti()) {
            if (partecipante.getId().equals(io.getId())) {
                return chat;
            }
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non fai parte di questa chat");
    }

    // i messaggi di una chat, dal più vecchio al più nuovo
    public List<Messaggio> messaggiDellaChat(Long chatId, Utente io) {
        trovaChatDellUtente(chatId, io);
        return messaggioRepository.findByChatIdOrderByDataInvioAsc(chatId);
    }
}
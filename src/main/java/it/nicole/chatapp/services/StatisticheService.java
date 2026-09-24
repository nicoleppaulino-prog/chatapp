package it.nicole.chatapp.services;

import it.nicole.chatapp.entities.Utente;
import it.nicole.chatapp.repositories.ChatRepository;
import it.nicole.chatapp.repositories.MessaggioRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class StatisticheService {

    private final MessaggioRepository messaggioRepository;
    private final ChatRepository chatRepository;
    private final JavaMailSender postino;
    private final TemplateEngine thymeleaf;

    public StatisticheService(MessaggioRepository messaggioRepository,
                              ChatRepository chatRepository,
                              JavaMailSender postino,
                              TemplateEngine thymeleaf) {
        this.messaggioRepository = messaggioRepository;
        this.chatRepository = chatRepository;
        this.postino = postino;
        this.thymeleaf = thymeleaf;
    }

    public void inviaStatistiche(Utente io) {
        long inviati = messaggioRepository.countByMittenteId(io.getId());
        long ricevuti = messaggioRepository.countByChatPartecipantiIdAndMittenteIdNot(io.getId(), io.getId());
        long chatAperte = chatRepository.countByPartecipantiId(io.getId());

        Context contesto = new Context();
        contesto.setVariable("username", io.getUsername());
        contesto.setVariable("inviati", inviati);
        contesto.setVariable("ricevuti", ricevuti);
        contesto.setVariable("chatAperte", chatAperte);
        String html = thymeleaf.process("statistiche", contesto);

        try {
            MimeMessage email = postino.createMimeMessage();
            MimeMessageHelper busta = new MimeMessageHelper(email, "UTF-8");
            busta.setFrom("chatapp@test.it");
            busta.setTo(io.getEmail());
            busta.setSubject("Le tue statistiche su Chatapp");
            busta.setText(html, true);
            postino.send(email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Invio email non riuscito: " + e.getMessage());
        }
    }
}
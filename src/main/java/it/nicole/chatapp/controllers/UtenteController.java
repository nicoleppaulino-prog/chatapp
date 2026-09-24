package it.nicole.chatapp.controllers;

import it.nicole.chatapp.entities.Utente;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/utenti")
public class UtenteController {

    @GetMapping("/me")
    public String chiSonoIo(@AuthenticationPrincipal Utente utente) {
        return "Ciao " + utente.getUsername() + ", sei loggata con id " + utente.getId();
    }
}
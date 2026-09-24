package it.nicole.chatapp.controllers;

import it.nicole.chatapp.entities.Utente;
import it.nicole.chatapp.services.StatisticheService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/utenti")
public class UtenteController {

    private final StatisticheService statisticheService;

    public UtenteController(StatisticheService statisticheService) {
        this.statisticheService = statisticheService;
    }

    @GetMapping("/me")
    public String chiSonoIo(@AuthenticationPrincipal Utente utente) {
        return "Ciao " + utente.getUsername() + ", sei loggata con id " + utente.getId();
    }

    @PostMapping("/me/statistiche")
    public String inviaStatistiche(@AuthenticationPrincipal Utente utente) {
        statisticheService.inviaStatistiche(utente);
        return "Statistiche inviate a " + utente.getEmail();
    }
}
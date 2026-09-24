package it.nicole.chatapp.controllers;

import it.nicole.chatapp.dto.LoginDTO;
import it.nicole.chatapp.dto.RegistrazioneDTO;
import it.nicole.chatapp.dto.TokenDTO;
import it.nicole.chatapp.entities.Utente;
import it.nicole.chatapp.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String registra(@RequestBody @Valid RegistrazioneDTO dati) {
        Utente nuovo = authService.registra(dati);
        return "Utente registrato con id " + nuovo.getId();
    }

    @PostMapping("/login")
    public TokenDTO login(@RequestBody @Valid LoginDTO dati) {
        String token = authService.login(dati);
        return new TokenDTO(token);
    }
}
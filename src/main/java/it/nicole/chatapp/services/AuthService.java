package it.nicole.chatapp.services;

import it.nicole.chatapp.dto.LoginDTO;
import it.nicole.chatapp.dto.RegistrazioneDTO;
import it.nicole.chatapp.entities.Utente;
import it.nicole.chatapp.repositories.UtenteRepository;
import it.nicole.chatapp.security.JwtTools;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTools jwtTools;

    // Spring passa da solo gli attrezzi che servono all'impiegato
    public AuthService(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder, JwtTools jwtTools) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTools = jwtTools;
    }

    public Utente registra(RegistrazioneDTO dati) {
        if (utenteRepository.findByEmail(dati.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email già registrata");
        }
        if (utenteRepository.existsByUsername(dati.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username già in uso");
        }

        Utente nuovo = new Utente();
        nuovo.setUsername(dati.username());
        nuovo.setEmail(dati.email());
        // la password viene "tritata" prima di essere salvata
        nuovo.setPassword(passwordEncoder.encode(dati.password()));

        return utenteRepository.save(nuovo);
    }

    public String login(LoginDTO dati) {
        Utente utente = utenteRepository.findByEmail(dati.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide"));

        // confronta la password inserita con quella tritata nel database
        if (!passwordEncoder.matches(dati.password(), utente.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide");
        }

        return jwtTools.creaToken(utente);
    }
}
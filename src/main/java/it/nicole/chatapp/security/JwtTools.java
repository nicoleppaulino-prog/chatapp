package it.nicole.chatapp.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.nicole.chatapp.entities.Utente;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTools {

    // legge la frase segreta da application.properties
    @Value("${jwt.secret}")
    private String secret;

    // trasforma la frase segreta nel "timbro" usato per firmare
    private SecretKey getChiave() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // stampa il braccialetto per un utente, valido 7 giorni
    public String creaToken(Utente utente) {
        return Jwts.builder()
                .subject(String.valueOf(utente.getId()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(getChiave())
                .compact();
    }

    // legge il braccialetto: se il timbro è falso o è scaduto lancia un errore,
    // altrimenti restituisce l'id dell'utente scritto sopra
    public Long leggiIdUtente(String token) {
        String id = Jwts.parser()
                .verifyWith(getChiave())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.parseLong(id);
    }
}
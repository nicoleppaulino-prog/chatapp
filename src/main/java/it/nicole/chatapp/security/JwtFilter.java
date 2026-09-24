package it.nicole.chatapp.security;

import it.nicole.chatapp.entities.Utente;
import it.nicole.chatapp.repositories.UtenteRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTools jwtTools;
    private final UtenteRepository utenteRepository;

    public JwtFilter(JwtTools jwtTools, UtenteRepository utenteRepository) {
        this.jwtTools = jwtTools;
        this.utenteRepository = utenteRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // il braccialetto arriva nell'intestazione "Authorization: Bearer <token>"
        String intestazione = request.getHeader("Authorization");

        if (intestazione != null && intestazione.startsWith("Bearer ")) {
            String token = intestazione.substring(7); // tolgo "Bearer " (7 caratteri)
            try {
                Long idUtente = jwtTools.leggiIdUtente(token);
                Utente utente = utenteRepository.findById(idUtente).orElse(null);

                if (utente != null) {
                    // diciamo a Spring: "questa persona è riconosciuta, ed è questo utente"
                    UsernamePasswordAuthenticationToken riconoscimento =
                            new UsernamePasswordAuthenticationToken(utente, null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(riconoscimento);
                }
            } catch (Exception e) {
                // braccialetto falso o scaduto: non riconosciamo nessuno
            }
        }

        // in ogni caso la richiesta prosegue: sarà il regolamento a decidere se può entrare
        filterChain.doFilter(request, response);
    }
}
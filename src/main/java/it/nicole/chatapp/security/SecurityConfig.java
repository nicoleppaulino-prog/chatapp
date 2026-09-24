package it.nicole.chatapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                // niente pagina di login di Spring: il login lo facciamo noi con il JWT
                .formLogin(AbstractHttpConfigurer::disable)
                // protezione pensata per i siti con form HTML, a noi non serve
                .csrf(AbstractHttpConfigurer::disable)
                // il server non si ricorda di nessuno: ogni richiesta deve portare il braccialetto
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // il regolamento: chi può entrare dove
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/error", "/ws/**", "/test-chat.html").permitAll()
                        .anyRequest().authenticated()
                );

        // il buttafuori controlla il braccialetto prima dei controlli standard di Spring
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // il "tritacarne" per le password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }
}
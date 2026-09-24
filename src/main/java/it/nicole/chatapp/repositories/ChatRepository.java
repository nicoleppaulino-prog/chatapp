package it.nicole.chatapp.repositories;

import it.nicole.chatapp.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByPartecipantiId(Long idUtente);

    // quante chat ho aperte
    long countByPartecipantiId(Long idUtente);
}
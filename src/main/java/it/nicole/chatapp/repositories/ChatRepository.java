package it.nicole.chatapp.repositories;

import it.nicole.chatapp.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
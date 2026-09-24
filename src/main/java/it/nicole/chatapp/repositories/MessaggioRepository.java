package it.nicole.chatapp.repositories;

import it.nicole.chatapp.entities.Messaggio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessaggioRepository extends JpaRepository<Messaggio, Long> {

    List<Messaggio> findByChatIdOrderByDataInvioAsc(Long chatId);
}
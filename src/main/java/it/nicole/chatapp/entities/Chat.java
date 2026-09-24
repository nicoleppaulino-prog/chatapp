package it.nicole.chatapp.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // registro presenze: quali utenti fanno parte di questa chat
    @ManyToMany
    @JoinTable(
            name = "chat_partecipanti",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "utente_id")
    )
    private List<Utente> partecipanti = new ArrayList<>();

    public Chat() {
    }

    public Long getId() {
        return id;
    }

    public List<Utente> getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(List<Utente> partecipanti) {
        this.partecipanti = partecipanti;
    }
}
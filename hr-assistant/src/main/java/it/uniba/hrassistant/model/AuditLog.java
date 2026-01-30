package it.uniba.hrassistant.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs") // Specifichiamo il nome esatto della tabella nel DB.
@Data // Genera automaticamente Getters, Setters, toString(), equals() e hashCode().
@NoArgsConstructor // Genera il costruttore vuoto
public class AuditLog {

    @Id // Chiave Primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // Utente che ha interagito con il sistema

    @Column(nullable = false)
    private LocalDateTime timestamp; // Quando è successo

    @Column(length = 1000)
    private String domanda; // Prompt

    private String status; // Stato

    // Costruttore personalizzato
    public AuditLog(String username, String domanda, String status) {
        this.username = username;
        this.domanda = domanda;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
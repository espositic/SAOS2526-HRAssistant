package it.uniba.hrassistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity JPA che rappresenta un record di Audit nel database.
 * <p>
 * Questa classe mappa la tabella 'audit_logs' e serve per storicizzare
 * ogni interazione avvenuta con il chatbot, inclusi:
 * <ul>
 * <li>Chi ha fatto la richiesta (Username)</li>
 * <li>Cosa ha chiesto (Domanda)</li>
 * <li>L'esito dell'operazione (SUCCESS, BLOCKED, ERROR)</li>
 * <li>Il timestamp preciso dell'evento</li>
 * </ul>
 * Utile per compliance, sicurezza e analisi dell'utilizzo.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 1000) // Limitiamo la lunghezza per il DB
    private String domanda;

    private String status; // ES: "SUCCESS", "BLOCKED", "ERROR"

    // Costruttore vuoto necessario per JPA
    public AuditLog() {}

    public AuditLog(String username, String domanda, String status) {
        this.username = username;
        this.domanda = domanda;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    // Getter e Setter omessi per brevità (puoi usare Lombok @Data se preferisci)
}
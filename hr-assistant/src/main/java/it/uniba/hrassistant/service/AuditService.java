package it.uniba.hrassistant.service;

import it.uniba.hrassistant.model.AuditLog;
import it.uniba.hrassistant.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service // Indica a Spring che questa classe contiene la "Business Logic".
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    // Dependency Injection tramite costruttore
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Recupera i log in modo PAGINATO.
     * Invece di scaricare 10.000 righe tutte insieme (che bloccherebbero il server),
     * scarica solo la pagina richiesta (es. pagina 1, 20 elementi).
     *
     * @param pageable Oggetto di Spring che contiene info su: pagina richiesta, grandezza pagina e ordinamento.
     * @return Una Page<AuditLog> che contiene i dati + info extra (es. "totale pagine: 50").
     */
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        // Delega la chiamata al repository che supporta nativamente la paginazione
        return auditLogRepository.findAll(pageable);
    }
}
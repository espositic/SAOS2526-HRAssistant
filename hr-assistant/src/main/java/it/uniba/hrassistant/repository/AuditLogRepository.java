package it.uniba.hrassistant.repository;

import it.uniba.hrassistant.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository per la gestione dei Log.
 * Funge da intermediario tra il codice Java e il Database.
 */
@Repository // Indica a Spring che questo componente gestisce l'accesso ai dati
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
package it.uniba.hrassistant.repository;

import it.uniba.hrassistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interfaccia di accesso ai dati per l'entità {@link User}.
 * Spring Data JPA genera automaticamente l'implementazione SQL al runtime.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Cerca un utente tramite email. Usato principalmente durante il login.
     *
     * @param email L'email da cercare.
     * @return Un Optional contenente l'utente se trovato.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica l'esistenza di un'email. Usato per prevenire duplicati in fase di registrazione.
     *
     * @param email L'email da verificare.
     * @return true se l'email esiste già, false altrimenti.
     */
    boolean existsByEmail(String email);
}
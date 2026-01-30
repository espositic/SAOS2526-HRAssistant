package it.uniba.hrassistant.repository;

import it.uniba.hrassistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository per la gestione degli Utenti.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * METODO DERIVATO (Query Method).
     * Spring legge "findByEmail" e genera automaticamente la query:
     * SELECT * FROM app_users WHERE email = ?
     * * Restituisce un Optional per evitare NullPointerException: 
     * se l'utente non c'è, il contenitore è vuoto, ma non nullo.
     */
    Optional<User> findByEmail(String email);

    /**
     * Altro METODO DERIVATO.
     * Spring legge "existsByEmail" e genera una query ottimizzata:
     * SELECT COUNT(*) FROM app_users WHERE email = ?
     * * È molto più veloce di fare una "find" perché il DB risponde solo "sì/no"
     * senza dover estrarre tutti i dati dell'utente.
     */
    boolean existsByEmail(String email);
}
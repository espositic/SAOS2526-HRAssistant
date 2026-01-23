package it.uniba.hrassistant.repository;

import it.uniba.hrassistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Metodo necessario per l'autenticazione tramite email
    Optional<User> findByEmail(String email);

    // Check utile per la fase di registrazione per evitare duplicati
    boolean existsByEmail(String email);
}
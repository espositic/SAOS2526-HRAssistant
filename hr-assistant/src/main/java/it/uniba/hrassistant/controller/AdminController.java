package it.uniba.hrassistant.controller;

import it.uniba.hrassistant.model.AuditLog;
import it.uniba.hrassistant.model.Role;
import it.uniba.hrassistant.model.User;
import it.uniba.hrassistant.repository.AuditLogRepository;
import it.uniba.hrassistant.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort; // Importante per l'ordinamento
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository; // Accesso alla tabella Utenti
    private final AuditLogRepository auditLogRepository; // Accesso alla tabella dei Log
    private final PasswordEncoder passwordEncoder; // Strumento per criptare le password

    /**
     * Endpoint per creare nuovi utenti.
     * Metodo: POST
     * URL: /admin/users/create
     */
    @PostMapping("/users/create")
    @PreAuthorize("hasRole('HR_ADMIN')") // Solo chi ha il ruolo HR_ADMIN può chiamare questo metodo.
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {

        // 1. L'email esiste già?
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Errore: L'email è già in uso.");
        }

        // 2. Costruzione dell'utente
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole()) // Assegniamo il ruolo scelto dall'admin
                .build();

        // 3. Salvataggio nel Database
        userRepository.save(user);
        return ResponseEntity.ok("Utente creato con successo: " + user.getEmail());
    }

    /**
     * Endpoint per vedere i LOG di sistema.
     * Serve all'admin per controllare "chi ha fatto cosa".
     * Metodo: GET
     * URL: /admin/logs
     */
    @GetMapping("/logs")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        // Recupera tutti i log ordinati per data decrescente
        List<AuditLog> logs = auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        return ResponseEntity.ok(logs);
    }
}

// DTO
// È una classe "usa e getta" che serve solo a trasportare i dati dal Frontend al Backend.
@Data
class CreateUserRequest {
    private String email;
    private String password;
    private Role role;
}
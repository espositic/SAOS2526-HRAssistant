package it.uniba.hrassistant.controller;

import it.uniba.hrassistant.config.JwtUtil;
import it.uniba.hrassistant.model.User;
import it.uniba.hrassistant.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository; // Serve per recuperare il ruolo esatto
    private final JwtUtil jwtUtil;

    /**
     * Endpoint di Login.
     * Riceve Email e Password, restituisce il Token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // 1. Autenticazione
        // L'AuthenticationManager controlla se email e password corrispondono.
        // Se la password è sbagliata, lancia un'eccezione e il codice si ferma qui (ritornando 403 Forbidden).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Recuperiamo l'utente dal DB
        // Se siamo arrivati qui, la password è corretta.
        // Ora recuperiamo l'utente intero dal Database perché ci serve il suo RUOLO.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Errore inatteso: utente non trovato dopo auth."));

        // 3. Estraiamo il ruolo come stringa (es. "HR_ADMIN" o "USER")
        String role = user.getRole().name();

        // 4. Generiamo il token includendo il ruolo
        String jwtToken = jwtUtil.generateToken(user.getEmail(), role);

        // 5. Restituiamo il token al client (Frontend) in formato JSON.
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }
}

// DTO per ricevere i dati
@Data
class AuthRequest {
    private String email;
    private String password;
}

// DTO per inviare i dati
@Data
@lombok.AllArgsConstructor
class AuthResponse {
    private String token;
}
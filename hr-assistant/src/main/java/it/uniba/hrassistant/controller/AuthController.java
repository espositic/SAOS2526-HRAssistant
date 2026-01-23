package it.uniba.hrassistant.controller;

import it.uniba.hrassistant.config.JwtUtil;
import it.uniba.hrassistant.model.Role;
import it.uniba.hrassistant.model.User;
import it.uniba.hrassistant.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST per la gestione dell'autenticazione.
 * Espone endpoint pubblici per registrazione e login.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Endpoint per la registrazione di nuovi utenti.
     * Crea un nuovo utente con ruolo USER e password hashata.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {

        // Controllo preventivo duplicati
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new AuthResponse("Email already in use"));
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Default a USER
                .build();

        userRepository.save(user);
        var jwtToken = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    /**
     * Endpoint per il login.
     * Autentica le credenziali e restituisce un token JWT valido.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }
}

// DTOs (Data Transfer Objects) per gestire i payload JSON
@Data
class RegisterRequest {
    private String email;
    private String password;
}

@Data
class AuthRequest {
    private String email;
    private String password;
}

@Data
@lombok.AllArgsConstructor
class AuthResponse {
    private String token;
}
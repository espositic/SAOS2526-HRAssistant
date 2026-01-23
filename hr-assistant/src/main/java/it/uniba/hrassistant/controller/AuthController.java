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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Default a USER
                .build();

        userRepository.save(user);
        var jwtToken = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

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

// DTOs interni per brevità (idealmente in package dto)
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
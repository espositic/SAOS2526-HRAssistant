package it.uniba.hrassistant.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Legge la chiave segreta che serve a firmare il token
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // --- ESTRAZIONE DATI ---

    /**
     * Estrae lo username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Estrae il ruolo dal claim personalizzato "role"
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Metodo generico per estrarre una qualsiasi informazione
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Decodifica il token usando la chiave segreta.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // --- GENERAZIONE ---

    /**
     * Crea un nuovo token per un utente appena loggato.
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Inseriamo il ruolo nel payload
        return buildToken(claims, username);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject) {
        return Jwts.builder()
                .setClaims(extraClaims) // Aggiunge i dati extra (ruolo)
                .setSubject(subject)    // Imposta il proprietario
                .setIssuedAt(new Date(System.currentTimeMillis())) // Data creazione
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Data scadenza
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Firma crittografica
                .compact(); // Converte tutto in una stringa "base64"
    }

    // --- VALIDAZIONE ---

    /**
     * Controlla se il token appartiene all'utente e se non è scaduto
     */
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Decodifica la chiave segreta da Base64
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
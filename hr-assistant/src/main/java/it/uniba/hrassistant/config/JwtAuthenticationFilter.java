package it.uniba.hrassistant.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // Questo metodo viene eseguito per OGNI singola richiesta HTTP (GET, POST, ecc.)
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        String extractedRole; // Usiamo una variabile temporanea

        // 1. FAST EXIT
        // Se la richiesta non ha l'header "Authorization" o non inizia con Bearer,
        // non è una richiesta di login. Lasciamo passare la richiesta così com'è.
        // (Sarà poi la SecurityConfig a bloccarla se la rotta era protetta).
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Togliamo il prefisso Bearer per avere solo il token puro.
        jwt = authHeader.substring(7);

        // 2. EXTRACTION: Proviamo a leggere il token
        try {
            userEmail = jwtUtil.extractUsername(jwt);
            extractedRole = jwtUtil.extractRole(jwt); // Estraiamo il ruolo direttamente dal Token
        } catch (Exception e) {
            // Se il token è scaduto o manomesso, ignoriamo l'errore e non autentichiamo l'utente.
            filterChain.doFilter(request, response);
            return;
        }

        // 3. AUTHENTICATION
        // Se abbiamo trovato l'email e l'utente non è già autenticato nel contesto attuale
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Validiamo il token
            if (jwtUtil.isTokenValid(jwt, userEmail)) {

                // Se il ruolo non è presente nel token, assegniamo un default per evitare crash
                String finalRole = (extractedRole != null) ? extractedRole : "USER";

                // Aggiungiamo prefisso ROLE_ se manca
                String authorityName = finalRole.startsWith("ROLE_") ? finalRole : "ROLE_" + finalRole;

                // Creiamo l'authority
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityName);

                // Creiamo il token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail,
                        null,
                        List.of(authority)
                );

                // Aggiungiamo dettagli tecnici della richiesta
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Da ora in poi, in tutta l'app, l'utente risulta loggato.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // Passiamo la palla al prossimo filtro della catena
        filterChain.doFilter(request, response);
    }
}
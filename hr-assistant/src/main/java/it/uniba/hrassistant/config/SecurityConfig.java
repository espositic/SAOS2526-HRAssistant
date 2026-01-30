package it.uniba.hrassistant.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Abilita le annotazioni @PreAuthorize("hasRole('ADMIN')") nei Controller
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Filtro custom
    private final AuthenticationProvider authenticationProvider; // La logica di login definita in ApplicationConfig

    /**
     * SecurityFilterChain: Definisce la catena di filtri di sicurezza.
     * Ogni richiesta HTTP passa attraverso questa configurazione.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http    
                // Disabilitiamo CSRF perché usiamo JWT
                .csrf(AbstractHttpConfigurer::disable)
                // Abilitiamo CORS
                .cors(Customizer.withDefaults())
                // Impostiamo la gestione della sessione su STATELESS
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Colleghiamo il provider che sa come verificare password e utenti
                .authenticationProvider(authenticationProvider)
                // Definiamo quali URL sono pubblici e quali privati
                .authorizeHttpRequests(auth -> auth
                        // 1. Permettiamo le richieste OPTIONS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        
                        // 2. Endpoint Pubblici
                        .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()

                        // 3. Tutto il resto richiede autenticazione
                        // (I ruoli specifici sono gestiti dai Controller con @PreAuthorize)
                        .anyRequest().authenticated()
                )
                // Inseriamo il nostro filtro JWT custom prima del filtro standard di Spring.
                // Se il token è valido, Spring troverà l'utente già autenticato quando arriva al suo filtro standard
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configurazione CORS (Cross-Origin Resource Sharing).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Autorizziamo solo il frontend
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        // Autorizziamo i metodi HTTP standard
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Autorizziamo tutti gli header
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
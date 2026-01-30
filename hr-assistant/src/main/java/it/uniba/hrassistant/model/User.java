package it.uniba.hrassistant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entità User.
 * Rappresenta la tabella nel database, ma implementa anche l'interfaccia UserDetails.
 * Questo permette a Spring Security di usare direttamente questa classe per i controlli di sicurezza.
 */
@Data // Genera Getter, Setter, toString, ecc.
@Builder
@NoArgsConstructor // Costruttore vuoto
@AllArgsConstructor // Costruttore con tutti i campi
@Entity // È una tabella del DB
@Table(name = "app_users") // "user" è una parola riservata in SQL (PostgreSQL). La rinominiamo.
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(unique = true, nullable = false) // L'email deve essere univoca
    private String email;

    /**
     * Password cifrata con BCrypt. Non deve mai essere salvata in chiaro.
     */
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Salva nel DB la stringa "HR_ADMIN" invece di un numero (0 o 1)
    private Role role;

    /**
     * Mappa il ruolo dell'utente in una {@link GrantedAuthority} di Spring Security.
     * Viene aggiunto il prefisso "ROLE_" standard.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // Spring Security usa username come identificativo generico.
    // Noi gli passiamo l'email.
    @Override
    public String getUsername() {
        return email;
    }

    // Flag per la gestione dello stato dell'account.
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
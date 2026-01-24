package it.uniba.hrassistant.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Servizio responsabile della gestione delle policy di Rate Limiting distribuito su Redis.
 * <p>
 * Implementa il pattern "Token Bucket":
 * <ul>
 * <li>Ogni utente ha un "secchiello" virtuale identificato dal suo username.</li>
 * <li>Il secchiello contiene massimo 10 gettoni.</li>
 * <li>Ogni minuto vengono rigenerati 10 gettoni.</li>
 * <li>Ogni richiesta consuma 1 gettone.</li>
 * </ul>
 * Se i gettoni finiscono, le richieste vengono rifiutate finché non avviene la ricarica.
 */
@Service
public class RateLimiterService {

    private final ProxyManager<String> proxyManager;

    @Autowired
    public RateLimiterService(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    /**
     * Verifica se l'utente ha diritto ad accedere alla risorsa.
     * <p>
     * Questo metodo contatta Redis, recupera il bucket associato all'utente
     * e tenta di sottrarre 1 gettone.
     *
     * @param username L'identificativo univoco dell'utente (usato per generare la chiave Redis).
     * @return {@code true} se l'accesso è consentito (il gettone è stato consumato),
     * {@code false} se l'accesso è negato (esaurimento gettoni).
     */
    public boolean tryAccess(String username) {
        // Definiamo la chiave univoca per Redis (es. "rate_limit:mario.rossi")
        String key = "rate_limit:" + username;

        // Configurazione della regola (Policy): 10 token ogni 1 minuto
        Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.simple(10, Duration.ofMinutes(1)))
                .build();

        // Il proxyManager cerca il bucket su Redis. Se non esiste, lo crea al volo usando la configSupplier.
        Bucket bucket = proxyManager.builder().build(key, configSupplier);

        // Proviamo a consumare 1 token.
        return bucket.tryConsume(1);
    }
}
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
 * Servizio che implementa la logica del Rate Limiting (Limitazione della frequenza).
 * Usa il database Redis per memorizzare i contatori, garantendo che il limite funzioni
 * anche se l'applicazione viene eseguita su più server contemporaneamente.
 */
@Service
public class RateLimiterService {

    // Il ProxyManager è l'oggetto (configurato in RateLimitConfig) che sa parlare con Redis.
    private final ProxyManager<String> proxyManager;

    @Autowired
    public RateLimiterService(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    /**
     * Metodo booleano: SÌ (puoi passare) o NO (sei bloccato).
     * Viene chiamato dal ChatService prima di invocare l'AI.
     */
    public boolean tryAccess(String username) {
        // 1. Chiave Univoca: Creiamo una chiave specifica per questo utente.
        // Su Redis vedremo chiavi tipo: "rate_limit:mario.rossi@email.it"
        String key = "rate_limit:" + username;

        // 2. Definizione della Regola:
        // Usiamo una Supplier (funzione lambda) perché la configurazione serve solo 
        // se il bucket non esiste ancora su Redis e deve essere creato da zero.
        Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
                // Concediamo 10 gettoni (richieste) in una finestra di 1 minuto.
                .addLimit(Bandwidth.simple(10, Duration.ofMinutes(1)))
                .build();

        // 3. Recupero/Creazione del Bucket:
        // Chiediamo al ProxyManager: "Dammi il secchiello associato a questa chiave".
        // Se non c'è, crealo usando la regola sopra.
        Bucket bucket = proxyManager.builder().build(key, configSupplier);

        // 4. Tentativo di Consumo:
        // Proviamo a togliere 1 gettone dal secchiello.
        // - Se ci sono gettoni: restituisce TRUE e decrementa il contatore su Redis.
        // - Se il secchiello è vuoto: restituisce FALSE l'utente deve aspettare.
        return bucket.tryConsume(1);
    }
}
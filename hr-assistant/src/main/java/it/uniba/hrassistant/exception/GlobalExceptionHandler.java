package it.uniba.hrassistant.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

/**
 * Gestore globale delle eccezioni REST.
 * <p>
 * Intercetta le eccezioni lanciate dai controller o dai service e le trasforma
 * in risposte JSON pulite e standardizzate per il client.
 * Particolarmente utile per gestire il Rate Limiting (429) senza esporre stacktrace.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestisce le eccezioni di tipo ResponseStatusException (es. 429 Too Many Requests).
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "error", "RATE_LIMIT_EXCEEDED",
                        "message", ex.getReason(),
                        "status", String.valueOf(ex.getStatusCode().value())
                ));
    }
}
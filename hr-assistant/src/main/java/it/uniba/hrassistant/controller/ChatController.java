package it.uniba.hrassistant.controller;

import it.uniba.hrassistant.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    // Stiamo collegando il "ChatService", che è il cervello che sa come generare le risposte.
    private ChatService chatService;

    /**
     * Endpoint per inviare una domanda al bot.
     * Metodo: POST
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> askBot(@RequestBody Map<String, String> payload) {
        // 1. Estrazione: Prendiamo la domanda dal JSON in arrivo
        String question = payload.get("question");

        // 2. Validazione: Se l'utente manda una stringa vuota o nulla,
        // blocchiamo subito per evitare errori nel service.
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La domanda non può essere vuota"));
        }

        // 3. Elaborazione: Passiamo la palla al Service.
        String response = chatService.getAnswer(question);

        // 4. Risposta: Impacchettiamo la stringa di risposta in un JSON
        // e restituiamo codice 200 OK.
        return ResponseEntity.ok(Map.of("answer", response));
    }
}
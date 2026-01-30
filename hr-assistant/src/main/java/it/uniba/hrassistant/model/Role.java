package it.uniba.hrassistant.model;

/**
 * Definisce i ruoli disponibili nel sistema per il controllo degli accessi (RBAC).
 * USER: Utente base.
 * HR_ADMIN: Amministratore delle risorse umane con permessi elevati.
 */
public enum Role {
    // Il dipendente standard.
    // Può chattare col bot.
    USER,

    // L'amministratore HR (Risorse Umane).
    // Ha accesso agli endpoint protetti (es. /admin/users/create, /admin/logs).
    // Corrisponde al controllo @PreAuthorize("hasRole('HR_ADMIN')") nei Controller.
    HR_ADMIN
}
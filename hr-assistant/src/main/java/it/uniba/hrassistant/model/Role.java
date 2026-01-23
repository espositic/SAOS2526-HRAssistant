package it.uniba.hrassistant.model;

/**
 * Definisce i ruoli disponibili nel sistema per il controllo degli accessi (RBAC).
 * USER: Utente base (dipendente).
 * HR_ADMIN: Amministratore delle risorse umane con permessi elevati.
 */
public enum Role {
    USER,
    HR_ADMIN
}
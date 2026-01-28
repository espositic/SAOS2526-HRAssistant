# Secure HR Virtual Assistant
**Secure HR Virtual Assistant** è una piattaforma backend sicura sviluppata per la gestione automatizzata delle risorse umane, progettata con un focus specifico sulla sicurezza architetturale, l'isolamento dei servizi e l'integrazione di Intelligenza Artificiale locale (Ollama/Llama 3) per garantire la privacy dei dati sensibili.

Progetto realizzato per l'esame di Sicurezza delle Architetture Orientate ai Servizi dell'Università degli Studi di Bari.

# Indice
- [Secure HR Virtual Assistant](#secure-hr-virtual-assistant)
  - [Scenario](#scenario)
  - [Stack Tecnologico](#stack-tecnologico)
    - [Infrastruttura e Librerie](#infrastruttura-e-librerie)
- [Guida alla replicazione](#guida-alla-replicazione)
  - [Prerequisiti](#prerequisiti)
  - [Configurazione Ambiente e Segreti (.env)](#configurazione-ambiente-e-segreti-env)
  - [Generazione Certificati SSL (HTTPS)](#generazione-certificati-ssl-https)
  - [Avvio Infrastruttura Docker](#avvio-infrastruttura-docker)
  - [Installazione Modello AI](#installazione-modello-ai)
- [Guida all'avvio del Backend](#guida-allavvio-del-backend)
- [Avvio Frontend (React)](#avvio-frontend-react)
- [Approcci di sicurezza adottati](#approcci-di-sicurezza-adottati)
  - [Isolamento di Rete (Network Segregation)](#isolamento-di-rete-network-segregation)
  - [Autenticazione Stateless & Gestione Identità](#autenticazione-stateless--gestione-identità)
  - [Gestione dei Segreti (No Hardcoded Credentials)](#gestione-dei-segreti-no-hardcoded-credentials)
  - [Crittografia in Transito (TLS/SSL)](#crittografia-in-transito-tlsssl)
  - [Protezione Dati a Riposo (Docker Volumes)](#protezione-dati-a-riposo-docker-volumes)
  - [Protezione Client-Side & Cache Control](#protezione-client-side--cache-control)
  - [Protezione DoS & Rate Limiting](#protezione-dos--rate-limiting)
  - [Audit Logging & Non-Repudiation](#audit-logging--non-repudiation)
  - [Sicurezza della Navigazione](#sicurezza-della-navigazione)
- [Architettura del Sistema](#architettura-del-sistema)

## Scenario
La piattaforma funge da intermediario sicuro tra i dipendenti e i dati aziendali, sfruttando un LLM (Large Language Model) locale per evitare la fuga di dati verso terzi.
* **Dipendenti (Role USER):** Possono interrogare l'assistente per informazioni su buste paga, ferie e policy aziendali.
* **HR Admin (Role HR_ADMIN):** Gestiscono i documenti e monitorano gli audit log delle interazioni.
* **AI Engine (Ollama):** Analizza le richieste in linguaggio naturale all'interno di un perimetro di rete isolato.

## Stack Tecnologico
* **Linguaggio:** Java 21 (LTS)
* **Framework:** Spring Boot 3.2.5
* **Containerizzazione:** Docker & Docker Compose

### Infrastruttura e Librerie
* **Database:** PostgreSQL 16
* **Cache:** Redis 7.2 (Per la gestione delle sessioni e Rate Limiting)
* **AI Engine:** Ollama con modello Llama 3 (LLM on-premise)
* **Sicurezza:** Spring Security, SSL/TLS (Self-signed PKCS12)
* **Comunicazione AI:** Spring AI 0.8.1

---

# Guida alla replicazione

### Prerequisiti
* Sistema Operativo: Ubuntu 25.10 (o compatibile)
* Java JDK 21
* Docker & Docker Compose v2

### Configurazione Ambiente e Segreti (.env)

1. Creare un file `.env` nella root del progetto (non tracciato da Git).
2. Copiare il contenuto dal template seguente:

```properties
# Database Configuration
DB_NAME=hr_db
DB_USER=change_me
DB_PASSWORD=change_me

# Security
APP_SECRET_KEY=insert_your_secure_key_here

# JWT Security
JWT_SECRET_KEY=insert_a_very_long_hex_string_key_here_at_least_256_bit
JWT_EXPIRATION=86400000

```

> **Nota:** Il file `.env` è inserito nel `.gitignore` per evitare leak di sicurezza su GitHub.
Per generare i token JWT viene usato l'algoritmo HMAC-SHA256.

## Generazione Certificati SSL (HTTPS)

Il backend espone le API esclusivamente su HTTPS (Porta 8443). È necessario generare un Keystore.
Eseguire il comando nella cartella `src/main/resources`:

```bash
keytool -genkeypair \
  -alias hr-assistant \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore keystore.p12 \
  -validity 3650

```

> **Nota:** Quando richiesto, inserire una password che deve coincidere con `SSL_KEY_PASSWORD` nel file .env.

## Avvio Infrastruttura Docker

L'intero sistema (Database, Redis, Ollama, Backend e Frontend) è containerizzato. Per garantire un avvio corretto, il download del modello AI e la creazione degli utenti, utilizzare lo script di automazione.

1. Creare un file `start.sh` nella root del progetto e incollare il seguente contenuto:

```bash
#!/bin/bash
docker compose down -v
docker compose up -d
sleep 15

curl -s -k -X POST https://localhost:8443/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@uniba.it",
    "password": "password",
    "fullName": "Super Admin",
    "role": "HR_ADMIN"
}'

curl -s -k -X POST https://localhost:8443/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "mario.rossi@uniba.it",
    "password": "password",
    "fullName": "Mario Rossi",
    "role": "USER"
}'

docker exec -it hr_ollama ollama pull llama3

```

2. Rendere eseguibile e lanciare lo script:

```bash
chmod +x start.sh
./start.sh

```

## Installazione Modello AI

L'installazione del modello LLM (Llama 3) è **gestita automaticamente** dallo script `start.sh`.
Il modello viene scaricato all'interno del container `hr_ollama` e salvato nel volume persistente. Non sono richieste azioni manuali.

---

# Guida all'avvio del Backend

Il Backend Spring Boot viene avviato automaticamente all'interno del container Docker.

1. **Monitoraggio:** Per verificare che il backend sia attivo e controllare i log:
```bash
docker logs -f hr-assistant

```


2. **Accesso SSL (Passaggio Critico):**
Poiché si utilizza un certificato Self-Signed, il browser bloccherà le connessioni.
* Aprire il browser su: `https://localhost:8443/auth/login`
* Accettare il rischio di sicurezza (Avanzate -> Procedi su localhost).
* **Senza questo passaggio, il Frontend non potrà comunicare con il Backend.**



---

# Avvio Frontend (React)

Anche il Frontend è containerizzato e servito tramite Nginx/Vite all'interno di Docker.

1. **Accesso:** Aprire il browser su `http://localhost:5173`.
2. **Login:** Utilizzare le credenziali generate automaticamente:
* **User:** `mario.rossi@uniba.it` / `password`
* **Admin:** `admin@uniba.it` / `password`



> **Nota:** Se si riscontrano errori di "Network Error", assicurarsi di aver completato il passaggio "Accesso SSL" descritto nella sezione Backend.

# Approcci di sicurezza adottati

## Isolamento di Rete (Network Segregation)

È stata implementata una rigorosa segregazione di rete tramite Docker Networks.

* **Ollama (AI):** Non espone alcuna porta verso l'host (macchina fisica). È configurato per rispondere solo all'interno della rete docker `hr-network`.
* **Backend:** Funge da *gateway* unico. Nessun utente può interrogare direttamente l'AI; ogni richiesta deve passare dal Backend che applica autenticazione e validazione.

## Autenticazione Stateless & Gestione Identità

Il sistema di Autenticazione è implementato utilizzando:

* JWT (JSON Web Token): Autenticazione Stateless. Il server non mantiene sessioni in memoria, riducendo l'impatto di attacchi DoS e problemi di scalabilità.

* Password Hashing: Le password degli utenti sono salvate nel database esclusivamente in formato hash tramite BCrypt, rendendole illeggibili anche in caso di Data Breach.

* RBAC (Role-Based Access Control): Implementazione di ruoli (USER, HR_ADMIN) per segregare l'accesso agli endpoint sensibili.

## Gestione dei Segreti (No Hardcoded Credentials)

Per mitigare il rischio di esposizione delle credenziali, è stata adottata una gestione basata su variabili d'ambiente.

* Le password del DB e le chiavi segrete sono iniettate a runtime tramite il file `.env`.
* Il file `application.properties` utilizza placeholder `${NOME_VARIABILE}` invece di valori statici.

## Crittografia in Transito (TLS/SSL)

Tutte le comunicazioni REST API sono protette da crittografia.

* Il server Spring Boot è configurato per accettare solo connessioni HTTPS sulla porta 8443.
* Viene utilizzato un certificato PKCS12 autogenerato (simulando un certificato di una CA interna aziendale).

## Protezione Dati a Riposo (Docker Volumes)

I dati sensibili (Database HR) e i modelli AI (Proprietà intellettuale/Knowledge base) sono salvati su **Docker Volumes** persistenti.
Questo garantisce che i dati sopravvivano al riavvio dei container, ma rimangano isolati dal file system generico dell'host, accessibili solo tramite i processi docker autorizzati.

## Protezione Client-Side & Cache Control

Per mitigare rischi di privacy sui computer condivisi (es. navigazione post-logout):

* **No-Cache Headers:** Implementazione aggressiva degli header `Cache-Control: no-store, no-cache, max-age=0` e `Pragma: no-cache`.
* Questo impedisce al browser di salvare le pagine visitate nella cronologia locale, bloccando la visualizzazione di dati sensibili tramite il tasto "Indietro" del browser dopo il logout.
* **CORS Restrittivo:** Le API accettano richieste solo dall'origine frontend autorizzata (`http://localhost:5173`), bloccando chiamate da domini non attendibili.

## Protezione DoS & Rate Limiting

Per mitigare attacchi Denial of Service (DoS) e abuso delle risorse AI (che sono costose in termini di CPU):

* Algoritmo Token Bucket: Implementato tramite libreria Bucket4j.
* Policy: 10 richieste al minuto per utente.
* Distributed State: I contatori sono mantenuti su Redis, garantendo che il limite persista anche in caso di riavvio dell'applicazione.
* Risposta: Al superamento della soglia, il server restituisce immediatamente status 429 Too Many Requests senza ingaggiare l'AI.

## Audit Logging & Non-Repudiation
Ogni interazione con il sistema viene tracciata in modo indelebile nel database PostgreSQL.

* La tabella audit_logs registra: Username, Domanda, Timestamp e Status (SUCCESS, BLOCKED, ERROR).
* Questo garantisce la non ripudiabilità delle azioni e permette analisi forensi in caso di incidenti.

## Sicurezza della Navigazione

* Logout Sicuro: Utilizzo di `window.location.replace('/login')` per distruggere lo stato dell'applicazione e sovrascrivere la cronologia, impedendo l'uso del tasto "Indietro" per rientrare nella sessione.
* Private Routes: Componenti React "Guard" che bloccano il rendering delle pagine se il token non è presente o è scaduto.
* Axios Interceptors: Gestione centralizzata del token e reindirizzamento automatico al login in caso di errore 401 Unauthorized.
---

# Architettura del Sistema

<p align="center">
<img src="img/architecture_schema.png" alt="Schema Architetturale" width="600">
</p>

L'architettura segue il pattern a microservizi (in container) con un Backend monolitico modulare:

1. **Client (Browser/Postman):** Chiama il Backend su HTTPS (8443).
2. **Spring Boot Backend:**
* Valida il token e l'utente.
* Controlla la Cache Redis per Rate Limiting.
* Recupera i dati strutturati da PostgreSQL.
* Inoltra il prompt anonimizzato a Ollama (porta interna 11434).
3. **Ollama:** Elabora la risposta e la restituisce al backend.
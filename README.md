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
- [Approcci di sicurezza adottati](#approcci-di-sicurezza-adottati)
  - [Isolamento di Rete (Network Segregation)](#isolamento-di-rete-network-segregation)
  - [Crittografia in Transito (SSL/TLS)](#crittografia-in-transito-ssltls)
  - [Gestione Sicura dei Segreti](#gestione-sicura-dei-segreti)
  - [Persistenza e Volumi Sicuri](#persistenza-e-volumi-sicuri)
- [Architettura del Sistema](#architettura-del-sistema)

---

## Scenario
La piattaforma funge da intermediario sicuro tra i dipendenti e i dati aziendali, sfruttando un LLM (Large Language Model) locale per evitare la fuga di dati verso terzi.
* **Dipendenti:** Possono interrogare l'assistente per informazioni su buste paga, ferie e policy aziendali.
* **HR Admin:** Gestiscono i documenti e monitorano gli audit log delle interazioni.
* **AI Engine (Ollama):** Analizza le richieste in linguaggio naturale all'interno di un perimetro di rete isolato.

## Stack Tecnologico
* **Linguaggio:** Java 21 (LTS)
* **Framework:** Spring Boot 3.2.5
* **Containerizzazione:** Docker & Docker Compose

### Infrastruttura e Librerie
* **Database:** PostgreSQL 16 (Relational Data)
* **Cache:** Redis 7.2 (Session & Rate Limiting)
* **AI Engine:** Ollama con modello Llama 3 (On-premise AI)
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

```

> **Nota:** Il file `.env` è inserito nel `.gitignore` per evitare leak di sicurezza su GitHub.

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

Quando richiesto, inserire una password che dovrà coincidere con quella configurata in `application.properties`.

## Avvio Infrastruttura Docker

Il file `docker-compose.yml` orchestra PostgreSQL, Redis e Ollama.

1. Avviare i container:

```bash
docker compose up -d

```

2. Verificare lo stato:

```bash
docker ps

```

Dovresti vedere 3 container attivi: `hr_postgres`, `hr_redis`, `hr_ollama`.

## Installazione Modello AI (Ollama)

È necessario scaricare il modello LLM (Llama 3) all'interno del container in esecuzione.

1. Eseguire il comando di download (~4GB):

```bash
docker exec -it hr_ollama ollama run llama3

```

2. Una volta apparso il prompt di chat `>>>`, digitare `/bye` per uscire. Il modello è ora persistente nel volume Docker.

---

# Guida all'avvio del Backend

1. Aprire il progetto con **IntelliJ IDEA** (versione Ultimate o Community).
2. Attendere l'indicizzazione delle dipendenze Maven (`pom.xml`).
3. Verificare che `application.properties` abbia la configurazione SSL attiva:
```properties
server.port=8443
server.ssl.enabled=true

```


4. Avviare la classe Main `HrVirtualAssistantApplication.java`.
5. Il server sarà raggiungibile su: `https://localhost:8443`.

---

# Approcci di sicurezza adottati

## Isolamento di Rete (Network Segregation)

È stata implementata una rigorosa segregazione di rete tramite Docker Networks.

* **Ollama (AI):** Non espone alcuna porta verso l'host (macchina fisica). È configurato per rispondere solo all'interno della rete docker `hr-network`.
* **Backend:** Funge da *gateway* unico. Nessun utente può interrogare direttamente l'AI; ogni richiesta deve passare dal Backend che applica autenticazione e validazione.

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
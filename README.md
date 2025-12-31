# N_Agent - Code Analysis GitHub App

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.2-blue.svg)](https://spring.io/projects/spring-ai)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0+-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Beta](https://img.shields.io/badge/Status-BETA-orange.svg)](BETA_GUIDE.md)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](Dockerfile)

**N_Agent** è una GitHub App intelligente che analizza automaticamente le Pull Request utilizzando modelli AI (GPT-4o-mini) per fornire feedback contestuale, identificare code smells e suggerire miglioramenti basati su regole personalizzabili.

> 🎯 **BETA Program Active!** - [Unisciti al programma beta](BETA_GUIDE.md) per testare in anteprima le nuove funzionalità.

## 📋 Indice

- [Caratteristiche Principali](#-caratteristiche-principali)
- [🎯 Beta Testing Program](#-beta-testing-program)
- [Architettura](#-architettura)
- [Stack Tecnologico](#-stack-tecnologico)
- [Prerequisiti](#-prerequisiti)
- [Installazione](#-installazione)
- [Configurazione](#-configurazione)
- [Utilizzo](#-utilizzo)
- [API Endpoints](#-api-endpoints)
- [Webhook Events](#-webhook-events)
- [Sviluppo](#-sviluppo)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🚀 Caratteristiche Principali

### Analisi Automatica PR
- **Trigger Configurabili**: Analisi su apertura, riapertura, aggiornamento PR
- **Diff Analysis**: Estrazione e analisi delle modifiche tramite GitHub API
- **AI-Powered Feedback**: Utilizzo di GPT-4o-mini per suggerimenti contestuali
- **Commenti Automatici**: Pubblicazione risultati analisi direttamente nella PR

### Gestione Configurazioni Utente
- **Settings Per Repository**: Regole di analisi personalizzabili per ogni repo
- **Global Settings**: Configurazioni comuni a livello di installazione
- **Cache Intelligente**: Caffeine cache (TTL 50 minuti) per performance ottimali
- **Notifiche Configurabili**: Controllo granulare delle notifiche per evento

### Integrazione GitHub App
- **Webhook Real-time**: Ricezione eventi PR, Installation, Repository
- **HMAC Signature Verification**: Validazione sicurezza payload GitHub
- **Installation Token Management**: Autenticazione JWT per GitHub App
- **Multi-Repository Support**: Gestione dinamica aggiunta/rimozione repository

### Resilienza e Performance
- **Reactive Stack**: Spring WebFlux per I/O non-bloccante
- **Exponential Retry**: Resilience4j per gestione fallimenti MongoDB
- **Timeout Configuration**: Protezione contro operazioni lente
- **Error Handling Robusto**: Eccezioni custom con context logging

---

## 🎯 Beta Testing Program

### Unisciti al Programma BETA!

N_Agent è attualmente in **fase BETA** e cerchiamo beta testers per aiutarci a migliorare l'applicazione prima del rilascio pubblico.

#### 📝 Come Partecipare

1. **Leggi la [Guida Beta Testers](BETA_GUIDE.md)** completa
2. **Compila il form**: [Beta Program Application](https://forms.gle/your-form-link) *(opzionale)*
3. **Installa la GitHub App** sul tuo repository di test
4. **Inizia a testare** e fornisci feedback!

#### ✨ Benefici Beta Testers

- ✅ **Free tier lifetime** quando l'app diventerà paid (se applicabile)
- ✅ **Early access** a tutte le nuove feature
- ✅ **Influenza diretta** sulla roadmap del prodotto
- ✅ **Credito speciale** nel README come contributor
- ✅ **Supporto prioritario** via email/Discord

#### 🎁 Cosa Ricevi

- Accesso completo all'app in ambiente beta
- Documentazione dettagliata e supporto dedicato
- Possibilità di richiedere feature personalizzate
- Partecipazione a decisioni architetturali

#### ⚠️ Cosa Aspettarsi

**Funzionalità Operative**:
- ✅ Analisi automatica PR
- ✅ Commenti AI
- ✅ Configurazione personalizzabile

**Limitazioni BETA**:
- ⚠️ Possibili downtime per manutenzione
- ⚠️ Feature in evoluzione (breaking changes possibili)
- ⚠️ Performance variabili durante testing

#### 📚 Documentazione Beta

- **[BETA_GUIDE.md](BETA_GUIDE.md)** - Guida completa per beta testers
- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Deploy rapido beta/produzione
- **[CHANGELOG.md](CHANGELOG.md)** - Note di release e known issues

#### 📊 Roadmap Beta → Stable

```
✅ BETA v0.1.0 (Attuale)
   ├─ Core features operative
   ├─ Testing con beta testers
   └─ Raccolta feedback

🔄 BETA v0.2.0 (Q1 2025)
   ├─ Fix bug critici
   ├─ Performance optimization
   └─ Support per Claude AI

🚀 STABLE v1.0.0 (Q2 2025)
   ├─ Testing completo
   ├─ Documentazione finale
   └─ Public release
```

---

## 🏗️ Architettura

```
┌─────────────────────────────────────────────────────────────────┐
│                         GitHub Platform                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐       │
│  │  PR #1   │  │  PR #2   │  │  Repo A  │  │  Repo B  │       │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘       │
│       │             │             │             │               │
│       └─────────────┴─────────────┴─────────────┘               │
│                         │                                        │
│                    Webhook Events                                │
└─────────────────────────┼──────────────────────────────────────┘
                          │
                          ▼
         ┌────────────────────────────────────┐
         │   WebhookController                │
         │   - HMAC Validation                │
         │   - Event Routing                  │
         └────────┬───────────────────────────┘
                  │
                  ▼
         ┌────────────────────────────────────┐
         │   WebhookService (Orchestrator)    │
         │   - handlePullRequestEvent()       │
         │   - handleInstallationEvent()      │
         │   - handleInstallationReposEvent() │
         └───┬──────────────────┬─────────────┘
             │                  │
    ┌────────▼────────┐   ┌────▼──────────────┐
    │ UserSettingService│   │ GithubService    │
    │ - getUserSettings()│   │ - getPullRequestDiff()│
    │ - addRepository() │   │ - postComment()  │
    │ - removeRepo()    │   │ - getInstToken() │
    └────────┬──────────┘   └────┬─────────────┘
             │                   │
             ▼                   ▼
    ┌─────────────────┐   ┌─────────────────┐
    │  MongoDB        │   │  GitHub API     │
    │  - UserSetting  │   │  - REST v3      │
    │  - Config       │   │  - Diff Format  │
    └─────────────────┘   └─────────────────┘
                  │
                  ▼
         ┌────────────────────────────────────┐
         │   PrCodeAnalysisService            │
         │   - analyzeCode()                  │
         └────────┬───────────────────────────┘
                  │
                  ▼
         ┌────────────────────────────────────┐
         │   Spring AI ChatClient             │
         │   - OpenAI GPT-4o-mini             │
         │   - Custom Advisors                │
         │   - MongoDB Chat Memory            │
         └────────────────────────────────────┘
```

### Package Structure
```
it.np.n_agent
├── controller          # REST endpoints (WebFlux)
│   ├── WebhookController
│   ├── UserSettingController
│   ├── ConfigController
│   └── TestController
├── service             # Business logic
│   ├── WebhookService
│   ├── UserSettingService
│   ├── PrCodeAnalysisService
│   ├── GithubService
│   ├── ConfigService
│   └── auth/
│       └── GitHubAuthService
├── repository          # MongoDB reactive repositories
│   ├── UserSettingRepository
│   └── ConfigRepository
├── entity              # MongoDB entities
│   ├── UserSetting
│   ├── RepositoryConfig
│   ├── GlobalSettings
│   ├── AnalysisRules
│   └── TriggerSettings
├── dto                 # Data Transfer Objects
│   └── UserSettingDto
├── mapper              # MapStruct mappers
│   └── UserSettingMapper
├── github              # GitHub integration
│   ├── dto/            # Webhook payloads
│   ├── enums/          # EventType, ActionType
│   └── utilities/      # Helpers
├── ai                  # Spring AI components
│   ├── advisor/        # Custom advisors
│   ├── dto/            # AI request/response
│   ├── enums/          # AI model types
│   └── functions/      # Function calling
├── config              # Spring configurations
│   ├── AiConfig
│   ├── CacheConfig
│   ├── CorsConfig
│   ├── RetryConfiguration
│   └── WebClientConfig
├── exception           # Custom exceptions
│   ├── MongoDbException
│   ├── GitHubApiException
│   └── WebhookMainException
└── utilities           # Utility classes
    └── UserSettingUtility
```

---

## 🛠️ Stack Tecnologico

### Backend Framework
- **Spring Boot 3.5.8** - Framework applicativo Java
- **Spring WebFlux** - Reactive web framework (non-blocking I/O)
- **Spring AI 1.1.2** - Integrazione con modelli LLM
- **Spring Data MongoDB Reactive** - Persistenza database reattiva

### Database
- **MongoDB 7.0+** - Database NoSQL document-oriented
- **MongoDB Atlas** (produzione) / **MongoDB locale** (sviluppo)

### AI/ML
- **OpenAI API** - GPT-4o-mini come modello principale
- **Spring AI ChatClient** - Astrazione per chiamate LLM
- **MongoDB Chat Memory** - Persistenza conversazioni AI

### Integrazione GitHub
- **GitHub App** - Autenticazione e autorizzazioni
- **GitHub Webhooks** - Ricezione eventi real-time
- **GitHub REST API v3** - Operazioni programmatiche
- **JJWT (io.jsonwebtoken)** - Generazione JWT per GitHub App

### Sviluppo & Tooling
- **Project Lombok** - Riduzione boilerplate Java
- **MapStruct** - Object mapping type-safe
- **Jackson** - Serializzazione/deserializzazione JSON
- **Maven** - Build tool e dependency management

### Resilienza & Caching
- **Resilience4j** - Circuit breaker, retry, timeout
- **Caffeine** - High-performance in-memory cache
- **Spring Cache Abstraction** - Cache unificata

### Security & Crypto
- **BouncyCastle** - Gestione chiavi private PEM
- **HMAC-SHA256** - Validazione signature webhook

### Testing (pianificato)
- **JUnit 5** - Unit testing framework
- **Reactor Test** - Testing per componenti reactive
- **Testcontainers** - Integration testing con MongoDB

### Logging & Monitoring
- **SLF4J + Logback** - Logging framework
- **Spring Boot Actuator** - Metriche e health checks (futuro)

---

## 📦 Prerequisiti

### Software Richiesto
- **Java 21+** (OpenJDK o Oracle JDK)
- **Maven 3.8+**
- **MongoDB 7.0+** (locale o Atlas)
- **Git**
- **Ngrok / Smee.io** (per sviluppo locale con webhook)

### Account & Credenziali
- **OpenAI API Key** - Per accesso modello GPT-4o-mini
- **GitHub App** - Configurata con permessi appropriati
  - **Repository permissions:**
    - Pull requests: Read & Write
    - Contents: Read
    - Metadata: Read
  - **Subscribe to events:**
    - Pull request
    - Installation
    - Installation repositories

---

## 🔧 Installazione

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/n_agent.git
cd n_agent
```

### 2. Setup MongoDB
**Opzione A - MongoDB Locale:**
```bash
# Installazione con Docker
docker run -d -p 27017:27017 --name mongodb mongo:7.0

# Verifica connessione
docker exec -it mongodb mongosh
```

**Opzione B - MongoDB Atlas:**
1. Crea cluster gratuito su [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. Ottieni connection string
3. Aggiorna `application.yaml`

### 3. Configurazione GitHub App

#### Crea GitHub App
1. Vai su **GitHub Settings** → **Developer settings** → **GitHub Apps** → **New GitHub App**
2. Compila i campi:
   - **GitHub App name**: `n-agent-yourname`
   - **Homepage URL**: `http://localhost:8080`
   - **Webhook URL**: `https://your-ngrok-url.ngrok.io/code-agent/webhook`
   - **Webhook secret**: Genera una stringa casuale (es. `openssl rand -hex 32`)
3. Configura **Permissions**:
   - Repository permissions:
     - Pull requests: **Read & Write**
     - Contents: **Read**
     - Metadata: **Read**
4. Configura **Subscribe to events**:
   - ✅ Pull request
   - ✅ Installation
   - ✅ Installation repositories
5. Crea e salva:
   - **App ID** (es. `12345`)
   - **Private Key** (scarica file `.pem`)
   - **Webhook Secret** (quello generato al passo 2)

#### Installa App su Repository
1. Vai su **Install App** nella dashboard della tua GitHub App
2. Seleziona il tuo account/organizzazione
3. Scegli repository da monitorare

### 4. Configurazione Variabili Ambiente

Crea file `.env` nella root del progetto:
```bash
# OpenAI Configuration
OPEN_AI_KEY=sk-proj-xxxxxxxxxxxxxxxxxxxxxxxxxx

# GitHub App Configuration
GITHUB_APP_ID=123456
GITHUB_WEBHOOK_SECRET=your_webhook_secret_here
GITHUB_PRIVATE_KEY_PATH=./code-analisys-agent.2025-12-17.private-key.pem

# MongoDB Configuration (opzionale, default: localhost)
# MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/code-agent
```

**⚠️ IMPORTANTE:** Aggiungi `.env` al `.gitignore` per non committare credenziali!

### 5. Setup Chiave Privata GitHub
Posiziona il file `.pem` scaricato dalla GitHub App nella root del progetto:
```bash
cp ~/Downloads/your-app-name.2025-12-17.private-key.pem ./code-analisys-agent.2025-12-17.private-key.pem
```

### 6. Build Progetto
```bash
# Clean e compile
mvn clean compile

# Package (crea JAR eseguibile)
mvn clean package -DskipTests
```

---

## ⚙️ Configurazione

### File `application.yaml`
```yaml
spring:
  application:
    name: n_agent
  webflux:
    base-path: /code-agent
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/code-agent}
  ai:
    openai:
      api-key: ${OPEN_AI_KEY}
      chat:
        options:
          model: gpt-4o-mini
          temperature: 0.2          # Controllo creatività (0.0-2.0)
          max-tokens: 2000          # Max token risposta
          frequency-penalty: 0.5    # Riduce ripetizioni
          presence-penalty: 0.3     # Incentiva nuovi topic
      connection-timeout: 10s
      read-timeout: 120s            # Timeout per risposte lunghe

server:
  port: 8080

webhook:
  github:
    secret: ${GITHUB_WEBHOOK_SECRET}

github:
  app:
    id: ${GITHUB_APP_ID}
    private-key-path: ${GITHUB_PRIVATE_KEY_PATH}
  api:
    base-url: https://api.github.com
    installation-token-url: https://api.github.com/app/installations/{installation_id}/access_tokens
```

### Configurazione Cache (CacheConfig.java)
- **TTL UserSettings**: 50 minuti
- **Max Size**: 1000 entry
- **Eviction Policy**: LRU (Least Recently Used)

### Configurazione Retry (RetryConfiguration.java)
- **Max Attempts**: 3
- **Backoff Strategy**: Exponential
- **Wait Duration**: 2 secondi iniziali

---

## 🎯 Utilizzo

### 1. Avvio Applicazione

**Sviluppo (con hot reload):**
```bash
mvn spring-boot:run
```

**Produzione (JAR standalone):**
```bash
java -jar target/n_agent-0.0.1-SNAPSHOT.jar
```

**Docker (opzionale):**
```bash
# Build immagine
docker build -t n-agent:latest .

# Run container
docker run -p 8080:8080 \
  -e OPEN_AI_KEY=$OPEN_AI_KEY \
  -e GITHUB_APP_ID=$GITHUB_APP_ID \
  -e GITHUB_WEBHOOK_SECRET=$GITHUB_WEBHOOK_SECRET \
  -e GITHUB_PRIVATE_KEY_PATH=/app/keys/private-key.pem \
  -v $(pwd)/code-analisys-agent.2025-12-17.private-key.pem:/app/keys/private-key.pem:ro \
  n-agent:latest
```

### 2. Setup Tunnel per Webhook (Sviluppo Locale)

**Opzione A - Ngrok:**
```bash
ngrok http 8080
# Copia URL HTTPS (es. https://abc123.ngrok.io)
# Aggiorna Webhook URL GitHub App: https://abc123.ngrok.io/code-agent/webhook
```

**Opzione B - Smee.io:**
```bash
npm install -g smee-client
smee --url https://smee.io/your-channel --path /code-agent/webhook --port 8080
```

### 3. Test Funzionamento

#### Verifica Health
```bash
curl http://localhost:8080/code-agent/test/health
# Risposta: "Application is running"
```

#### Test Analisi PR
1. Crea una Pull Request su un repository con GitHub App installata
2. Verifica log applicazione:
   ```
   INFO  WebhookService - Processing Pull Request event for PR #123
   INFO  PrCodeAnalysisService - Starting code analysis for PR #123
   INFO  GithubService - Posting comment to PR #123
   ```
3. Controlla commento pubblicato nella PR con risultati analisi

---

## 📡 API Endpoints

### Webhook
```http
POST /code-agent/webhook
Content-Type: application/json
X-GitHub-Event: pull_request
X-Hub-Signature-256: sha256=...

# Body: GitHub webhook payload
```

### User Settings
```http
# Get settings by installation ID
GET /code-agent/settings/{installation-id}
Response: 200 OK | 404 Not Found

# Update settings
PUT /code-agent/settings
Content-Type: application/json
Body: UserSettingDto
Response: 200 OK | 400 Bad Request
```

### Configuration
```http
# Get available AI models
GET /code-agent/configuration/models
Response: ["gpt-4o-mini", "gpt-4", "claude-3"]
```

### Test Endpoints
```http
# Health check
GET /code-agent/test/health
Response: "Application is running"

# Test AI integration
GET /code-agent/test/ai
Response: AI model response
```

---

## 🔔 Webhook Events

### Supported Events

| Event | Action | Handler | Descrizione |
|-------|--------|---------|-------------|
| `pull_request` | `opened` | `handlePullRequestEvent()` | Analizza nuova PR |
| `pull_request` | `reopened` | `handlePullRequestEvent()` | Analizza PR riaperta |
| `pull_request` | `synchronize` | `handlePullRequestEvent()` | Analizza aggiornamenti PR |
| `installation` | `created` | `handleInstallationEvent()` | Crea UserSetting default |
| `installation` | `deleted` | `handleInstallationEvent()` | Elimina UserSetting |
| `installation_repositories` | `added` | `handleNewReposInstallationEvent()` | Aggiunge repository a configurazione |
| `installation_repositories` | `removed` | `handleNewReposInstallationEvent()` | Rimuove repository da configurazione |

### Payload Example (Pull Request)
```json
{
  "action": "opened",
  "number": 123,
  "pull_request": {
    "id": 987654321,
    "number": 123,
    "title": "Add new feature",
    "diff_url": "https://github.com/owner/repo/pull/123.diff",
    "base": {
      "repo": {
        "id": 123456,
        "name": "repo-name",
        "full_name": "owner/repo-name"
      }
    }
  },
  "installation": {
    "id": 12345678
  }
}
```

---

## 💻 Sviluppo

### Setup Ambiente Locale
```bash
# Install dependencies
mvn clean install

# Run tests
mvn test

# Run with debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Abilitare Debug Logging
Nel file `application.yaml`:
```yaml
logging:
  level:
    it.np.n_agent: DEBUG
    org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor: DEBUG
    org.springframework.data.mongodb.core: DEBUG
```

### Struttura Prompt AI
I prompt sono configurabili in `src/main/resources/prompt/`:
- `default_analysis_diff.md` - Template analisi diff PR
- `historical_issue_prompt.md` - Template per context storico
- `user_analysis_rules.md` - Regole personalizzabili utente

### MapStruct Code Generation
Il progetto usa MapStruct per mapping entity ↔ DTO:
```bash
# Generate mappers
mvn clean compile

# Output in: target/generated-sources/annotations/
```

### Code Style
- **Java 21 features**: Record, pattern matching, sealed classes
- **Reactive patterns**: Preferire Mono/Flux, evitare blocking
- **Lombok**: Usare `@Data`, `@Builder`, `@Slf4j`
- **Logging**: Sempre con context (installationId, repoId, prNumber)

---

## 🧪 Testing

### Unit Tests (da implementare)
```bash
mvn test
```

**Coverage target:**
- Service layer: 80%+
- Controller layer: 70%+
- Utilities: 90%+

### Integration Tests (da implementare)
```bash
# Con Testcontainers MongoDB
mvn verify -Pintegration-tests
```

### Manual Testing Checklist
- [ ] PR opened → analisi pubblicata come commento
- [ ] PR updated → nuova analisi su diff aggiornato
- [ ] Installation created → UserSetting salvato in MongoDB
- [ ] Repository added → configurazione aggiornata
- [ ] Repository removed → configurazione aggiornata
- [ ] Cache eviction dopo update settings
- [ ] Retry su MongoDB timeout
- [ ] HMAC signature validation fallimento → 403

---

## 🚀 Deployment

### Deployment su Cloud (Heroku, Railway, Render)

#### Railway.app (Consigliato)
```bash
# Install Railway CLI
npm install -g @railway/cli

# Login
railway login

# Create project
railway init

# Add MongoDB plugin
railway add

# Set environment variables
railway variables set OPEN_AI_KEY=sk-...
railway variables set GITHUB_APP_ID=123456
railway variables set GITHUB_WEBHOOK_SECRET=xxx
railway variables set GITHUB_PRIVATE_KEY_PATH=/app/private-key.pem

# Deploy
railway up
```

#### Environment Variables Production
```bash
SPRING_PROFILES_ACTIVE=prod
OPEN_AI_KEY=sk-proj-...
GITHUB_APP_ID=123456
GITHUB_WEBHOOK_SECRET=...
GITHUB_PRIVATE_KEY_PATH=/app/keys/private-key.pem
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/code-agent
SERVER_PORT=8080
```

### Docker Deployment
```dockerfile
# Dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/n_agent-0.0.1-SNAPSHOT.jar app.jar
COPY code-analisys-agent.2025-12-17.private-key.pem /app/keys/private-key.pem
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build & Run
docker build -t n-agent:1.0 .
docker run -d -p 8080:8080 --env-file .env n-agent:1.0
```

### Kubernetes (opzionale)
```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: n-agent
spec:
  replicas: 2
  selector:
    matchLabels:
      app: n-agent
  template:
    metadata:
      labels:
        app: n-agent
    spec:
      containers:
      - name: n-agent
        image: n-agent:1.0
        ports:
        - containerPort: 8080
        env:
        - name: OPEN_AI_KEY
          valueFrom:
            secretKeyRef:
              name: n-agent-secrets
              key: openai-key
```

---

## 🔍 Troubleshooting

### Problemi Comuni

#### 1. "User settings not found for installationId"
**Causa**: GitHub App installata ma webhook `installation.created` non ricevuto.
**Soluzione**:
```bash
# Reinstalla GitHub App oppure crea manualmente settings via API:
curl -X PUT http://localhost:8080/code-agent/settings \
  -H "Content-Type: application/json" \
  -d '{
    "githubInstallationId": 12345678,
    "globalSettings": {},
    "repositories": []
  }'
```

#### 2. "HMAC signature validation failed"
**Causa**: `GITHUB_WEBHOOK_SECRET` non corrisponde.
**Soluzione**: Verifica che il secret nell'app GitHub coincida con la variabile ambiente.

#### 3. "MongoDB connection timeout"
**Causa**: MongoDB non raggiungibile.
**Soluzione**:
```bash
# Verifica connessione
mongosh "mongodb://localhost:27017/code-agent"

# Check logs MongoDB
docker logs mongodb
```

#### 4. "OpenAI API rate limit exceeded"
**Causa**: Troppi request verso OpenAI.
**Soluzione**:
- Aumenta tier OpenAI account
- Implementa rate limiting applicativo
- Usa cache per analisi ripetute

#### 5. "JWT signature does not match"
**Causa**: Chiave privata PEM non corretta o percorso errato.
**Soluzione**:
```bash
# Verifica formato PEM
openssl rsa -in code-analisys-agent.2025-12-17.private-key.pem -check

# Verifica percorso
ls -la code-analisys-agent.2025-12-17.private-key.pem
```

### Debug Checklist
1. **Verifica log applicazione** con livello DEBUG
2. **Controlla MongoDB** connessione e dati
3. **Verifica webhook delivery** nella dashboard GitHub App
4. **Test HMAC signature** manualmente
5. **Valida OpenAI API key** con curl
6. **Check GitHub App permissions** (Pull requests: Read & Write)

---

## 🤝 Contributing

Contributi benvenuti! Per feature request, bug report o pull request:

1. **Fork** il repository
2. **Crea feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit** le modifiche: `git commit -m 'feat: add amazing feature'`
4. **Push** al branch: `git push origin feature/amazing-feature`
5. **Apri Pull Request**

### Coding Guidelines
- Segui **Google Java Style Guide**
- Scrivi **Javadoc** per metodi pubblici
- Aggiungi **unit test** per nuove feature
- Mantieni **code coverage** sopra 70%
- Usa **Conventional Commits** per messaggi commit

### Commit Message Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

---

## 📄 License

Questo progetto è distribuito con licenza **MIT**. Vedi file [LICENSE](LICENSE) per dettagli.

---

## 📞 Contatti & Support

- **Author**: Nicola Ponziani
- **GitHub**: [@yourusername](https://github.com/yourusername)
- **Issues**: [GitHub Issues](https://github.com/yourusername/n_agent/issues)

---

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Framework applicativo
- [Spring AI](https://spring.io/projects/spring-ai) - Integrazione AI
- [OpenAI](https://openai.com/) - Modelli GPT
- [MongoDB](https://www.mongodb.com/) - Database NoSQL
- [GitHub](https://github.com/) - Platform & API

---

## 📚 Riferimenti

### Documentazione Ufficiale
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/3.5.8/reference/html/)
- [Spring WebFlux Guide](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [GitHub Apps Documentation](https://docs.github.com/en/apps)
- [MongoDB Reactive Streams](https://www.mongodb.com/docs/drivers/reactive-streams/)
- [Resilience4j Guide](https://resilience4j.readme.io/)
- [OpenAI API Reference](https://platform.openai.com/docs/api-reference)

### Tutorial & Guide
- [Building a GitHub App](https://docs.github.com/en/apps/creating-github-apps)
- [Reactive Programming with Spring](https://spring.io/reactive)
- [MapStruct Reference Guide](https://mapstruct.org/documentation/stable/reference/html/)

---

**Made with ❤️ using Spring Boot & Spring AI**


# Changelog

Tutti i cambiamenti rilevanti al progetto N_Agent saranno documentati in questo file.

Il formato è basato su [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
e questo progetto aderisce a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Da Implementare
- [ ] Unit test coverage completo (target 80%)
- [ ] Integration test con Testcontainers
- [ ] Support per Anthropic Claude AI
- [ ] Support per Ollama (offline AI)
- [ ] Dashboard web per configurazione
- [ ] Metriche Prometheus/Grafana
- [ ] Health check endpoints (Spring Actuator)
- [ ] Docker Compose per stack completo
- [ ] GitHub Actions CI/CD pipeline
- [ ] Rate limiting per OpenAI API

---

## [0.0.1-SNAPSHOT] - 2025-12-31

### Added ✨

#### Core Features
- **GitHub App Integration**
  - Webhook receiver con HMAC-SHA256 signature validation
  - Support eventi: `pull_request`, `installation`, `installation_repositories`
  - JWT token generation per GitHub App authentication
  - Installation token management con auto-refresh

- **Pull Request Analysis**
  - Analisi automatica diff PR tramite OpenAI GPT-4o-mini
  - Pubblicazione commenti AI-powered nelle PR
  - Support trigger configurabili: `opened`, `reopened`, `synchronize`
  - Custom prompt templates in `src/main/resources/prompt/`

- **User Settings Management**
  - CRUD completo settings utente via REST API
  - Configurazioni per-repository personalizzabili
  - Global settings a livello installazione
  - Supporto aggiunta/rimozione dinamica repository

#### Infrastructure
- **Reactive Stack**
  - Spring Boot 3.5.8 con WebFlux
  - Spring Data MongoDB Reactive
  - Project Reactor per programmazione reattiva
  - Non-blocking I/O per tutte le operazioni

- **Resilienza & Cache**
  - Resilience4j retry mechanism (exponential backoff)
  - Caffeine cache per UserSettings (TTL 50 minuti)
  - Timeout configuration su tutte le operazioni I/O
  - Automatic cache eviction su update settings

- **AI Integration**
  - Spring AI 1.1.2 con ChatClient
  - OpenAI GPT-4o-mini integration
  - MongoDB Chat Memory per context storico
  - Custom advisors per prompt management

#### Developer Experience
- **Code Quality**
  - Lombok per riduzione boilerplate
  - MapStruct per type-safe object mapping
  - Custom exception handling con context logging
  - Structured logging con SLF4J

- **Documentation**
  - README completo con guide installazione
  - CONTRIBUTING.md con coding guidelines
  - .env.example per setup rapido
  - Javadoc completo su metodi pubblici

### Security 🔒
- HMAC signature validation per webhook GitHub
- BouncyCastle per gestione chiavi private PEM
- Environment variables per secrets management
- .gitignore configurato per proteggere credenziali

### Configuration ⚙️
- Configurazione retry MongoDB (max 3 attempts, 2s backoff)
- Cache configuration (50 min TTL, 1000 max entries)
- CORS configuration per sviluppo locale
- WebClient configuration con timeout customizzati

### API Endpoints 📡
```
POST   /code-agent/webhook                    # GitHub webhook receiver
GET    /code-agent/settings/{installation-id} # Get user settings
PUT    /code-agent/settings                   # Update user settings
GET    /code-agent/configuration/models       # Get available AI models
GET    /code-agent/test/health                # Health check
GET    /code-agent/test/ai                    # Test AI integration
```

### Dependencies 📦

**Core**
- Spring Boot 3.5.8
- Spring AI 1.1.2
- Java 21

**Database**
- Spring Data MongoDB Reactive

**AI/LLM**
- spring-ai-starter-model-openai
- spring-ai-starter-model-chat-memory-repository-mongodb

**Utilities**
- Lombok 1.18.42
- MapStruct 1.6.3
- Jackson (JSON processing)

**Security & Auth**
- JJWT 0.11.5 (JWT generation)
- BouncyCastle 1.78.1 (PEM key loading)

**Resilience**
- Resilience4j 2.2.0
- Caffeine (cache)

**Testing**
- spring-boot-starter-test
- reactor-test (disabled)

### Known Issues 🐛
- ⚠️ Test coverage sotto 10% (da implementare)
- ⚠️ Nessun rate limiting su chiamate OpenAI
- ⚠️ Webhook replay vulnerability (da implementare timestamp check)
- ⚠️ Mancano health check endpoints produzione

### Breaking Changes 💥
Nessuno (prima release)

---

## Release Notes Format

Le future release seguiranno questo formato:

### [X.Y.Z] - YYYY-MM-DD

#### Added ✨
Nuove feature aggiunte.

#### Changed 🔄
Modifiche a feature esistenti.

#### Deprecated ⚠️
Feature che saranno rimosse in future release.

#### Removed 🗑️
Feature rimosse.

#### Fixed 🐛
Bug fix.

#### Security 🔒
Fix vulnerabilità security.

---

## Versioning Strategy

- **MAJOR** (X.0.0): Breaking changes incompatibili
- **MINOR** (0.X.0): Nuove feature backward-compatible
- **PATCH** (0.0.X): Bug fix backward-compatible

---

[Unreleased]: https://github.com/yourusername/n_agent/compare/v0.0.1...HEAD
[0.0.1-SNAPSHOT]: https://github.com/yourusername/n_agent/releases/tag/v0.0.1


# N_Agent - AI-Powered Code Review for GitHub

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.2-blue.svg)](https://spring.io/projects/spring-ai)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0+-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Beta](https://img.shields.io/badge/Status-BETA-orange.svg)](docs/beta/BETA_GUIDE.md)

**N_Agent** è una GitHub App intelligente che analizza automaticamente le tue Pull Request utilizzando AI (GPT-4o-mini) per fornire feedback contestuale, identificare code smells e suggerire miglioramenti.

> 🎯 **Programma BETA Attivo!** - [Unisciti ora](docs/beta/BETA_GUIDE.md) per testare l'app e ricevere early access alle nuove funzionalità.

---

## 🚀 Quick Start

### Per Utenti (Installa l'App)

1. **Installa N_Agent sul tuo repository**
   - Vai su: `https://github.com/apps/n-agent-beta` *(link verrà fornito)*
   - Clicca **"Install"**
   - Seleziona i repository da analizzare

2. **Configura le tue preferenze** *(opzionale)*
   - Accedi al pannello di configurazione: `https://n-agent-beta.railway.app/setup`
   - Personalizza regole di analisi, prompt AI, notifiche

3. **Apri una Pull Request**
   - L'app analizzerà automaticamente le modifiche
   - Riceverai commenti AI direttamente sulla PR

**Nessuna installazione locale richiesta!** Usa l'app come servizio SaaS.

---

## ✨ Caratteristiche Principali

### 🤖 Analisi AI Automatica
- **Feedback Contestuale**: Analisi intelligente delle modifiche al codice
- **Code Smells Detection**: Identifica anti-pattern e bad practices
- **Suggerimenti Pratici**: Soluzioni concrete per migliorare il codice
- **Modelli AI Supportati**: GPT-4o-mini (default), Claude, Ollama

### ⚙️ Configurazione Personalizzabile
- **Regole Per Repository**: Ogni repo può avere regole specifiche
- **Prompt Personalizzati**: Adatta l'analisi al tuo stack tecnologico
- **Trigger Configurabili**: Scegli quando attivare l'analisi (PR aperta, aggiornata, riaperta)
- **Notifiche Granulari**: Controlla quando ricevere feedback

### 🔒 Sicurezza e Privacy
- **Webhook Signature Verification**: HMAC SHA-256 validation
- **Accesso Read-Only al Codice**: L'app legge solo i diff delle PR
- **Nessun Salvataggio Codice**: Zero retention del tuo codice sorgente
- **GitHub App Permissions**: Permessi minimi necessari (PR read/write, Contents read)

### ⚡ Performance e Resilienza
- **Reactive Architecture**: Spring WebFlux per alta concorrenza
- **Cache Intelligente**: Riduzione chiamate API GitHub e MongoDB
- **Retry Logic**: Gestione automatica fallimenti transitori
- **Timeout Protection**: Nessuna analisi infinita

---

## 🎯 Programma Beta Testers

### Perché Partecipare?

N_Agent è in **fase BETA** e cerchiamo beta testers per raccogliere feedback e migliorare l'app.

**Benefici:**
- ✅ **Early Access** a tutte le nuove feature
- ✅ **Supporto Prioritario** via GitHub Issues
- ✅ **Influenza sulla Roadmap** - richiedi feature personalizzate
- ✅ **Riconoscimento Pubblico** come contributor nel progetto

**Cosa Aspettarsi:**
- ⚠️ L'app è stabile ma in evoluzione (possibili breaking changes)
- ⚠️ Occasionali downtime per manutenzione (notifiche anticipate)
- ⚠️ Alcune feature potrebbero cambiare in base ai feedback

### Come Unirsi

1. Leggi la [**Guida Beta Testers**](docs/beta/BETA_GUIDE.md)
2. Installa l'app sui tuoi repository di test
3. Apri issue con feedback, bug report o feature request
4. (Opzionale) Contribuisci al codice - vedi [Contributing](docs/development/CONTRIBUTING.md)

---

## 📚 Documentazione

### Per Utenti
- **[Guida Beta Testers](docs/beta/BETA_GUIDE.md)** - Come partecipare al programma beta
- **[Configurazione App](docs/beta/BETA_GUIDE.md#configurazione)** - Setup pannello web
- **[FAQ](docs/beta/BETA_GUIDE.md#faq)** - Domande frequenti

### Per Sviluppatori
- **[Contributing Guide](docs/development/CONTRIBUTING.md)** - Come contribuire al progetto
- **[Architecture Overview](docs/development/ARCHITECTURE.md)** - Architettura tecnica *(TODO)*
- **[API Documentation](docs/development/API.md)** - Endpoints REST *(TODO)*

### Release Notes
- **[CHANGELOG](CHANGELOG.md)** - Storico modifiche
- **[Release Notes v0.1.0-beta](docs/beta/RELEASE_NOTES_v0.1.0-beta.md)** - Dettagli release attuale

---

## 🏗️ Stack Tecnologico

### Backend
- **Spring Boot 3.5.8** - Application framework
- **Spring WebFlux** - Reactive web stack (non-blocking I/O)
- **Spring AI 1.1.2** - AI model integrations (OpenAI, Anthropic, Ollama)
- **Spring Data MongoDB Reactive** - Database persistence

### Database
- **MongoDB 7.0+** - NoSQL document store

### AI/ML
- **OpenAI GPT-4o-mini** - Default model
- **Anthropic Claude** - Alternative model (supportato)
- **Ollama** - Local models (supportato)

### Integrations
- **GitHub App** - Webhook events + REST API
- **JWT (JJWT)** - GitHub App authentication
- **WebClient** - Reactive HTTP client

---

## 🛠️ Self-Hosting (Opzionale)

> **Nota**: La maggior parte degli utenti **NON deve fare self-hosting**. Usa semplicemente l'app SaaS.

Se preferisci eseguire la tua istanza privata di N_Agent:

1. Clona il repository
2. Consulta [Infrastructure Documentation](infrastructure/README.md)
3. Segui la guida deployment Railway/Docker

**Requisiti self-hosting:**
- Java 21+
- MongoDB 7.0+
- OpenAI API Key (o Claude/Ollama)
- GitHub App creata manualmente

---

## 🤝 Contributing

Contributi benvenuti! Vedi [CONTRIBUTING.md](docs/development/CONTRIBUTING.md) per linee guida.

### Come Contribuire
1. Fork del repository
2. Crea branch feature: `git checkout -b feature/amazing-feature`
3. Commit modifiche: `git commit -m 'Add amazing feature'`
4. Push al branch: `git push origin feature/amazing-feature`
5. Apri Pull Request

---

## 📝 License

Questo progetto è rilasciato sotto licenza **MIT**. Vedi [LICENSE](LICENSE) per dettagli.

---

## 🙏 Supporto

- **Bug Report**: [GitHub Issues](https://github.com/YOUR_USERNAME/n_agent/issues)
- **Feature Request**: [GitHub Discussions](https://github.com/YOUR_USERNAME/n_agent/discussions)
- **Email**: your.email@example.com *(per richieste private)*

---

## 🗓️ Roadmap

### ✅ v0.1.0-beta (Attuale)
- Analisi automatica PR con GPT-4o-mini
- Configurazioni personalizzabili per repository
- Cache e retry logic
- Webhook GitHub completi

### 🔄 v0.2.0 (Q1 2026)
- **Frontend Web UI** per configurazione avanzata
- Supporto Claude AI e Ollama
- Analytics dashboard per maintainer
- Multi-language prompt templates

### 🚀 v1.0.0 Stable (Q2 2026)
- Testing completo e stabilizzazione
- Documentazione estesa
- Public release su GitHub Marketplace

---

<div align="center">

**[Installa N_Agent](https://github.com/apps/n-agent-beta)** | **[Documentazione](docs/)** | **[Unisciti al Beta Program](docs/beta/BETA_GUIDE.md)**

Made with ❤️ by [Your Name](https://github.com/YOUR_USERNAME)

</div>


# ============================================
# N_Agent BETA - Guida per Beta Testers
# ============================================

Benvenuto nel programma **BETA di N_Agent**! 🎉

Questa guida ti aiuterà a installare e testare l'applicazione.

## 📋 Indice

- [Cos'è N_Agent](#cosè-n_agent)
- [Cosa Aspettarsi dalla BETA](#cosa-aspettarsi-dalla-beta)
- [Requisiti](#requisiti)
- [Installazione GitHub App](#installazione-github-app)
- [Primo Utilizzo](#primo-utilizzo)
- [Configurazione Avanzata](#configurazione-avanzata)
- [Segnalare Bug](#segnalare-bug)
- [FAQ](#faq)
- [Supporto](#supporto)

---

## Cos'è N_Agent?

**N_Agent** è una GitHub App intelligente che analizza automaticamente le tue Pull Request usando AI (GPT-4o-mini) per:

✅ Identificare code smells e anti-pattern  
✅ Suggerire miglioramenti architetturali  
✅ Verificare best practices  
✅ Fornire feedback contestuale sul codice  

---

## Cosa Aspettarsi dalla BETA

### ✅ Funzionalità Operative
- ✅ Analisi automatica PR quando aperte/aggiornate
- ✅ Commenti AI pubblicati direttamente nella PR
- ✅ Configurazione per-repository personalizzabile
- ✅ Support per trigger eventi (opened/reopened/synchronize)

### ⚠️ Limitazioni Note
- ⚠️ **Analisi limitata a file modificati** (max 2000 token per analisi)
- ⚠️ **Tempo risposta variabile** (dipende da carico OpenAI API)
- ⚠️ **Nessuna interfaccia web** (configurazione via API REST)
- ⚠️ **Log dettagliati esposti** per debugging (profilo beta)

### 🐛 Known Issues
- [ ] Analisi può fallire su diff molto grandi (>10k linee)
- [ ] Nessun rate limiting su chiamate OpenAI (uso responsabile richiesto)
- [ ] Cache non condivisa tra istanze (se scaled)

---

## Requisiti

### Account Necessari
- ✅ **Account GitHub** (personale o organizzazione)
- ✅ **Repository su GitHub** dove testare l'app
- ✅ **Permessi Admin** sul repository per installare GitHub App

### Opzionale (per configurazione avanzata)
- Tool per API REST (Postman, curl, Insomnia)

---

## Installazione GitHub App

### Step 1: Installa N_Agent Beta

1. **Vai al link di installazione**:
   ```
   https://github.com/apps/n-agent-beta/installations/new
   ```
   *(Il link esatto ti sarà fornito via email)*

2. **Seleziona account/organizzazione**:
   - Scegli dove installare l'app (tuo account personale o org)

3. **Scegli repository**:
   - **Opzione A**: Tutti i repository (consigliato per testing completo)
   - **Opzione B**: Solo repository selezionati (più sicuro)

4. **Autorizza installazione**:
   - Clicca "Install" e conferma

### Step 2: Verifica Installazione

1. Vai su **Settings** del tuo repository
2. Clicca **Integrations** → **GitHub Apps**
3. Verifica che **N_Agent Beta** sia presente

### Step 3: Ottieni Installation ID

Il tuo **Installation ID** ti serve per configurare l'app.

**Come trovarlo**:
```bash
# Opzione A: Dalla URL dopo installazione
# https://github.com/settings/installations/12345678
# -> Installation ID = 12345678

# Opzione B: Via API GitHub
curl -H "Authorization: token YOUR_GITHUB_TOKEN" \
  https://api.github.com/user/installations
```

📝 **Annota il tuo Installation ID**: _________________

---

## Primo Utilizzo

### Test 1: Analisi Automatica PR

1. **Crea una nuova branch**:
   ```bash
   git checkout -b test-n-agent
   ```

2. **Fai modifiche a un file**:
   ```bash
   # Esempio: aggiungi un metodo Java
   echo "public void testMethod() { }" >> src/Main.java
   git add .
   git commit -m "test: add test method"
   git push origin test-n-agent
   ```

3. **Apri Pull Request**:
   - Vai su GitHub
   - Clicca "Compare & pull request"
   - Titolo: "Test N_Agent Beta"
   - Crea PR

4. **Attendi analisi** (30-60 secondi):
   - Verifica che appaia un commento da **n-agent-beta[bot]**
   - Il commento conterrà feedback AI sul tuo codice

### Test 2: Trigger su Aggiornamento PR

1. **Modifica la PR esistente**:
   ```bash
   echo "public void anotherMethod() { }" >> src/Main.java
   git add .
   git commit -m "test: add another method"
   git push origin test-n-agent
   ```

2. **Verifica nuovo commento**:
   - Dovrebbe apparire un nuovo commento con analisi aggiornata

---

## Configurazione Avanzata

### Visualizza Configurazione Corrente

```bash
curl https://n-agent-beta.railway.app/code-agent/settings/{INSTALLATION_ID}
```

**Risposta esempio**:
```json
{
  "githubInstallationId": 12345678,
  "globalSettings": {
    "aiModel": "gpt-4o-mini",
    "language": "en"
  },
  "repositories": [
    {
      "repoId": 987654,
      "repoName": "my-repo",
      "isActive": true,
      "triggers": {
        "onPROpen": true,
        "onPRReopen": true,
        "onPRUpdate": true
      },
      "rules": {
        "checkCodeSmells": true,
        "checkSecurity": true,
        "checkPerformance": true
      }
    }
  ]
}
```

### Disabilita Analisi per un Repository

```bash
curl -X PUT https://n-agent-beta.railway.app/code-agent/settings \
  -H "Content-Type: application/json" \
  -d '{
    "githubInstallationId": 12345678,
    "repositories": [
      {
        "repoId": 987654,
        "repoName": "my-repo",
        "isActive": false
      }
    ]
  }'
```

### Personalizza Trigger Eventi

```bash
# Analisi solo su PR aperte, non su update
curl -X PUT https://n-agent-beta.railway.app/code-agent/settings \
  -H "Content-Type: application/json" \
  -d '{
    "githubInstallationId": 12345678,
    "repositories": [
      {
        "repoId": 987654,
        "triggers": {
          "onPROpen": true,
          "onPRReopen": true,
          "onPRUpdate": false
        }
      }
    ]
  }'
```

---

## Segnalare Bug

### Prima di Segnalare
✅ Verifica che il bug non sia già nei [Known Issues](#-known-issues)  
✅ Controlla che non sia un problema GitHub (webhook non ricevuto)  
✅ Testa su repository diverso per confermare

### Come Segnalare

1. **Apri Issue su GitHub**:
   ```
   https://github.com/yourusername/n_agent/issues/new
   ```

2. **Usa template**:
   ```markdown
   **Versione**: BETA v0.1.0
   **Installation ID**: [tuo ID]
   **Repository**: [link al repo]
   
   **Descrizione Bug**
   [Cosa è successo]
   
   **Step per Riprodurre**
   1. Apri PR con file X
   2. ...
   
   **Comportamento Atteso**
   [Cosa ti aspettavi]
   
   **Screenshot/Log**
   [Se disponibili]
   ```

3. **Aggiungi label**: `beta`, `bug`

### Cosa Riceverai
- ✅ Risposta entro **24-48 ore**
- ✅ Tracciamento fix nella [Issue](https://github.com/yourusername/n_agent/issues)
- ✅ Notifica quando fix deployato

---

## FAQ

### ❓ Quanto costa usare N_Agent Beta?
**Gratuito** durante il periodo beta! (fino a 31/03/2025)

### ❓ Posso usarlo su repository privati?
**Sì**, N_Agent supporta repository pubblici e privati.

### ❓ L'AI legge tutto il mio codice?
**No**, solo le modifiche (diff) della PR. Il codice NON viene salvato permanentemente.

### ❓ Posso cambiare il modello AI?
Nella beta è fisso su **gpt-4o-mini**. In futuro sarà configurabile.

### ❓ Cosa succede se supero limiti OpenAI?
L'analisi fallirà temporaneamente. Verrà aggiunto rate limiting in versione stabile.

### ❓ Come disinstallo l'app?
1. Vai su GitHub Settings → Integrations
2. Trova N_Agent Beta → Configure
3. Clicca "Uninstall"

### ❓ I miei dati vengono salvati?
Sì, solo configurazioni e installation ID in MongoDB. **Nessun codice sorgente** viene salvato.

---

## Supporto

### 📧 Contatti
- **Email**: support@n-agent.dev
- **GitHub Issues**: [Link](https://github.com/yourusername/n_agent/issues)
- **Discord** (opzionale): [Link server Discord]

### ⏰ SLA Beta
- Risposta entro: 24-48 ore
- Uptime target: 95%
- Manutenzione programmata: comunicata 24h prima

### 📊 Status Page
Verifica status servizio: https://status.n-agent.dev *(opzionale)*

---

## 🎁 Ringraziamenti

Grazie per partecipare al programma BETA! Il tuo feedback è prezioso per migliorare N_Agent.

### Benefit Beta Testers
- ✅ **Credito nel README** come contributor
- ✅ **Early access** a nuove feature
- ✅ **Influenza roadmap** tramite feedback
- ✅ **Supporto prioritario** per bug e richieste

---

**Versione Guida**: 1.0 (31/12/2024)  
**Target Release Stabile**: Q2 2026


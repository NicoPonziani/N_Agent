# 🚀 N_Agent BETA - Checklist Lancio

## ✅ Preparazione Completata

### 📁 File Creati
- [x] `Dockerfile` - Multi-stage build ottimizzato
- [x] `docker-compose.yml` - Stack completo (dev/beta/prod)
- [x] `.dockerignore` - Ottimizzazione build
- [x] `Procfile` - Railway/Heroku deployment
- [x] `railway.json` - Configurazione Railway
- [x] `deploy-railway.sh` - Script deploy automatico (Linux/Mac)
- [x] `deploy-railway.ps1` - Script deploy automatico (Windows)
- [x] `application-beta.yaml` - Profilo Spring BETA
- [x] `application-prod.yaml` - Profilo Spring PRODUCTION
- [x] `BETA_GUIDE.md` - Guida per beta testers
- [x] `DEPLOYMENT.md` - Quick deployment guide
- [x] `.env.example` - Template environment variables
- [x] `CONTRIBUTING.md` - Guidelines per contributor
- [x] `CHANGELOG.md` - Release notes
- [x] `LICENSE` - MIT License
- [x] `README.md` - Documentazione completa con badge BETA

### ⚙️ Configurazione
- [x] Versione aggiornata a `0.1.0-BETA` in pom.xml
- [x] Spring profiles configurati (beta/prod)
- [x] Docker healthcheck implementato
- [x] Logging strutturato per debugging
- [x] Actuator endpoints abilitati (beta)
- [x] MongoDB separato per beta (`code-agent-beta`)

---

## 🎯 Prossimi Step per Lancio BETA

### 1. Setup Infrastruttura Cloud

#### Opzione A: Railway (Consigliato)

```bash
# Windows
.\deploy-railway.ps1 -Environment beta

# Linux/Mac
chmod +x deploy-railway.sh
./deploy-railway.sh beta
```

**Cosa fa lo script**:
1. ✅ Crea progetto Railway `n-agent-beta`
2. ✅ Aggiunge MongoDB service
3. ✅ Configura tutte le environment variables
4. ✅ Upload chiave privata GitHub (base64)
5. ✅ Deploy applicazione
6. ✅ Fornisce URL deployment

#### Opzione B: Docker Locale + Ngrok

```bash
# Avvia stack beta
docker-compose --profile beta up -d

# Esponi con ngrok
ngrok http 8080

# Aggiorna webhook GitHub con URL ngrok
```

### 2. Configurazione GitHub App

#### Crea Nuova GitHub App per BETA

1. **Vai su**: https://github.com/settings/apps/new

2. **Compila form**:
   - **Name**: `n-agent-beta` (deve essere unico globally)
   - **Homepage URL**: `https://github.com/yourusername/n_agent`
   - **Webhook URL**: `https://n-agent-beta.railway.app/code-agent/webhook`
   - **Webhook Secret**: Genera con `openssl rand -hex 32`

3. **Permissions (Repository)**:
   ```
   Pull requests: Read & Write
   Contents: Read
   Metadata: Read
   ```

4. **Subscribe to events**:
   ```
   ✅ Pull request
   ✅ Installation
   ✅ Installation repositories
   ```

5. **Save** e scarica:
   - App ID (es. `123456`)
   - Private Key (file `.pem`)
   - Webhook Secret

#### Aggiorna Environment Variables

Se usi Railway:
```bash
railway variables set GITHUB_APP_ID=123456
railway variables set GITHUB_WEBHOOK_SECRET=your_secret
# Private key già caricata da script
```

Se usi Docker:
```bash
# Aggiorna .env
GITHUB_APP_ID=123456
GITHUB_WEBHOOK_SECRET=your_secret
```

### 3. Test Deployment

```bash
# Health check
curl https://n-agent-beta.railway.app/code-agent/test/health
# Expected: "Application is running"

# Test AI integration
curl https://n-agent-beta.railway.app/code-agent/test/ai
# Expected: AI response

# Check actuator (solo beta)
curl https://n-agent-beta.railway.app/actuator/health
# Expected: {"status":"UP"}
```

### 4. Invita Beta Testers

#### Crea Form Google per Application (Opzionale)

**Domande suggerite**:
1. Nome e email
2. GitHub username
3. Tipo di progetto (linguaggio, framework)
4. Numero repository da testare
5. Esperienza con GitHub Apps
6. Motivazione partecipazione

#### Invio Inviti

**Template Email**:
```
Oggetto: 🎉 Sei invitato al BETA Program di N_Agent!

Ciao [Nome],

Sei stato selezionato per partecipare al programma BETA di N_Agent,
la GitHub App intelligente che analizza Pull Request con AI!

🔗 Installa l'app: https://github.com/apps/n-agent-beta/installations/new
📖 Guida completa: https://github.com/yourusername/n_agent/blob/main/BETA_GUIDE.md

Cosa aspettarsi:
✅ Analisi automatica PR con feedback AI
✅ Supporto dedicato via email
✅ Influenza roadmap prodotto
✅ Early access a tutte le nuove funzionalità

📝 Dopo installazione:
1. Installa l'app su un repository di test
2. Apri una PR e verifica che il bot commenti
3. Fornisci feedback su: github.com/yourusername/n_agent/issues

Grazie per partecipare! 🚀

--
N_Agent Team
```

### 5. Monitoring & Support

#### Setup Canali Support

**Opzioni**:
- [ ] Email dedicata: `beta@n-agent.dev`
- [ ] Discord server per beta testers
- [ ] GitHub Discussions abilitato
- [ ] GitHub Issues con label `beta`

#### Monitoring Railway

```bash
# View logs
railway logs -f

# View metrics
railway dashboard

# Check deployments
railway status
```

#### Monitoring Docker

```bash
# View logs
docker-compose logs -f n-agent-beta

# Container stats
docker stats n-agent-beta

# Check healthcheck
docker inspect n-agent-beta | grep -A 10 Health
```

---

## 📊 Metriche da Tracciare

### KPI BETA Program

**Utilizzo**:
- [ ] Numero beta testers attivi
- [ ] Numero repository monitorate
- [ ] PR analizzate (totale e per repository)
- [ ] Tempo medio risposta AI
- [ ] Tasso successo analisi (%)

**Feedback**:
- [ ] Bug reportati (e risolti)
- [ ] Feature richieste
- [ ] Satisfaction score (survey)
- [ ] Net Promoter Score (NPS)

**Performance**:
- [ ] Uptime (target: 95%+)
- [ ] Response time API (target: <500ms)
- [ ] OpenAI API latency
- [ ] MongoDB query time

**Costi**:
- [ ] OpenAI API usage (tokens)
- [ ] Railway/hosting costs
- [ ] MongoDB storage usage

### Tools Consigliati

**Analytics**:
- Google Analytics per landing page
- Mixpanel per eventi app (future)
- Railway metrics dashboard

**Monitoring**:
- Railway logs
- MongoDB Atlas monitoring (se usato)
- Sentry per error tracking (opzionale)

---

## 🎁 Incentivi Beta Testers

### Tier Programma

**🥉 Bronze** (partecipazione base):
- Early access feature
- Credito nel README
- Support prioritario

**🥈 Silver** (10+ PR analizzate):
- Influenza roadmap avanzata
- Badge "Beta Tester" GitHub
- Supporto prioritario esteso

**🥇 Gold** (Bug report/feature contribuite):
- Tutto Silver +
- Consulenza personalizzata
- Co-marketing opportunità

---

## 🚨 Piano Emergenza

### Rollback Rapido

```bash
# Railway
railway rollback

# Docker
docker-compose --profile beta down
git checkout previous-version
docker-compose --profile beta up -d
```

### Comunicazione Downtime

**Template**:
```
🚨 N_Agent BETA - Scheduled Maintenance

Data: [DATE]
Durata: ~30 minuti
Impatto: Servizio non disponibile

Cosa stiamo facendo:
- [Descrizione update/fix]

Alternative:
- Salva bozze PR prima manutenzione
- Ri-trigger analisi dopo ripristino

Updates: github.com/yourusername/n_agent/issues/XX
```

---

## ✅ Checklist Pre-Lancio Finale

Prima di invitare beta testers, verifica:

### Tecnico
- [ ] App deployata e raggiungibile pubblicamente
- [ ] Health endpoint risponde correttamente
- [ ] GitHub webhook URL aggiornato nella app
- [ ] Test webhook delivery funziona
- [ ] MongoDB connesso e operativo
- [ ] OpenAI API key valida e con credito
- [ ] Logs accessibili e strutturati
- [ ] Backup MongoDB configurato (se prod-like)

### Documentazione
- [ ] BETA_GUIDE.md completa e testata
- [ ] README.md con badge BETA e link
- [ ] DEPLOYMENT.md con istruzioni aggiornate
- [ ] FAQ aggiornata con risposte common issues
- [ ] CHANGELOG.md con versione 0.1.0-BETA

### Comunicazione
- [ ] Email invito pronta
- [ ] Form application creato (se usato)
- [ ] Canali support attivi
- [ ] Template risposte FAQ preparate
- [ ] Processo segnalazione bug chiaro

### Legal (se necessario)
- [ ] Privacy policy (se raccolta dati)
- [ ] Terms of Service
- [ ] GDPR compliance (se EU)
- [ ] OpenAI usage policy rispettata

---

## 🎉 Lancio!

Quando tutto è pronto:

```bash
# 1. Final check
curl https://n-agent-beta.railway.app/code-agent/test/health

# 2. Invia inviti beta testers
# (email o annuncio pubblico)

# 3. Monitor first installations
railway logs -f | grep "Building user setting"

# 4. Celebrate! 🎊
```

---

**Versione Checklist**: 1.0  
**Data Creazione**: 31/12/2024  
**Ultimo Aggiornamento**: 31/12/2024


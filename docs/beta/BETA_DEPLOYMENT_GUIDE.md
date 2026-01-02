# 🎯 Next Steps - Deploy Beta SaaS

## 📋 Checklist Immediata

### 1. ✅ Commit Riorganizzazione (ADESSO)

Su branch `dev`:
```bash
git status  # Verifica modifiche
git add -A  # Già fatto
git commit -m "refactor: reorganize for SaaS model

- Move infrastructure to /infrastructure (docker, railway)
- Move docs to /docs (beta, development)  
- Rewrite README for SaaS users (206 lines)
- Remove premium/paid references from all docs
- Update deploy scripts paths
- Remove DEPLOYMENT.md (not needed for SaaS)

Root is now minimal like standard Spring Boot project"
```

### 2. 🔄 Merge su Main (DOPO)

```bash
git checkout main
git merge dev --no-ff
git push origin main
git checkout dev
git push origin dev
```

**IMPORTANTE**: Tutte le modifiche vanno su **ENTRAMBI i branch** (main e dev sono identici per file organizzazione).

---

## 🚀 Deploy Backend su Railway

### Step 1: Setup Railway Project

```bash
cd infrastructure/railway
./deploy-railway.ps1 -Environment beta
```

**Dati richiesti durante deploy:**
- OpenAI API Key
- GitHub App ID (ottieni da passo successivo)
- GitHub Webhook Secret (genera random: `openssl rand -hex 32`)

**Output atteso:**
```
✅ Deploy completato
🌐 URL: https://n-agent-beta.up.railway.app
```

**Annota l'URL!** Ti serve per configurare GitHub App.

---

## 🔧 Configurazione GitHub App Beta

### Step 2: Crea GitHub App

1. Vai su: https://github.com/settings/apps/new

2. **Configurazione base:**
   - **Name**: `N_Agent Beta`
   - **Homepage URL**: `https://github.com/YOUR_USERNAME/n_agent`
   - **Webhook URL**: `https://n-agent-beta.up.railway.app/code-agent/webhook`
   - **Webhook Secret**: (quello generato prima)

3. **Permessi richiesti:**
   - **Repository permissions:**
     - Pull requests: `Read & Write`
     - Contents: `Read-only`
   - **Subscribe to events:**
     - [x] Pull request
     - [x] Pull request review comment

4. **Opzioni:**
   - [ ] Active (lascia deselezionato per ora)
   - **Where can this GitHub App be installed?**
     - ⚪ Only on this account (consigliato per beta)

5. **Crea App** → Salva:
   - **App ID** (usalo nel deploy Railway)
   - **Client ID**
   - **Client Secret**
   - Genera **Private Key** → Download `.pem` → Metti in root `n_agent/`

---

## 🎨 Deploy Frontend

### Step 3: Deploy Frontend su Vercel/Netlify

```bash
cd ../n_agent_frontend  # Vai al progetto frontend

# Verifica configurazione
cat package.json  # Framework? (React/Vue/Next.js)
cat .env.example  # Variabili d'ambiente
```

**Variabili d'ambiente Frontend:**
```env
VITE_API_URL=https://n-agent-beta.up.railway.app
VITE_GITHUB_APP_NAME=n-agent-beta
```

**Deploy Vercel:**
```bash
npm install -g vercel
vercel login
vercel --prod
```

**Deploy Netlify:**
```bash
npm install -g netlify-cli
netlify login
netlify deploy --prod
```

**Annota URL frontend**: `https://n-agent-beta.vercel.app`

---

## 🔗 Collegamento App ↔ Frontend

### Step 4: Aggiorna GitHub App con Setup URL

1. Torna su: https://github.com/settings/apps/n-agent-beta
2. **Setup URL**: `https://n-agent-beta.vercel.app/setup`
3. **Redirect URL**: `https://n-agent-beta.vercel.app/callback`
4. Salva modifiche

---

## ✅ Test End-to-End

### Step 5: Verifica Funzionamento

1. **Attiva GitHub App**:
   - Settings → Developer settings → GitHub Apps → N_Agent Beta
   - [x] Active

2. **Installa su repository di test**:
   ```
   https://github.com/apps/n-agent-beta/installations/new
   ```
   - Seleziona 1 repository di test

3. **Configura via Frontend**:
   - Vai su: `https://n-agent-beta.vercel.app`
   - Login con GitHub
   - Configura regole analisi

4. **Apri Pull Request di test**:
   - Crea branch + modifica file
   - Apri PR
   - **Attendi 30-60 secondi**
   - Verifica commento bot nella PR ✨

---

## 🐛 Troubleshooting

### Backend non risponde
```bash
railway logs --tail 100  # Vedi errori
```

**Errori comuni:**
- ❌ MongoDB connection failed → Verifica `MONGODB_URI` in Railway
- ❌ OpenAI API error → Controlla `OPEN_AI_KEY` valida
- ❌ Webhook signature invalid → Verifica `GITHUB_WEBHOOK_SECRET` match

### Frontend non si connette
- Verifica CORS in backend (`CorsConfig.java`)
- Controlla `VITE_API_URL` in frontend
- Apri DevTools → Console per errori

### Bot non commenta su PR
1. Verifica webhook delivery su GitHub:
   - App Settings → Advanced → Recent Deliveries
   - Cerca errori HTTP 500/401/403

2. Controlla log Railway:
   ```bash
   railway logs | grep "ERROR"
   ```

3. Test webhook manualmente:
   ```bash
   curl -X POST https://n-agent-beta.up.railway.app/code-agent/webhook \
     -H "Content-Type: application/json" \
     -d '{"action":"opened","pull_request":{"number":1}}'
   ```

---

## 📧 Invita Beta Testers

### Step 6: Lancia il Programma Beta

1. **Aggiorna README** con URL reali:
   - Sostituisci `YOUR_USERNAME` con tuo GitHub username
   - Aggiungi link installazione app: `https://github.com/apps/n-agent-beta`
   - Aggiungi link configurazione: `https://n-agent-beta.vercel.app`

2. **Condividi link installazione**:
   ```
   https://github.com/apps/n-agent-beta/installations/new
   ```

3. **Opzionale - Email template** (usa `docs/beta/BETA_LAUNCH_CHECKLIST.md`):
   - Sezione "Email Template for Invitations"

4. **Monitora feedback**:
   - GitHub Issues: bug reports
   - GitHub Discussions: feature requests
   - Analytics Railway: performance

---

## 📊 Monitoring

### Metriche da Tracciare

**Railway Dashboard:**
- ✅ Requests per minute
- ✅ Response time media
- ✅ Error rate
- ✅ MongoDB connections

**GitHub App:**
- ✅ Installazioni totali
- ✅ Webhook deliveries
- ✅ Failed deliveries

**OpenAI:**
- ✅ Token usage
- ✅ Costi giornalieri
- ✅ Rate limits

---

## 🎯 Roadmap Beta

### Fase 1: Testing Privato (Settimana 1-2)
- [ ] Deploy backend + frontend
- [ ] 5-10 beta testers
- [ ] Fix bug critici
- [ ] Raccolta feedback iniziale

### Fase 2: Beta Allargata (Settimana 3-4)
- [ ] 20-50 beta testers
- [ ] Performance tuning
- [ ] Implementa feature richieste
- [ ] Documentazione aggiornata

### Fase 3: Pre-Release (Settimana 5-6)
- [ ] 100+ beta testers
- [ ] Testing sotto carico
- [ ] Stabilizzazione
- [ ] Preparazione Marketplace

### Release Stable v1.0.0 (Q2 2026)
- [ ] Public release GitHub Marketplace
- [ ] Documentazione completa
- [ ] Support plan

---

## 📝 Checklist Finale Prima del Launch

- [ ] Backend deployed su Railway ✅
- [ ] Frontend deployed su Vercel/Netlify ✅
- [ ] GitHub App configurata e attiva ✅
- [ ] Test end-to-end passato ✅
- [ ] README aggiornato con URL reali
- [ ] CHANGELOG aggiornato
- [ ] Email beta testers preparata
- [ ] Monitoring setup
- [ ] Link installazione testato
- [ ] Documentazione rivista

---

**Quando tutto è ✅ → Sei pronto per lanciare la BETA! 🚀**

---

## 🆘 Supporto

Se hai problemi durante il setup:
1. Controlla logs Railway: `railway logs`
2. Verifica webhook deliveries GitHub App
3. Apri issue su GitHub con tag `deployment`
4. Consulta docs: `/docs/beta/BETA_GUIDE.md`

---

**Creato il**: 2 Gennaio 2026  
**Ultima modifica**: 2 Gennaio 2026  
**Valido per**: N_Agent v0.1.0-beta


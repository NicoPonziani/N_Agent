# Infrastructure

Questa cartella contiene configurazioni e script per il deployment dell'applicazione.

## 📁 Struttura

### `/railway`
Script e configurazioni per deployment su Railway:
- `deploy-railway.sh` - Script di deploy per Linux/macOS
- `deploy-railway.ps1` - Script di deploy per Windows PowerShell
- `railway.json` - Configurazione Railway
- `Procfile` - Processo di avvio per Railway

### `/docker`
Configurazioni Docker per sviluppo locale e containerizzazione:
- `Dockerfile` - Immagine Docker per produzione
- `docker-compose.yml` - Setup MongoDB locale + N_Agent
- `.dockerignore` - File esclusi dal build context

## 🚀 Deploy Rapido

### Railway (Produzione)
```bash
cd infrastructure/railway
./deploy-railway.sh [dev|beta|prod]
```

### Docker Locale
```bash
cd infrastructure/docker
docker-compose up -d
```

## 📝 Note

Questi file sono ad **uso esclusivo del maintainer** per deployment e testing.
Gli utenti finali usano l'app come SaaS tramite installazione GitHub App.


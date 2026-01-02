# Infrastructure

Deployment configurations and scripts for N_Agent.

## 📁 Contents

### `/docker`
Docker containerization files:
- **Dockerfile** - Multi-stage build for Spring Boot app
- **docker-compose.yml** - Local development stack (app + MongoDB)
- **.dockerignore** - Files excluded from Docker context

### `/railway`
Railway.app deployment:
- **deploy-railway.ps1** - PowerShell deployment script (Windows)
- **deploy-railway.sh** - Bash deployment script (Linux/macOS)
- **railway.json** - Railway configuration
- **Procfile** - Process startup command

---

## 🚀 Quick Deploy

### Railway (SaaS - Recommended)

**Prerequisites:**
- Railway account: [railway.app](https://railway.app)
- Railway CLI: `npm install -g @railway/cli`
- GitHub repository connected

**Deploy:**
```powershell
# Windows
cd infrastructure/railway
.\deploy-railway.ps1 -Environment beta

# Linux/macOS
cd infrastructure/railway
./deploy-railway.sh beta
```

**Variables to configure on Railway:**
```env
SPRING_PROFILES_ACTIVE=beta
MONGODB_URI=${{MongoDB.MONGO_URL}}
MONGODB_DATABASE=code-agent-beta
GITHUB_APP_ID=your-app-id
GITHUB_WEBHOOK_SECRET=your-secret
GITHUB_PRIVATE_KEY_BASE64=your-base64-key
OPEN_AI_KEY=sk-your-key
```

---

### Docker Compose (Local Development)

**Prerequisites:**
- Docker Desktop installed
- `.env` file configured (see `.env.example`)

**Run:**
```bash
cd infrastructure/docker
docker-compose up -d
```

**Access:**
- App: http://localhost:8080/code-agent/test/health
- MongoDB: localhost:27017

**Stop:**
```bash
docker-compose down
```

---

## 📋 Deployment Checklist

### Before Deploying

- [ ] MongoDB instance ready (Railway plugin or external)
- [ ] OpenAI API key obtained
- [ ] GitHub App created and configured
- [ ] Private key generated and Base64 encoded
- [ ] Webhook secret generated
- [ ] Environment variables configured

### After Deploying

- [ ] Healthcheck responds: `https://your-url.railway.app/code-agent/test/health`
- [ ] GitHub App webhook URL updated
- [ ] Test PR created to verify analysis
- [ ] Logs checked for errors
- [ ] Frontend deployed and connected (if using)

---

## 🔧 Configuration Files

### `railway.json`
Railway-specific build and deployment configuration:
- **Builder**: Auto-detection (Maven project)
- **Build command**: `mvn clean package -DskipTests`
- **Start command**: `java -jar target/*.jar`
- **Healthcheck**: `/code-agent/test/health`

### `Procfile`
Process definition for Railway:
```
web: java -Dserver.port=$PORT $JAVA_OPTS -jar target/*.jar
```

### `Dockerfile`
Multi-stage build:
1. **Build stage**: Maven build with dependencies cached
2. **Runtime stage**: Lightweight JRE 21 with app JAR

---

## 🌐 Deployment Environments

### Development (Local)
- Profile: `dev`
- MongoDB: `mongodb://localhost:27017/code-agent-dev`
- Logs: All levels enabled
- CORS: Localhost only

### Beta (Railway)
- Profile: `beta`
- MongoDB: Railway plugin
- Logs: Debug enabled
- CORS: Vercel + localhost
- URL: `https://nagent-production.up.railway.app`

### Production (Future)
- Profile: `prod`
- MongoDB: Atlas cluster
- Logs: Errors only
- CORS: Production frontend only
- URL: `https://api.n-agent.app`

---

## 📖 Detailed Guides

For complete deployment instructions, see:
- **[BETA Deployment Guide](../docs/beta/BETA_DEPLOYMENT_GUIDE.md)** - Step-by-step Railway + Vercel setup
- **[Main README](../README.md)** - Project overview

---

## 🆘 Troubleshooting

### Railway Build Fails

**Problem**: Maven build fails  
**Solution**: Check `railway.json` has correct `buildCommand`

**Problem**: App crashes on startup  
**Solution**: Verify all environment variables are set

### Docker Build Fails

**Problem**: MongoDB connection refused  
**Solution**: Ensure `docker-compose.yml` MongoDB service is running

**Problem**: Port 8080 already in use  
**Solution**: Change `SERVER_PORT` in `.env` or stop conflicting service

---

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/NicoPonziani/N_Agent/issues)
- **Discussions**: [GitHub Discussions](https://github.com/NicoPonziani/N_Agent/discussions)


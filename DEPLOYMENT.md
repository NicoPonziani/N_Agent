# N_Agent Beta - Quick Deployment Guide

## 🚀 Railway Deployment (Recommended)

### Prerequisites
```bash
npm install -g @railway/cli
```

### Deploy Steps

1. **Login to Railway**:
   ```bash
   railway login
   ```

2. **Run deployment script**:
   ```bash
   chmod +x deploy-railway.sh
   ./deploy-railway.sh beta
   ```

3. **Get deployment URL**:
   ```bash
   railway domain
   ```

4. **Update GitHub App Webhook URL**:
   - Go to: `https://github.com/settings/apps/your-app-name`
   - Update Webhook URL to: `https://your-app.railway.app/code-agent/webhook`

---

## 🐳 Docker Deployment (Local/VPS)

### Quick Start

```bash
# Copy environment variables
cp .env.example .env
# Edit .env with your credentials

# Start beta environment
docker-compose --profile beta up -d

# View logs
docker-compose logs -f n-agent-beta

# Stop
docker-compose --profile beta down
```

### With ngrok (for local testing)

```bash
# Terminal 1: Start stack
docker-compose --profile beta up

# Terminal 2: Start ngrok tunnel
ngrok http 8080

# Update GitHub webhook URL with ngrok URL
```

---

## 🌐 Render Deployment

### Deploy from GitHub

1. **Create account**: https://render.com
2. **New Web Service** → Connect GitHub repository
3. **Configure**:
   - Name: `n-agent-beta`
   - Runtime: `Docker`
   - Branch: `main`
   - Docker Command: *(auto-detected from Dockerfile)*

4. **Environment Variables**:
   ```
   SPRING_PROFILES_ACTIVE=beta
   OPEN_AI_KEY=sk-...
   GITHUB_APP_ID=123456
   GITHUB_WEBHOOK_SECRET=...
   GITHUB_PRIVATE_KEY_PATH=/app/keys/private-key.pem
   ```

5. **Add MongoDB**:
   - Create new MongoDB on Render
   - Copy connection string to `MONGODB_URI`

6. **Deploy**!

---

## ✅ Verification Checklist

After deployment:

- [ ] Health endpoint responds: `curl https://your-app.com/code-agent/test/health`
- [ ] GitHub webhook URL updated
- [ ] Test webhook delivery from GitHub App settings
- [ ] Create test PR → verify bot comment appears
- [ ] Check logs for errors: `railway logs` or `docker-compose logs`
- [ ] MongoDB connection successful (check logs)
- [ ] Cache working (second request faster)

---

## 🐛 Troubleshooting

### App doesn't start
```bash
# Check logs
railway logs
# or
docker-compose logs n-agent-beta

# Common issues:
# - Missing environment variables
# - Invalid OpenAI key
# - GitHub private key not found
# - MongoDB connection failed
```

### Webhook not received
1. Check webhook deliveries in GitHub App settings
2. Verify webhook URL is correct and HTTPS
3. Check HMAC signature validation (secret must match)

### MongoDB connection failed
```bash
# Check MongoDB is running
docker ps | grep mongo

# Test connection
mongosh "mongodb://localhost:27017/code-agent-beta"
```

---

## 📊 Monitoring

### Railway
```bash
# View metrics
railway dashboard

# Tail logs
railway logs -f
```

### Docker
```bash
# Container stats
docker stats n-agent-beta

# View logs
docker logs -f n-agent-beta --tail 100
```

---

## 🔄 Updates

### Update Railway deployment
```bash
git pull origin main
railway up
```

### Update Docker
```bash
git pull origin main
docker-compose --profile beta down
docker-compose --profile beta build --no-cache
docker-compose --profile beta up -d
```

---

## 📞 Support

Issues? Contact: support@n-agent.dev


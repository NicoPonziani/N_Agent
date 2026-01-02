# 🎉 N_Agent v0.1.0-BETA - First Public Beta Release

## What's New

This is the **first public BETA release** of N_Agent - an AI-powered GitHub App that automatically analyzes Pull Requests using GPT-4o-mini to provide contextual feedback, identify code smells, and suggest improvements.

### 🚀 Key Features

#### Core Functionality
- ✅ **Automatic PR Analysis** - AI-powered code review on PR open/update
- ✅ **Smart Comments** - Contextual feedback directly in PR conversations
- ✅ **Configurable Triggers** - Control when analysis runs (open/reopen/update)
- ✅ **Per-Repository Settings** - Customize rules for each repository

#### Deployment Ready
- 🐳 **Docker Support** - Multi-stage build with healthcheck
- ☁️ **Cloud Deployment** - Railway, Render, Heroku ready
- 📦 **Docker Compose** - Complete stack with MongoDB
- 🔧 **Spring Profiles** - Separate configurations for dev/beta/prod

#### Documentation
- 📖 **BETA_GUIDE.md** - Complete guide for beta testers
- 🚀 **DEPLOYMENT.md** - Quick deployment instructions
- ✅ **BETA_LAUNCH_CHECKLIST.md** - Launch preparation guide

### 📦 Installation

**For Beta Testers:**

1. **Install GitHub App**: [Install N_Agent Beta](#) *(link will be provided)*
2. **Read Guide**: [BETA_GUIDE.md](BETA_GUIDE.md)
3. **Start Testing**: Open a PR and see AI feedback!

**For Self-Hosting:**

```bash
# Railway Deployment (Recommended)
.\deploy-railway.ps1 -Environment beta

# Docker Compose
docker-compose --profile beta up -d

# Manual
mvn clean package
java -jar target/n_agent-0.1.0-BETA.jar --spring.profiles.active=beta
```

### ⚙️ Configuration

**Environment Variables Required:**
```bash
OPEN_AI_KEY=sk-...
GITHUB_APP_ID=123456
GITHUB_WEBHOOK_SECRET=...
GITHUB_PRIVATE_KEY_PATH=./private-key.pem
MONGODB_URI=mongodb://localhost:27017/code-agent-beta
```

See [.env.example](.env.example) for full configuration.

### 🎯 What's Included

**Docker & Deployment:**
- Multi-stage Dockerfile with non-root user
- docker-compose.yml with dev/beta/prod profiles
- Railway deployment scripts (Windows + Linux)
- Procfile and railway.json for PaaS deployment

**Spring Boot Profiles:**
- `application-dev.yaml` - Local development (full debug)
- `application-beta.yaml` - BETA environment (metrics enabled)
- `application-prod.yaml` - Production (hardened security)

**Documentation:**
- Complete README with architecture diagrams
- BETA testing program guide
- Deployment quick start
- Contributing guidelines
- Changelog

### ⚠️ Known Issues

- ⚠️ Test coverage below 10% (to be implemented)
- ⚠️ No rate limiting on OpenAI API calls
- ⚠️ Webhook replay vulnerability (timestamp check missing)
- ⚠️ Cache not shared between scaled instances

See [CHANGELOG.md](CHANGELOG.md) for full list.

### 🐛 Reporting Bugs

Found a bug? Please [open an issue](../../issues/new) with:
- Installation ID
- Repository name
- Steps to reproduce
- Expected vs actual behavior
- Logs (if available)

### 🤝 Beta Testing Program

Want to join the BETA program?

**Benefits:**
- ✅ Early access to new features
- ✅ Influence product roadmap
- ✅ Credit as contributor in README
- ✅ Priority support for bugs and requests

**How to participate:**
1. Read [BETA_GUIDE.md](BETA_GUIDE.md)
2. Install the app on your repository
3. Test and provide feedback via GitHub Issues

### 📊 Roadmap

**v0.2.0-BETA** (Q1 2025):
- Support for Claude AI models
- Rate limiting implementation
- Enhanced caching strategy
- Unit test coverage >70%

**v1.0.0 Stable** (Q2 2025):
- Production-ready release
- Complete test coverage
- Performance optimizations
- Public launch

### 🙏 Acknowledgments

Built with:
- [Spring Boot 3.5.8](https://spring.io/projects/spring-boot)
- [Spring AI 1.1.2](https://spring.io/projects/spring-ai)
- [OpenAI GPT-4o-mini](https://openai.com/)
- [MongoDB 7.0+](https://www.mongodb.com/)

### 📄 License

MIT License - see [LICENSE](LICENSE) file for details.

---

**Full Changelog**: [v0.1.0-BETA](CHANGELOG.md#010-beta---2025-01-15)

**Installation Guide**: [BETA_GUIDE.md](BETA_GUIDE.md)

**Deployment Guide**: [DEPLOYMENT.md](DEPLOYMENT.md)


# N_Agent - AI-Powered Code Review for GitHub

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.2-blue.svg)](https://spring.io/projects/spring-ai)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0+-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Beta](https://img.shields.io/badge/Status-BETA-orange.svg)](docs/beta/BETA_GUIDE.md)

**N_Agent** is an intelligent GitHub App that automatically analyzes your Pull Requests using AI (GPT-4o-mini) to provide contextual feedback, identify code smells, and help your team avoid technical debt.

> 🎯 **BETA Program Active!** - [Join now](docs/beta/BETA_GUIDE.md) to test the app and get early access to new features.

---

## 🚀 Quick Start

### For Users (Install the App)

1. **Install N_Agent on your repository**
   - Go to: **[https://github.com/apps/code-analisys-agent/installations/new](https://github.com/apps/code-analisys-agent/installations/new)**
   - Click **"Install"**
   - Select repositories to analyze

2. **Configure your preferences** *(optional)*
   - Access the configuration panel: `https://n-agent-frontend.vercel.app`
   - Customize analysis rules, AI prompts, notifications

3. **Open a Pull Request**
   - The app will automatically analyze changes
   - You'll receive AI comments directly on the PR

**No local installation required!** Use the app as a SaaS service.

---

## ✨ Key Features

### 🤖 AI-Powered Analysis
- **Contextual Feedback**: Intelligent analysis of code changes using GPT-4
- **Code Smells Detection**: Identifies anti-patterns and bad practices
- **Technical Debt Prediction**: Highlights code that might cause future regret
- **Practical Suggestions**: Concrete solutions to improve code quality
- **Supported AI Models**: GPT-4o-mini (default), Claude, Ollama

### ⚙️ Customizable Configuration
- **Per-Repository Rules**: Each repo can have specific settings
- **Custom Prompts**: Adapt analysis to your tech stack
- **Configurable Triggers**: Choose when to activate analysis (PR opened, updated, reopened)
- **Granular Notifications**: Control when to receive feedback

### 🔒 Security & Privacy
- **Webhook Signature Verification**: HMAC SHA-256 validation
- **Read-Only Code Access**: App only reads PR diffs
- **Zero Code Retention**: No storage of your source code
- **Minimal GitHub App Permissions**: PR read/write, Contents read only

### ⚡ Performance & Resilience
- **Reactive Architecture**: Spring WebFlux for high concurrency
- **Intelligent Caching**: Reduces GitHub API and MongoDB calls
- **Retry Logic**: Automatic handling of transient failures
- **Timeout Protection**: No infinite analysis loops

---

## 🎯 Beta Tester Program

### Why Participate?

N_Agent is in **BETA** and we're looking for beta testers to gather feedback and improve the app.

**Benefits:**
- ✅ **Early Access** to all new features
- ✅ **Priority Support** via GitHub Issues
- ✅ **Roadmap Influence** - request custom features
- ✅ **Public Recognition** as a contributor in the project

**What to Expect:**
- ⚠️ The app is stable but evolving (possible breaking changes)
- ⚠️ Occasional downtime for maintenance (advance notice provided)
- ⚠️ Some features may change based on feedback

### How to Join

1. Read the [**Beta Testers Guide**](docs/beta/BETA_GUIDE.md)
2. Install the app on your test repositories: **[Install N_Agent](https://github.com/apps/code-analisys-agent/installations/new)**
3. Open issues with feedback, bug reports, or feature requests
4. (Optional) Contribute code - see [Contributing](docs/development/CONTRIBUTING.md)

---

## 📚 Documentation

### For Users
- **[Beta Testers Guide](docs/beta/BETA_GUIDE.md)** - How to participate in the beta program
- **[App Configuration](docs/beta/BETA_GUIDE.md#configuration)** - Web panel setup
- **[FAQ](docs/beta/BETA_GUIDE.md#faq)** - Frequently asked questions

### For Developers
- **[Contributing Guide](docs/development/CONTRIBUTING.md)** - How to contribute to the project
- **[Architecture Overview](docs/development/ARCHITECTURE.md)** - Technical architecture *(TODO)*
- **[API Documentation](docs/development/API.md)** - REST endpoints *(TODO)*

### Release Notes
- **[CHANGELOG](CHANGELOG.md)** - Change history
- **[Release Notes v0.1.0-beta](docs/beta/RELEASE_NOTES_v0.1.0-beta.md)** - Current release details

---

## 🏗️ Technology Stack

// ...existing code...

---

## 🛠️ Self-Hosting (Optional)

> **Note**: Most users **do NOT need self-hosting**. Simply use the SaaS app.

If you prefer to run your own private instance of N_Agent:

1. Clone the repository
2. See [Infrastructure Documentation](infrastructure/README.md)
3. Follow the Railway/Docker deployment guide

**Self-hosting requirements:**
- Java 21+
- MongoDB 7.0+
- OpenAI API Key (or Claude/Ollama)
- Manually created GitHub App

---

## 🤝 Contributing

Contributions welcome! See [CONTRIBUTING.md](docs/development/CONTRIBUTING.md) for guidelines.

### How to Contribute
1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

---

## 📝 License

This project is released under the **MIT** license. See [LICENSE](LICENSE) for details.

---

## 🙏 Support

- **Bug Reports**: [GitHub Issues](https://github.com/NicoPonziani/N_Agent/issues)
- **Feature Requests**: [GitHub Discussions](https://github.com/NicoPonziani/N_Agent/discussions)
- **Email**: nico.ponziani@gmail.com *(for private inquiries)*

---

## 🗓️ Roadmap

### ✅ v0.1.0-beta (Current)
- Automatic PR analysis with GPT-4o-mini
- Customizable configurations per repository
- Cache and retry logic
- Complete GitHub webhooks

### 🔄 v0.2.0 (Q1 2026)
- **Frontend Web UI** for advanced configuration
- Claude AI and Ollama support
- Analytics dashboard for maintainers
- Multi-language prompt templates

### 🚀 v1.0.0 Stable (Q2 2026)
- Complete testing and stabilization
- Extended documentation
- Public release on GitHub Marketplace

---

<div align="center">

**[Install N_Agent](https://github.com/apps/code-analisys-agent/installations/new)** | **[Documentation](docs/)** | **[Join Beta Program](docs/beta/BETA_GUIDE.md)**

Made with ❤️ by [Nico Ponziani](https://github.com/NicoPonziani)

</div>


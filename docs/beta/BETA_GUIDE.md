# ============================================
# N_Agent BETA - Beta Testers Guide
# ============================================

Welcome to the **N_Agent BETA program**! 🎉

This guide will help you install and test the application.

## 📋 Table of Contents

- [What is N_Agent?](#what-is-n_agent)
- [What to Expect from BETA](#what-to-expect-from-beta)
- [Requirements](#requirements)
- [GitHub App Installation](#github-app-installation)
- [First Use](#first-use)
- [Advanced Configuration](#advanced-configuration)
- [Reporting Bugs](#reporting-bugs)
- [FAQ](#faq)
- [Support](#support)

---

## What is N_Agent?

**N_Agent** is an intelligent GitHub App that automatically analyzes your Pull Requests using AI (GPT-4o-mini) to:

✅ Identify code smells and anti-patterns  
✅ Suggest architectural improvements  
✅ Verify best practices  
✅ Provide contextual code feedback  

---

## What to Expect from BETA

### ✅ Operational Features
- ✅ Automatic PR analysis when opened/updated
- ✅ AI comments published directly in the PR
- ✅ Customizable per-repository configuration
- ✅ Support for event triggers (opened/reopened/synchronize)

### ⚠️ Known Limitations
- ⚠️ **Analysis limited to modified files** (max 2000 tokens per analysis)
- ⚠️ **Variable response time** (depends on OpenAI API load)
- ⚠️ **Web interface in development** (configuration via web panel available)
- ⚠️ **Detailed logs exposed** for debugging (beta profile)

### 🐛 Known Issues
- [ ] Analysis may fail on very large diffs (>10k lines)
- [ ] Rate limiting on OpenAI calls (use responsibly)
- [ ] Cache not shared between instances (if scaled)

---

## Requirements

### Minimum Requirements
- **GitHub Account** (personal or organization)
- **Repository** where you want to install N_Agent
- **Pull Request workflow** (the app analyzes PRs)

### Optional
- Custom AI models (Claude, Ollama) - contact us for configuration

---

## GitHub App Installation

### Step 1: Install the App

1. Go to the installation page:
   **[https://github.com/apps/code-analisys-agent/installations/new](https://github.com/apps/code-analisys-agent/installations/new)**

2. Click **"Install"**

3. Choose:
   - **All repositories** (app will analyze all your repos), or
   - **Only select repositories** (recommended for testing)

4. Click **"Install & Authorize"**

### Step 2: Verify Installation

1. Go to: `https://github.com/settings/installations`
2. You should see **"Code Analysis Agent"** in the installed apps list
3. Click **"Configure"** to view permissions and selected repositories

---

## First Use

### Test the App

1. **Open a test Pull Request**:
   - Create a new branch: `git checkout -b test/n-agent-demo`
   - Make some code changes (add a file, modify logic, add a TODO comment)
   - Commit: `git commit -m "Test N_Agent analysis"`
   - Push: `git push origin test/n-agent-demo`
   - Open PR on GitHub

2. **Wait for analysis** (30-60 seconds)

3. **Check PR comments**:
   - N_Agent will add a comment with:
     - **Summary**: Overall quality assessment
     - **Findings**: List of issues found with severity
     - **Suggestions**: Concrete improvements

### Example Analysis Result

```markdown
## 🤖 N_Agent Analysis

**Summary:**
The changes introduce technical debt with 2 TODOs and a hardcoded value.
Consider refactoring before merging.

**Findings:**
- 🟡 **TODO detected** (line 45, src/main.js): "TODO: add error handling"
  - **Suggestion**: Implement try-catch block before merging

- 🟠 **Magic Number** (line 67, src/config.js): Hardcoded value `3600`
  - **Suggestion**: Extract to constant MAX_TIMEOUT

**Code Regret Score**: 0.65/1.0 (Medium Risk)
```

---

## Advanced Configuration

### Web Configuration Panel

Access the configuration panel at:
**[https://n-agent-frontend.vercel.app](https://n-agent-frontend.vercel.app)**

1. **Login with GitHub**
2. **Select your repository**
3. **Configure**:
   - Analysis rules (enable/disable specific checks)
   - Custom AI prompts
   - Notification preferences
   - Trigger events (when to analyze)

### Per-Repository Settings

You can customize analysis for each repository:

- **AI Model**: GPT-4o-mini (default), Claude, Ollama
- **Analysis Depth**: Quick, Standard, Deep
- **Custom Prompts**: Add domain-specific rules
- **File Exclusions**: Ignore paths (e.g., `tests/`, `vendor/`)

---

## Reporting Bugs

Found a bug? Help us improve! 🐛

### Where to Report

**GitHub Issues**: [https://github.com/NicoPonziani/N_Agent/issues](https://github.com/NicoPonziani/N_Agent/issues)

### What to Include

1. **Title**: Brief description (e.g., "Analysis fails on large PRs")
2. **Description**:
   - Steps to reproduce
   - Expected behavior
   - Actual behavior
3. **Environment**:
   - Repository (if public or you can share)
   - PR link (if possible)
4. **Logs** (if available):
   - Check PR comments for error messages
   - Include relevant details

### Example Bug Report

```markdown
**Title**: Analysis timeout on PR with 500+ files changed

**Description**:
Opened a PR with 600 files changed (refactoring).
N_Agent started analysis but never completed after 5 minutes.

**Steps to Reproduce**:
1. Open PR with >500 files changed
2. Wait for analysis
3. Analysis never completes

**Expected**: Analysis completes or timeout message
**Actual**: No feedback, stuck in "analyzing" state

**Repository**: myorg/myproject (private)
**PR**: #1234
```

---

## FAQ

### General

**Q: Is N_Agent free?**  
A: Yes, during the BETA program N_Agent is completely free. We're still evaluating the future pricing model.

**Q: Which AI model does it use?**  
A: Default is GPT-4o-mini for cost/performance balance. We support Claude and Ollama as alternatives.

**Q: Does it store my code?**  
A: **No**. N_Agent only reads PR diffs via GitHub API and never stores your source code. Analysis results are saved (summary + findings), but not the code itself.

### Installation

**Q: Can I install on private repositories?**  
A: Yes! The app works on both public and private repositories.

**Q: Can I install on organization repositories?**  
A: Yes, if you have admin permissions on the organization.

**Q: How do I uninstall the app?**  
A: Go to `https://github.com/settings/installations`, find "Code Analysis Agent", click "Configure" → "Uninstall".

### Usage

**Q: Why isn't the app commenting on my PR?**  
A: Check:
1. App is installed on the repository
2. PR has code changes (not just README/docs)
3. Wait 60 seconds after opening PR
4. Check Railway logs for errors (contact support)

**Q: Can I disable analysis for specific PRs?**  
A: Add `[skip-n-agent]` in the PR title to skip analysis.

**Q: Analysis is too slow, can I speed it up?**  
A: Analysis time depends on:
- PR size (larger diffs take longer)
- OpenAI API response time (variable)
- Usually completes in 30-60 seconds

**Q: Can I customize which files are analyzed?**  
A: Yes! Use the web configuration panel to exclude paths (e.g., `tests/`, `*.md`).

### Configuration

**Q: Where do I configure custom rules?**  
A: Web panel: [https://n-agent-frontend.vercel.app](https://n-agent-frontend.vercel.app)

**Q: Can I use my own OpenAI API key?**  
A: Not in the SaaS version. For self-hosting, yes (see documentation).

**Q: Does it support languages other than English?**  
A: AI responses are in English by default. Multi-language support coming in future versions.

---

## Support

Need help? We're here!

### Channels

- **GitHub Issues**: [Bug reports & feature requests](https://github.com/NicoPonziani/N_Agent/issues)
- **GitHub Discussions**: [Q&A and community support](https://github.com/NicoPonziani/N_Agent/discussions)
- **Email**: nico.ponziani@gmail.com (for private inquiries)

### Response Time

- **Bug reports**: Within 48 hours
- **Feature requests**: Reviewed weekly
- **Email support**: Within 72 hours

---

## 🎁 Thank You

Thank you for participating in the BETA program! Your feedback is invaluable for improving N_Agent.

### Beta Tester Benefits
- ✅ **Credit in README** as a contributor
- ✅ **Early access** to new features
- ✅ **Roadmap influence** via feedback
- ✅ **Priority support** for bugs and requests

---

**Guide Version**: 1.0 (January 2, 2026)  
**Target Stable Release**: Q2 2026


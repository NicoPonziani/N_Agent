# Contributing to N_Agent

👍 Thank you for your interest in contributing to N_Agent! This document provides guidelines to make the contribution process simple and transparent.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Environment Setup](#development-environment-setup)
- [Development Workflow](#development-workflow)
- [Coding Guidelines](#coding-guidelines)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)
- [Testing](#testing)

---

## Code of Conduct

This project adheres to a Code of Conduct. By participating, you agree to respect these guidelines:

- **Be respectful** and inclusive
- **Accept constructive feedback** with an open mind
- **Focus on value** for the community
- **Show empathy** towards other members

---

## How Can I Contribute?

### 🐛 Reporting Bugs

Before creating a bug report:
1. **Check** that the bug hasn't already been reported in [Issues](https://github.com/NicoPonziani/N_Agent/issues)
2. **Gather information**:
   - N_Agent version
   - Java version
   - Operating system
   - Complete stack trace
   - Steps to reproduce the bug

**Bug Report Template:**
```markdown
**Bug Description**
Brief description of the problem.

**How to Reproduce**
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

**Expected Behavior**
What should happen.

**Actual Behavior**
What actually happens.

**Screenshots**
If applicable, add screenshots.

**Environment**:
- OS: [e.g., Windows 11]
- Java Version: [e.g., 21.0.1]
- N_Agent Version: [e.g., 0.1.0-beta]
```

### 💡 Suggesting Features

Have an idea for a new feature?

1. **Check** [Discussions](https://github.com/NicoPonziani/N_Agent/discussions) to see if it's already been proposed
2. **Open a Discussion** in the "Ideas" category
3. **Describe**:
   - Use case
   - Expected benefits
   - Possible implementation (if you have ideas)

**Feature Request Template:**
```markdown
**Feature Description**
Clear description of the feature.

**Use Case**
Why is this feature needed? What problem does it solve?

**Proposed Solution**
How might this feature be implemented?

**Alternatives Considered**
Other approaches you've thought about.
```

### 📝 Improving Documentation

Documentation improvements are always welcome!

- Fix typos
- Clarify unclear explanations
- Add missing examples
- Translate documentation (currently in English and Italian)

---

## Development Environment Setup

### Prerequisites

- **Java 21+** ([OpenJDK](https://openjdk.org/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.9+** (or use included `mvnw`)
- **MongoDB 7.0+** ([Docker](https://hub.docker.com/_/mongo) or [local installation](https://www.mongodb.com/try/download/community))
- **Git**
- **IDE** (IntelliJ IDEA recommended, VS Code with Java extensions, Eclipse)

### Local Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/NicoPonziani/N_Agent.git
   cd N_Agent/n_agent
   ```

2. **Start MongoDB** (with Docker):
   ```bash
   docker run -d -p 27017:27017 --name mongodb mongo:7.0
   ```

3. **Configure environment variables**:
   Create `.env` file in the project root:
   ```env
   # OpenAI API
   OPEN_AI_KEY=sk-your-openai-api-key

   # GitHub App (for testing webhooks)
   GITHUB_APP_ID=your-app-id
   GITHUB_WEBHOOK_SECRET=your-webhook-secret
   GITHUB_PRIVATE_KEY_BASE64=your-base64-encoded-private-key

   # MongoDB
   MONGODB_URI=mongodb://localhost:27017
   MONGODB_DATABASE=code-agent-dev

   # Spring Profile
   SPRING_PROFILES_ACTIVE=dev
   ```

4. **Build the project**:
   ```bash
   ./mvnw clean install
   ```

5. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

6. **Verify**:
   - Open: `http://localhost:8080/code-agent/test/health`
   - You should see: `"OK - Code Regret Predictor Agent is running"`

---

## Development Workflow

### Branching Strategy (GitFlow)

We use **GitFlow**:

- `main` → Stable production code
- `dev` → Active development branch
- `feature/*` → New features
- `fix/*` → Bug fixes
- `hotfix/*` → Emergency production fixes

### Creating a Feature

1. **Update dev branch**:
   ```bash
   git checkout dev
   git pull origin dev
   ```

2. **Create feature branch**:
   ```bash
   git checkout -b feature/amazing-feature
   ```

3. **Develop**:
   - Write code
   - Add tests
   - Update documentation

4. **Commit**:
   ```bash
   git add .
   git commit -m "feat: add amazing feature"
   ```

5. **Push**:
   ```bash
   git push origin feature/amazing-feature
   ```

6. **Open Pull Request**:
   - From `feature/amazing-feature` to `dev`
   - Follow [PR template](#pull-request-process)

---

## Coding Guidelines

### Java Code Style

- **Follow Spring Boot conventions**
- **Use Lombok** for boilerplate reduction (`@Data`, `@Builder`, etc.)
- **Reactive Programming**: Use `Mono<T>` and `Flux<T>` for async operations
- **Package structure**:
  ```
  it.np.n_agent
  ├── controller   # REST endpoints (WebFlux)
  ├── service      # Business logic
  ├── repository   # MongoDB reactive repositories
  ├── model        # DTOs and MongoDB entities
  ├── config       # Spring configurations
  └── exception    # Custom exceptions
  ```

### Naming Conventions

- **Classes**: `PascalCase` (e.g., `WebhookController`)
- **Methods**: `camelCase` (e.g., `analyzeP ullRequest()`)
- **Variables**: `camelCase` (e.g., `pullRequestNumber`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_ATTEMPTS`)

### Code Quality

- **SonarLint** enabled in IDE
- **No compiler warnings** (fix or suppress with justification)
- **No commented-out code** (use Git history)
- **Meaningful names** (avoid `tmp`, `data`, `doStuff`)

---

## Commit Messages

Follow **Conventional Commits**:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation only
- `style`: Code style (formatting, no logic change)
- `refactor`: Code refactoring (no feature/bug change)
- `perf`: Performance improvement
- `test`: Adding tests
- `chore`: Maintenance tasks (dependencies, build)

### Examples

**Feature:**
```
feat(ai): add Claude AI model support

- Integrate Anthropic Claude API
- Add configuration for model selection
- Update documentation with Claude setup

Closes #42
```

**Bug fix:**
```
fix(webhook): handle missing PR diff gracefully

Previously crashed when GitHub API returned null diff.
Now returns early with warning log.

Fixes #89
```

**Documentation:**
```
docs(readme): update installation instructions

Add missing step for MongoDB setup.
```

---

## Pull Request Process

### Before Opening PR

1. ✅ **Tests pass locally**: `./mvnw test`
2. ✅ **Code compiles**: `./mvnw clean install`
3. ✅ **No merge conflicts** with `dev`
4. ✅ **Documentation updated** (if needed)
5. ✅ **Commit messages** follow conventions

### PR Template

```markdown
## Description
Brief description of changes.

## Type of Change
- [ ] Bug fix (non-breaking change that fixes an issue)
- [ ] New feature (non-breaking change that adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## How Has This Been Tested?
Describe the tests you ran.

## Checklist
- [ ] My code follows the style guidelines
- [ ] I have performed a self-review
- [ ] I have commented my code in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective or that my feature works
- [ ] New and existing unit tests pass locally

## Related Issues
Closes #(issue number)
```

### Review Process

1. **Automated checks** run (GitHub Actions - coming soon)
2. **Maintainer review** (usually within 48 hours)
3. **Feedback addressed** (if any)
4. **Approval + merge** to `dev`

---

## Testing

### Unit Tests

Use **JUnit 5** and **Reactor Test**:

```java
@SpringBootTest
class WebhookServiceTest {
    
    @Autowired
    private WebhookService webhookService;
    
    @Test
    void shouldAnalyzePullRequest() {
        // Given
        PullRequestEvent event = createTestEvent();
        
        // When
        Mono<AnalysisResult> result = webhookService.processPullRequest(event);
        
        // Then
        StepVerifier.create(result)
            .assertNext(analysis -> {
                assertThat(analysis.getFindings()).isNotEmpty();
                assertThat(analysis.getRegretScore()).isBetween(0.0, 1.0);
            })
            .verifyComplete();
    }
}
```

### Integration Tests

Use **Testcontainers** for MongoDB:

```java
@SpringBootTest
@Testcontainers
class UserSettingRepositoryIT {
    
    @Container
    static MongoDBContainer mongodb = new MongoDBContainer("mongo:7.0");
    
    // ... tests
}
```

### Running Tests

```bash
# All tests
./mvnw test

# Specific test
./mvnw test -Dtest=WebhookServiceTest

# Skip tests (for quick build)
./mvnw clean install -DskipTests
```

---

## 📞 Need Help?

- **Questions**: [GitHub Discussions](https://github.com/NicoPonziani/N_Agent/discussions)
- **Chat**: (coming soon - Discord/Slack)
- **Email**: nico.ponziani@gmail.com

---

**Thank you for contributing to N_Agent!** 🙏

Every contribution, big or small, makes a difference.


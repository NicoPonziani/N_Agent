# Contributing to N_Agent

👍 Grazie per l'interesse nel contribuire a N_Agent! Questo documento fornisce linee guida per rendere il processo di contribuzione semplice e trasparente.

## 📋 Indice

- [Code of Conduct](#code-of-conduct)
- [Come Posso Contribuire?](#come-posso-contribuire)
- [Setup Ambiente di Sviluppo](#setup-ambiente-di-sviluppo)
- [Workflow di Sviluppo](#workflow-di-sviluppo)
- [Coding Guidelines](#coding-guidelines)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)
- [Testing](#testing)

---

## Code of Conduct

Questo progetto aderisce a un Code of Conduct. Partecipando, ti impegni a rispettare queste linee guida:

- **Sii rispettoso** e inclusivo
- **Accetta feedback costruttivi** con mente aperta
- **Focus sul valore** per la community
- **Mostra empatia** verso gli altri membri

---

## Come Posso Contribuire?

### 🐛 Reporting Bugs

Prima di creare un bug report:
1. **Verifica** che il bug non sia già stato segnalato in [Issues](https://github.com/yourusername/n_agent/issues)
2. **Raccogli informazioni**:
   - Versione N_Agent
   - Versione Java
   - Sistema operativo
   - Stack trace completo
   - Step per riprodurre il bug

**Template Bug Report:**
```markdown
**Descrizione Bug**
Breve descrizione del problema.

**Come Riprodurre**
1. Vai su '...'
2. Clicca su '....'
3. Scroll down to '....'
4. Vedi errore

**Comportamento Atteso**
Cosa ti aspettavi succedesse.

**Screenshot**
Se applicabile, aggiungi screenshot.

**Ambiente:**
 - OS: [es. macOS 14.0]
 - Java Version: [es. 21]
 - N_Agent Version: [es. 0.0.1-SNAPSHOT]

**Log Errore**
```
Inserisci stack trace qui
```
```

### ✨ Suggesting Enhancements

Per suggerire nuove feature:
1. **Apri Issue** con label `enhancement`
2. **Descrivi il caso d'uso** e il valore aggiunto
3. **Proponi soluzione** (opzionale)

**Template Feature Request:**
```markdown
**Il Problema da Risolvere**
Descrizione chiara del problema attuale.

**Soluzione Proposta**
Come vorresti risolvere il problema.

**Alternative Considerate**
Altre soluzioni che hai valutato.

**Contesto Aggiuntivo**
Screenshot, mockup, esempi di codice.
```

### 📝 Contributing Code

Accettiamo pull request per:
- **Bug fixes**
- **Nuove feature** (discusse preventivamente in Issue)
- **Miglioramenti performance**
- **Refactoring** (con giustificazione)
- **Documentazione**
- **Test coverage**

---

## Setup Ambiente di Sviluppo

### Prerequisiti
- Java 21+
- Maven 3.8+
- MongoDB 7.0+
- Git
- IDE consigliato: IntelliJ IDEA / VS Code

### Clone e Setup
```bash
# Fork repository su GitHub, poi:
git clone https://github.com/YOUR_USERNAME/n_agent.git
cd n_agent

# Setup environment variables
cp .env.example .env
# Edita .env con le tue credenziali

# Install dependencies
mvn clean install

# Run tests
mvn test

# Start application
mvn spring-boot:run
```

### Struttura Branch
- `main` - Branch stabile di produzione
- `develop` - Branch di sviluppo (base per feature branch)
- `feature/*` - Feature branch (es. `feature/add-claude-support`)
- `bugfix/*` - Bug fix branch (es. `bugfix/fix-cache-eviction`)
- `hotfix/*` - Fix urgenti per produzione

---

## Workflow di Sviluppo

### 1. Crea Feature Branch
```bash
# Partendo da develop
git checkout develop
git pull origin develop
git checkout -b feature/my-amazing-feature
```

### 2. Sviluppa
- Scrivi codice seguendo le [Coding Guidelines](#coding-guidelines)
- Aggiungi test per nuove feature
- Aggiorna documentazione se necessario

### 3. Test Localmente
```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=UserSettingServiceTest

# Verify build
mvn clean package
```

### 4. Commit
```bash
git add .
git commit -m "feat(service): add support for Claude AI model"
```

### 5. Push e Open PR
```bash
git push origin feature/my-amazing-feature
# Apri PR su GitHub verso branch develop
```

---

## Coding Guidelines

### Java Style Guide
Seguiamo **Google Java Style Guide** con alcune eccezioni:

#### Naming Conventions
```java
// Classes: PascalCase
public class UserSettingService { }

// Methods/Variables: camelCase
public Mono<Boolean> saveUserSettings(UserSetting userSetting) { }

// Constants: UPPER_SNAKE_CASE
private static final String DEFAULT_MODEL = "gpt-4o-mini";

// Packages: lowercase
package it.np.n_agent.service;
```

#### Lombok Usage
```java
// Preferire @Data per entity/DTO
@Data
@Builder
public class UserSetting {
    private String id;
    private Long userId;
}

// Usare @Slf4j per logging
@Slf4j
@Service
public class MyService {
    public void doSomething() {
        log.info("Doing something");
    }
}
```

#### Reactive Patterns
```java
// ✅ GOOD - Non-blocking reactive chain
public Mono<String> getUser(Long id) {
    return userRepository.findById(id)
        .map(User::getName)
        .defaultIfEmpty("Unknown");
}

// ❌ BAD - Blocking call
public Mono<String> getUser(Long id) {
    User user = userRepository.findById(id).block(); // ❌ NEVER block()
    return Mono.just(user.getName());
}

// ✅ GOOD - Operazioni bloccanti su scheduler dedicato
public Mono<String> readFile(String path) {
    return Mono.fromCallable(() -> Files.readString(Path.of(path)))
        .subscribeOn(Schedulers.boundedElastic());
}
```

#### Error Handling
```java
// ✅ GOOD - Error handling con context
return userRepository.findById(id)
    .switchIfEmpty(Mono.error(new UserNotFoundException(
        String.format("User not found with id: %s", id))))
    .onErrorMap(MongoException.class, error -> 
        new MongoDbException("Failed to retrieve user", HttpStatus.INTERNAL_SERVER_ERROR, error));

// ❌ BAD - Swallow exceptions
return userRepository.findById(id)
    .onErrorReturn(null); // ❌ Loss of error context
```

#### Javadoc
Documenta **tutti i metodi pubblici**:
```java
/**
 * Retrieves user settings for a specific installation ID.
 * Result is cached for 50 minutes (configured in CacheConfig).
 * Applies automatic retry (max 3 attempts) and 3-second timeout.
 *
 * @param installationId GitHub installation ID
 * @return Mono emitting UserSettingDto if found, empty otherwise
 * @throws MongoDbException if retrieval fails after all retries
 */
@Cacheable(value = "userSettings", key = "#installationId")
public Mono<UserSettingDto> getUserSettings(Long installationId) {
    // Implementation
}
```

### Code Organization
```java
// Ordine membri classe:
public class MyService {
    // 1. Static constants
    private static final Logger log = LoggerFactory.getLogger(MyService.class);
    
    // 2. Instance fields
    private final MyRepository repository;
    
    // 3. Constructor
    @Autowired
    public MyService(MyRepository repository) {
        this.repository = repository;
    }
    
    // 4. Public methods
    public Mono<String> publicMethod() { }
    
    // 5. Private methods
    private Mono<String> privateMethod() { }
    
    // 6. Static utility methods
    private static String utilityMethod() { }
}
```

---

## Commit Messages

Seguiamo **Conventional Commits** specification.

### Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- `feat`: Nuova feature
- `fix`: Bug fix
- `docs`: Solo documentazione
- `style`: Formattazione codice (no logic changes)
- `refactor`: Refactoring (no feat/fix)
- `perf`: Performance improvement
- `test`: Aggiungi/modifica test
- `chore`: Build process, dependencies

### Scope (opzionale)
- `service`: Service layer
- `controller`: Controller layer
- `repository`: Repository layer
- `config`: Configuration
- `ai`: AI integration
- `github`: GitHub integration

### Examples
```bash
# Feature
git commit -m "feat(ai): add support for Claude AI model"

# Bug fix
git commit -m "fix(cache): resolve cache eviction issue on repository removal"

# Documentation
git commit -m "docs(readme): add deployment section"

# Refactoring
git commit -m "refactor(service): extract webhook processing to separate handlers"

# Breaking change
git commit -m "feat(api)!: change UserSettingDto structure

BREAKING CHANGE: repositories field is now List<RepositoryConfigDto> instead of List<Long>"
```

---

## Pull Request Process

### Before Submitting PR

1. **Esegui tutti i test**
   ```bash
   mvn clean test
   ```

2. **Verifica code style**
   ```bash
   mvn checkstyle:check
   ```

3. **Update documentation** se necessario

4. **Rebase su develop**
   ```bash
   git fetch origin
   git rebase origin/develop
   ```

### PR Template

```markdown
## Descrizione
Breve descrizione delle modifiche.

## Tipo di Change
- [ ] Bug fix (non-breaking change)
- [ ] Nuova feature (non-breaking change)
- [ ] Breaking change (fix o feature che causa incompatibilità)
- [ ] Documentazione

## Come è stato Testato?
Descrivi test effettuati.

## Checklist
- [ ] Il mio codice segue le coding guidelines
- [ ] Ho eseguito self-review del codice
- [ ] Ho commentato parti complesse
- [ ] Ho aggiornato documentazione
- [ ] Le mie modifiche non generano warning
- [ ] Ho aggiunto test che provano la fix/feature
- [ ] Test nuovi ed esistenti passano localmente
- [ ] Ho verificato che non ci siano breaking change

## Screenshot (se applicabile)
```

### Review Process

1. **Automated Checks**: CI/CD deve passare
2. **Code Review**: Almeno 1 approvazione richiesta
3. **Testing**: Reviewer testa feature localmente
4. **Merge**: Squash merge verso develop

---

## Testing

### Unit Tests
```java
@SpringBootTest
class UserSettingServiceTest {
    
    @Mock
    private UserSettingRepository repository;
    
    @InjectMocks
    private UserSettingService service;
    
    @Test
    void getUserSettings_whenFound_returnsSettings() {
        // Arrange
        Long installationId = 123L;
        UserSetting expected = UserSetting.builder()
            .githubInstallationId(installationId)
            .build();
        when(repository.findByGithubInstallationId(installationId))
            .thenReturn(Mono.just(expected));
        
        // Act
        StepVerifier.create(service.getUserSettings(installationId))
            // Assert
            .expectNextMatches(dto -> dto.getGithubInstallationId().equals(installationId))
            .verifyComplete();
    }
}
```

### Integration Tests
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class WebhookControllerIntegrationTest {
    
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Test
    void webhook_validSignature_returnsOk() {
        webTestClient.post()
            .uri("/webhook")
            .header("X-GitHub-Event", "pull_request")
            .header("X-Hub-Signature-256", "sha256=...")
            .bodyValue(payload)
            .exchange()
            .expectStatus().isOk();
    }
}
```

### Test Coverage Goal
- **Overall**: 70%+
- **Service Layer**: 80%+
- **Controller Layer**: 70%+
- **Utilities**: 90%+

---

## 🎉 Riconoscimenti

Il tuo nome verrà aggiunto alla lista dei contributor nella README!

Grazie per contribuire a N_Agent! 🚀


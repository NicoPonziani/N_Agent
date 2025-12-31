package it.np.n_agent.service.auth;

import io.jsonwebtoken.Jwts;
import it.np.n_agent.exception.GitHubApiException;
import it.np.n_agent.exception.WebhookMainException;
import it.np.n_agent.github.config.GitHubConfig;
import it.np.n_agent.github.enums.HeaderGithubUtility;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class GitHubAuthService {

    private static final Logger log = LoggerFactory.getLogger(GitHubAuthService.class);

    private final GitHubConfig gitHubConfig;
    private final WebClient GITHUB_WEB_CLIENT;

    @Autowired
    public GitHubAuthService(
            GitHubConfig gitHubConfig,
            @Qualifier("githubWebClient") WebClient githubWebClient) {
        this.gitHubConfig = gitHubConfig;
        this.GITHUB_WEB_CLIENT = githubWebClient;
    }

    /**
     * Genera JWT per autenticare come GitHub App
     */
    public String generateJWT() {
        try {
            PrivateKey privateKey = loadPrivateKey();
            Instant now = Instant.now();
            Instant expiration = now.plusSeconds(600); // 10 minutes

            return Jwts.builder()
                    .setIssuer(gitHubConfig.getApp().getId())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expiration))
                    .signWith(privateKey)
                    .compact();
        } catch (Exception e) {
            log.error("Failed to generate JWT", e);
            throw new RuntimeException("Failed to generate JWT", e);
        }
    }

    /**
     * Ottiene Installation Access Token per accedere ai repository
     *
     * @param installationId ID dell'installazione (dal webhook payload)
     * @return Mono con l'access token
     */
    @Cacheable(value = "githubInstallationTokens", key = "#installationId")
    public Mono<String> getInstallationToken(Long installationId) {
        String jwt = generateJWT();

        log.info("Getting installation token for installation ID: {}", installationId);

        return GITHUB_WEB_CLIENT
                .post()
                .uri(gitHubConfig.getApi().getInstallationTokenUrl(), installationId)
                .header("Authorization", "Bearer " + jwt)
                .header("Accept", HeaderGithubUtility.APPLICATION_VND_V3_JSON.getHeaderValue())
                .retrieve()
                .onStatus(
                        status -> status.is5xxServerError() || status.is4xxClientError(),
                        ClientResponse::createException
                )
                .bodyToMono(Map.class)
                .map(response -> {
                    String token = (String) response.get("token");
                    log.info("Installation token obtained");
                    return token;
                })
                .timeout(Duration.ofSeconds(10))
                .onErrorMap(error -> new GitHubApiException("Failed to get installation token", HttpStatus.BAD_GATEWAY, error))
                .cache(Duration.ofMinutes(50)); // Cache per 50 min (scade dopo 60)
    }

    private PrivateKey loadPrivateKey() throws IOException {
        log.info("Loading private key from configured path");

        String keyContent;
        final String privateKeyPath = gitHubConfig.getApp().getPrivateKeyPath();
        if (privateKeyPath.startsWith("classpath:")) {
            String resourcePath = privateKeyPath.replace("classpath:", "");
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {

                if (inputStream == null) {
                    throw new IOException("Key not found in classpath: " + resourcePath);
                }

                keyContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

        } else {
            keyContent = Files.readString(Paths.get(privateKeyPath), StandardCharsets.UTF_8);
        }

        try (PEMParser pemParser = new PEMParser(new StringReader(keyContent))) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            PrivateKey privateKey;

            if (object instanceof PEMKeyPair) {
                privateKey = converter.getPrivateKey(((PEMKeyPair) object).getPrivateKeyInfo());
                log.info("Private key loaded (PKCS#1/RSA format)");

            } else if (object instanceof PrivateKeyInfo) {
                privateKey = converter.getPrivateKey((PrivateKeyInfo) object);
                log.info("Private key loaded (PKCS#8 format)");

            } else {
                throw new IOException("Unsupported key format: " + (object != null ? object.getClass() : "null"));
            }

            return privateKey;
        }
    }
}

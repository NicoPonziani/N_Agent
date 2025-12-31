package it.np.n_agent.service;

import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import it.np.n_agent.entity.Config;
import it.np.n_agent.exception.MongoDbException;
import it.np.n_agent.repository.ConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigService {

    private static final Logger log = LoggerFactory.getLogger(ConfigService.class);

    private final ConfigRepository configRepository;
    private final Retry mongoRetry;

    @Autowired
    public ConfigService(ConfigRepository configRepository, Retry mongoRetry) {
        this.configRepository = configRepository;
        this.mongoRetry = mongoRetry;
    }

    public Mono<List<String>> getAiModelsAvailable() {
        log.info("Fetching available AI models from configuration");

        return configRepository.findByConfigId("GLOBAL")
                .timeout(Duration.ofSeconds(3))
                .transformDeferred(RetryOperator.of(mongoRetry))
                .map(Config::getAiModelsAvailable)
                .doOnSuccess(models -> log.info("Retrieved {} AI models", models.size()))
                .onErrorMap(error -> new MongoDbException(
                        "Error retrieving AI models from configuration",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        error
                ));

    }

    public Mono<List<String>> getLenguagesAvailable() {
        log.info("Fetching available languages from configuration");

        return configRepository.findByConfigId("GLOBAL")
                .timeout(Duration.ofSeconds(3))
                .transformDeferred(RetryOperator.of(mongoRetry))
                .map(Config::getLanguagesAvailable)
                .doOnSuccess(languages -> log.info("Retrieved {} languages", languages.size()))
                .onErrorMap(error -> new MongoDbException(
                        "Error retrieving languages from configuration",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        error
                ));
    }
}

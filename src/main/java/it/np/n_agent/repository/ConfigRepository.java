package it.np.n_agent.repository;

import it.np.n_agent.entity.Config;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ConfigRepository extends ReactiveMongoRepository<Config, String> {

    Mono<Config> findByConfigId(String configId);
}

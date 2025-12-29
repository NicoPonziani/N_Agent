package it.np.n_agent.repository;

import it.np.n_agent.entity.UserSetting;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserSettingRepository extends ReactiveMongoRepository<UserSetting,String> {

    Mono<UserSetting> findByUserId(Long userId);

    @DeleteQuery("{ \"github_installation_id\": ?0, \"user_id\": ?1 }")
    Mono<Long> deleteByInstallationIdAndUserId(Long installationId, Long userId);

}

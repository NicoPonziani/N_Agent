package it.np.n_agent.repository;

import it.np.n_agent.entity.HistoricalIssueEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface IssueRepository extends ReactiveMongoRepository<HistoricalIssueEntity,String> {


    @Query("{ $or: [ " +
             "  {'type': { $regex: ?0, $options: 'i' }}, " +
             "], " +
             "'repository': { $regex: ?1, $options: 'i' } " +
             "'user_installation_id': ?2" +
           "}")
    Flux<HistoricalIssueEntity> findSimilarIssues(String keyword, String fileOrRepo, Long userInstallationId);

}

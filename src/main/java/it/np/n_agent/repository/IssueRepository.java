package it.np.n_agent.repository;

import it.np.n_agent.entity.HistoricalIssueEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface IssueRepository extends ReactiveMongoRepository<HistoricalIssueEntity,String> {


    @Query("{ $or: [ "
            + "  {'type': { $regex: ?0, $options: 'i' }}, "
            + "  {'resolution': { $regex: ?0, $options: 'i' }} "
            + "], 'repository': { $regex: ?1, $options: 'i' } }")
    Flux<HistoricalIssueEntity> findSimilarIssues(String keyword, String repository);

}

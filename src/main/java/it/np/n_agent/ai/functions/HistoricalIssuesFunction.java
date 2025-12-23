package it.np.n_agent.ai.functions;

import it.np.n_agent.entity.HistoricalIssueEntity;
import it.np.n_agent.repository.IssueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.NonNull;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HistoricalIssuesFunction {

    private static final Logger log = LoggerFactory.getLogger(HistoricalIssuesFunction.class);

    private final IssueRepository issueRepository;

    public HistoricalIssuesFunction(@NonNull IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    @Tool(description = "Search for SIMILAR code issues in historical database based on issue type and file. " +
                        "ALWAYS use this FIRST when analyzing code diffs to check past solutions!")
    public CompletableFuture<List<HistoricalIssueEntity>> searchSimilarIssues(
            @ToolParam(description = "Keyword or issue type to search")
            String keyword,

            @ToolParam(description = "File or repository name to filter")
            String fileOrRepo,

            @ToolParam(description = "Max results to return (default 5)")
            Integer maxResults
    ) {
        log.info("🔍 AI called searchSimilarIssues: type={}, file={}, max={}", keyword, fileOrRepo, maxResults);

        return issueRepository.findSimilarIssues(keyword, fileOrRepo)
                .take(maxResults != null ? maxResults : 5)
                .collectList()
                .doOnSuccess(issues -> log.info("Found {} similar issues", issues.size()))
                .subscribeOn(Schedulers.boundedElastic())
                .toFuture();
    }

}
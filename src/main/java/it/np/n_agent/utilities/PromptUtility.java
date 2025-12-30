package it.np.n_agent.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static it.np.n_agent.dto.UserSettingDto.RepositoryConfigDto.AnalysisRulesDto;

public class PromptUtility {

    private static final Logger log = LoggerFactory.getLogger(PromptUtility.class);

    public static String generatePullRequestPrompt(String promptPath, AnalysisRulesDto rules, String diff){
        log.info("Generating pull request prompt using rules: {}", rules);
        String basePrompt = ResourceUtility.loadPromptAsString(promptPath);

        return String.format(basePrompt,
                rules.getLanguages(),
                rules.getNullPointerPrediction(),
                rules.getDebtEstimation(),
                rules.getDetectTODOs(),
                rules.getPredictRegret(),
                rules.getCheckComplexity(),
                rules.getDetectDuplication(),
                rules.getCheckTestCoverage(),
                diff
        );
    }
}

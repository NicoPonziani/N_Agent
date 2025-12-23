package it.np.n_agent.utilities;

import it.np.n_agent.exception.WebhookMainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

public class ResourceUtility {

    private static final Logger log = LoggerFactory.getLogger(ResourceUtility.class);

    public static Resource loadPrompt(String promptPath) {
        String fullPath = "prompt/" + promptPath;
        ClassPathResource resource = new ClassPathResource(fullPath);

        if (!resource.exists()) {
            log.error("Prompt file not found: {}", fullPath);
            throw new WebhookMainException(String.format("Prompt file not found: %s", fullPath), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("✅ Loaded prompt: {} ", promptPath);
        return resource;
    }
}

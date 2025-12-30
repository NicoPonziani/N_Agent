package it.np.n_agent.utilities;

import it.np.n_agent.exception.WebhookMainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    public static String loadPromptAsString(String promptPath) {
        Resource resource = loadPrompt(promptPath);
        try {
            String result = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            log.info("✅ Loaded prompt as a String: {} ", promptPath);
            return result;
        } catch (IOException e) {
            log.error("Failed to read prompt file: {}", promptPath, e);
            throw new WebhookMainException(String.format("Failed to read prompt file: %s", promptPath), HttpStatus.INTERNAL_SERVER_ERROR,e);
        }
    }

}

package it.np.n_agent.ai.advisor;

import it.np.n_agent.exception.AiAnalysisException;
import lombok.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.http.HttpStatus;


public class RetryAdvisor implements CallAdvisor {

    private final int maxRetries;
    private final int order;

    public RetryAdvisor(int maxRetries, int order) {
        this.maxRetries = maxRetries;
        this.order = order;
    }

    @Override
    @NonNull
    public ChatClientResponse adviseCall(@NonNull ChatClientRequest request,@NonNull CallAdvisorChain chain) {
        int attempts = 0;
        Exception lastError = null;

        while (attempts < maxRetries) {
            try {
                return chain.nextCall(request);
            } catch (Exception e) {
                lastError = e;
                attempts++;
            }
        }

        throw new AiAnalysisException(String.format("Failed after %d retries",maxRetries), HttpStatus.BAD_GATEWAY, lastError);
    }

    @Override
    @NonNull
    public String getName() {
        return getClass().getName();
    }

    @Override
    public int getOrder() {
        return order;
    }
}

package it.np.n_agent.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

import static it.np.n_agent.utilities.ResourceUtility.loadPrompt;

@Configuration
public class AiConfig {

    private final static Logger log = LoggerFactory.getLogger(AiConfig.class);

    @Bean("OPEN_AI")
    public ChatClient openAiChatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                         .defaultAdvisors(new SimpleLoggerAdvisor())
//                         .defaultAdvisors(new RetryAdvisor(3,300))
                         .defaultSystem(loadPrompt("default_analysis_diff.md"))
                         .build();
    }

    @Bean
    public OpenAiApi openAiApi(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            RestClient.Builder openAiRestClient) {
        return OpenAiApi.builder()
                .baseUrl("https://api.openai.com")
                .apiKey(apiKey)
                .restClientBuilder(openAiRestClient)
                .build();
    }

    @Bean
    public RestClient.Builder openAiRestClient() {
        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
//                    request.getHeaders().add("Connection", "close");
                    return execution.execute(request, body);
                })
                .requestFactory(new ReactorClientHttpRequestFactory(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(300))  // ← Read timeout// ← Connect timeout
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                                .doOnConnected(conn ->
                                        conn.addHandlerLast(new ReadTimeoutHandler(300))   // Netty read
                                            .addHandlerLast(new WriteTimeoutHandler(120))  // Netty write
                                )
                ));
    }

}

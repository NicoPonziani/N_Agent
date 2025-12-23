package it.np.n_agent.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.NonNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<@NonNull Object,@NonNull Object> caffeineConfig() {
        return Caffeine.newBuilder()
                       .expireAfterWrite(50, TimeUnit.MINUTES);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<@NonNull Object,@NonNull Object> caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);
        caffeineCacheManager.setAsyncCacheMode(true);
        return caffeineCacheManager;
    }

}

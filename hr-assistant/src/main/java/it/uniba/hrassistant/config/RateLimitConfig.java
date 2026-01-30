package it.uniba.hrassistant.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    /**
     * Configurazione del Client Redis (Lettuce).
     * Lettuce è il driver Java più performante per connettersi a Redis.
     */
    @Bean
    public RedisClient redisClient() {
        RedisURI redisURI = RedisURI.builder()
                .withHost(redisHost)
                .withPort(redisPort)
                .withSsl(false)
                .build();

        return RedisClient.create(redisURI);
    }

    /**
     * Configurazione del ProxyManager di Bucket4j.
     * Questo è il "Cervello" che gestisce i secchielli (Buckets) dentro Redis.
     */
    @Bean
    public ProxyManager<String> lettuceProxyManager(RedisClient redisClient) {
        // Apriamo una connessione stateful (persistente) verso Redis.
        StatefulRedisConnection<String, byte[]> connection = redisClient
                .connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

        // Creiamo il manager che userà questa connessione.
        return LettuceBasedProxyManager.builderFor(connection)
                .withExpirationStrategy(
                    // Strategia di pulizia: Se un bucket non viene toccato per 10 minuti, 
                    // Redis lo cancella automaticamente per liberare memoria.
                    ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(10))
                )
                .build();
    }
}
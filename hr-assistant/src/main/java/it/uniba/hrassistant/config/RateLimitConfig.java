package it.uniba.hrassistant.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configurazione per il sistema di Rate Limiting distribuito.
 * <p>
 * Questa classe configura l'integrazione tra la libreria Bucket4j e Redis.
 * Il ProxyManager serve a gestire i "bucket" (secchielli di gettoni) direttamente
 * nella memoria di Redis, permettendo al contatore di sopravvivere ai riavvii
 * dell'applicazione e di funzionare in un ambiente a microservizi (scaling orizzontale).
 */
@Configuration
public class RateLimitConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedisClient redisClient() {
        // Usa l'host e la porta definiti in application.properties
        return RedisClient.create("redis://" + redisHost + ":" + redisPort);
    }

    /**
     * Crea e configura il ProxyManager basato su Lettuce (client Redis).
     *
     * @param redisClient Il client Redis iniettato automaticamente da Spring.
     * @return Un'istanza di ProxyManager capace di gestire i token su Redis.
     */
    @Bean
    public ProxyManager<String> lettuceProxyManager(RedisClient redisClient) {
        // Connette Lettuce a Redis
        StatefulRedisConnection<String, byte[]> connection = redisClient
                .connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

        // Crea il manager dei bucket su Redis
        return LettuceBasedProxyManager.builderFor(connection)
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(10))
                )
                .build();
    }
}
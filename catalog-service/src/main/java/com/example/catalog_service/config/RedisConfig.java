package com.example.catalog_service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching // Habilita el soporte de caching en Spring Boot
public class RedisConfig {

    /**
     * Configuración general del cache usando Redis
     * @return RedisCacheConfiguration con TTL y serialización
     * TTL – “Time To Live”
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig() // Configuración por defecto
                // Tiempo de vida de los valores en cache: 10 minutos
                // Después de este tiempo, Redis elimina automáticamente la clave
                .entryTtl(Duration.ofMinutes(10))

                // No guarda valores nulos en cache
                .disableCachingNullValues()

                // Serializa los valores a JSON usando Jackson
                // Esto permite guardar objetos Java complejos en Redis
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );
    }
}
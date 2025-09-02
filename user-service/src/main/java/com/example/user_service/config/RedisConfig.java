package com.example.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration  // Indica que esta clase contiene beans de configuración para Spring
@EnableCaching  // Habilita la funcionalidad de caché en la aplicación
@Profile("!test")
public class RedisConfig {

    // Bean que define la configuración por defecto de la caché
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig() // Configuración base de Redis
                .entryTtl(Duration.ofMinutes(5)) // TTL: Tiempo de vida de cada entrada (5 minutos)
                .disableCachingNullValues() // Evita almacenar valores nulos en la caché
                .serializeValuesWith( // Define cómo se serializan los valores
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()) // Serializa objetos a JSON
                );
    }

    // Bean que gestiona la caché y aplica la configuración definida
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory) // Crea un administrador de caché usando la conexión Redis
                .cacheDefaults(cacheConfiguration()) // Aplica la configuración personalizada
                .build(); // Construye y devuelve el CacheManager
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(serializer);
        return template;
    }
}
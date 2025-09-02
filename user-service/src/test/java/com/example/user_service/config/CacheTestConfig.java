package com.example.user_service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CacheTestConfig {

    // Definimos un bean llamado 'cacheManager' que reemplazará el uso de Redis durante los tests
    // Spring lo inyectará automáticamente en los servicios que usan @Cacheable, @CacheEvict, etc.
    @Bean
    public CacheManager cacheManager() {
        // Creamos un CacheManager en memoria (ConcurrentMapCacheManager)
        // Esto significa que la cache se guarda solo en memoria durante la ejecución de la prueba
        // y no intenta conectarse a Redis ni a ningún otro sistema externo
        return new ConcurrentMapCacheManager();
    }
}

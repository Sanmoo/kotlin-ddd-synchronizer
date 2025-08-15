package com.github.sanmoo.ddd.synchronizer.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class GenericConfig {
    @Bean
    fun clock(): Clock {
        return Clock.systemDefaultZone()
    }

    @Bean
    fun uuidProvider(): () -> String {
        return { java.util.UUID.randomUUID().toString() }
    }
}
package com.github.sanmoo.ddd.synchronizer.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

@Configuration
class SqsConfig(
    private val sqsProperties: SqsProperties
) {
    @Bean
    fun sqsAsyncClient(): SqsAsyncClient {
        val builder = SqsAsyncClient.builder()
            .region(Region.of(sqsProperties.region))

        // For local development with LocalStack
        sqsProperties.endpoint?.let {
            builder.endpointOverride(URI.create(it))
        }

        return builder.build()
    }
}

@Configuration
@ConfigurationProperties("sqs")
data class SqsProperties(
    var region: String = "us-east-1",
    var endpoint: String? = null,
    var maxNumberOfMessages: Int = 10,
    var waitTimeSeconds: Int = 20
)

package com.github.sanmoo.ddd.synchronizer.config

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
        if (sqsProperties.endpoint != null) {
            builder.endpointOverride(URI.create(sqsProperties.endpoint))
        }

        return builder.build()
    }
}

data class SqsProperties(
    val region: String = "us-east-1",
    val endpoint: String? = null,
    val queueUrl: String,
    val maxNumberOfMessages: Int = 10,
    val waitTimeSeconds: Int = 20
)

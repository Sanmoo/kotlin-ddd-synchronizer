package com.github.sanmoo.consumers.kinesis

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.sanmoo.consumers.EventProcessor
import com.github.sanmoo.messages.CommandDispatcher
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.kinesis.common.ConfigsBuilder
import java.net.URI

class KCLFactories {
    fun createKCLConfigsBuilder(): ConfigsBuilder {
        val region = Region.of("us-east-1")
        val uri = URI.create("http://localhost:4566")
        val credentialsProvider = StaticCredentialsProvider.create(
            AwsBasicCredentials.create("dummy", "dummy")
        )

        val objectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
        val kinesisAsyncClient = KinesisAsyncClient.builder().endpointOverride(uri).region(region).credentialsProvider(credentialsProvider).build()
        val dynamodbAsyncClient = DynamoDbAsyncClient.builder().endpointOverride(uri).region(region).credentialsProvider(credentialsProvider).build()
        val cloudWatchAsyncClient = CloudWatchAsyncClient.builder().endpointOverride(uri).region(region).credentialsProvider(credentialsProvider).build()
        val sqsAsyncClient = SqsAsyncClient.builder().endpointOverride(uri).region(region).credentialsProvider(credentialsProvider).build()
        val commandDispatcher = CommandDispatcher(sqsAsyncClient, objectMapper, System::getenv)

        val configsBuilder = ConfigsBuilder(
            UpstreamStreamTracker(),
            "integrator",
            kinesisAsyncClient,
            dynamodbAsyncClient,
            cloudWatchAsyncClient,
            "single-worker",
            SimpleRecordProcessorFactory(EventProcessor(objectMapper, commandDispatcher)),
        )

        return configsBuilder
    }
}
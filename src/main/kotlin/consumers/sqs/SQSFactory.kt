package com.github.sanmoo.consumers.sqs

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

class SQSFactory {
    fun create(): SqsAsyncClient {
        val region = Region.of("us-east-1")
        val uri = URI.create("http://localhost:4566")
        val credentialsProvider = StaticCredentialsProvider.create(
            AwsBasicCredentials.create("dummy", "dummy")
        )

        return SqsAsyncClient.builder().endpointOverride(uri).region(region).credentialsProvider(credentialsProvider)
            .build()
    }
}
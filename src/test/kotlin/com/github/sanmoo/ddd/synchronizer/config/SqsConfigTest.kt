package com.github.sanmoo.ddd.synchronizer.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.sqs.SqsAsyncClient

class SqsConfigTest {
    @Test
    fun testSqsAsyncClientLocalEndpoint() {
        val sqsProperties = SqsProperties()
        sqsProperties.region = "us-east-1"
        sqsProperties.endpoint = "http://localhost:4566"
        val sut = SqsConfig(sqsProperties)
        assertInstanceOf(SqsAsyncClient::class.java, sut.sqsAsyncClient())
    }

    @Test
    fun testSqsAsyncClient() {
        val sqsProperties = SqsProperties()
        sqsProperties.region = "us-east-1"
        val sut = SqsConfig(sqsProperties)
        assertInstanceOf(SqsAsyncClient::class.java, sut.sqsAsyncClient())
    }
}
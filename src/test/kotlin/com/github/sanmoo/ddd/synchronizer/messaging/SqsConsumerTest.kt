package com.github.sanmoo.ddd.synchronizer.messaging

import com.github.sanmoo.ddd.synchronizer.config.SqsProperties
import io.mockk.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.*
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.services.sqs.model.Message as SqsMessage

class SqsConsumerTest {
    private lateinit var sqsClient: SqsAsyncClient
    private lateinit var messageProcessor: MessageProcessor
    private lateinit var sqsProperties: SqsProperties
    private lateinit var consumer: SqsConsumer

    @BeforeEach
    fun setUp() {
        sqsClient = mockk()
        messageProcessor = mockk()
        sqsProperties = SqsProperties(
            region = "us-east-1",
            endpoint = "http://localhost:4566",
            maxNumberOfMessages = 1,
            waitTimeSeconds = 1
        )
        consumer = SqsConsumer(sqsClient, messageProcessor, sqsProperties, "queue-url")
    }

    @Test
    fun `start should only run once and launch processors`() = runBlocking {
        // You can use spyK to verify launch is called, or check isRunning state
        consumer.start()
        consumer.start() // should not start again
        // Assert isRunning is true, job is active, etc.
    }

    @Test
    fun `stop should cancel job and shutdown executor`() {
        consumer.start()
        consumer.stop()
        // Assert job is cancelled, executor is shutdown, etc.
    }

//    @Test
//    fun `should process and delete message on success`() = runBlocking {
//        val message = SqsMessage.builder().receiptHandle("abc").build()
//        coEvery { messageProcessor.processMessage(any()) } returns true
//        every { sqsClient.deleteMessage(any()) } returns CompletableFuture.completedFuture(DeleteMessageResponse.builder().build())
//
//        // Use reflection or a test-only method to call launchProcessor/messageChannel.send
//        // Or, refactor SqsConsumer for better testability (e.g. expose messageChannel for tests)
//    }
//
//    @Test
//    fun `should log error and not delete message on failure`() = runBlocking {
//        val message = Message.builder().receiptHandle("abc").build()
//        coEvery { messageProcessor.processMessage(any()) } throws RuntimeException("fail")
//        // Test that error is logged, deleteMessage is not called
//    }
//
//    @Test
//    fun `deleteMessage should call SQS delete API`() = runBlocking {
//        val message = Message.builder().receiptHandle("abc").build()
//        every { sqsClient.deleteMessage(any()) } returns CompletableFuture.completedFuture(DeleteMessageResponse.builder().build())
//        consumer.javaClass.getDeclaredMethod("deleteMessage", Message::class.java)
//            .apply { isAccessible = true }
//            .invoke(consumer, message)
//        verify { sqsClient.deleteMessage(any()) }
//    }
}

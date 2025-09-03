package com.github.sanmoo.ddd.synchronizer.messaging

import com.github.sanmoo.ddd.synchronizer.config.SqsProperties
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import selfie.SelfieSettings.Companion.expectSelfie
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.services.sqs.model.Message as SqsMessage

@ExtendWith(MockKExtension::class)
class SqsConsumerTest {
    @MockK
    private lateinit var sqsClient: SqsAsyncClient

    @MockK
    private lateinit var messageProcessor: MessageProcessor

    private lateinit var sqsProperties: SqsProperties
    private lateinit var sut: SqsConsumer

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
    }

    @AfterEach
    fun tearDown() {
        sut.stop()
    }

    private fun createMessage(id: String): CompletableFuture<ReceiveMessageResponse> {
        return CompletableFuture.completedFuture(
            ReceiveMessageResponse.builder()
                .messages(listOf(SqsMessage.builder().messageId(id).receiptHandle(id).build()))
                .build()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @Test
    fun `processes messages and deletes them from SQS`() = runTest {
        // Arrange
        val listOfProcessedMessages = mutableListOf<SqsMessage>()
        val responses = listOf(
            createMessage("1"), createMessage("2"), createMessage("3"), createMessage("4"), createMessage("5")
        )
        every { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) } returnsMany responses andThenJust Awaits
        every { sqsClient.deleteMessage(any<DeleteMessageRequest>()) } returns CompletableFuture.completedFuture(null)
        coEvery { messageProcessor.processMessage(capture(listOfProcessedMessages)) } returns true

        // Act
        launch(Dispatchers.Default) {
            sut = SqsConsumer(sqsClient, messageProcessor, sqsProperties, "queue-url", this.coroutineContext)
            sut.start()
        }

        coVerify(timeout = 100, exactly = 5) { messageProcessor.processMessage(any<SqsMessage>()) }
        listOfProcessedMessages.sortWith(Comparator { a, b -> a.messageId().compareTo(b.messageId()) })
        expectSelfie(listOfProcessedMessages.toString()).toMatchDisk()
    }

    @Test
    fun `starts and stops once`() = runTest {
        sut = SqsConsumer(sqsClient, messageProcessor, sqsProperties, "queue-url", this.coroutineContext)
        sut.start()
        sut.start()
        sut.stop()
        sut.stop()
    }

    @Test
    fun `should log error and not delete message on failure`() = runTest {
        every { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) } returns createMessage("1") andThenJust Awaits
        coEvery { messageProcessor.processMessage(any()) } throws RuntimeException("fail")

        launch(Dispatchers.Default) {
            sut = SqsConsumer(sqsClient, messageProcessor, sqsProperties, "queue-url", this.coroutineContext)
            sut.start()
        }

        coVerify(timeout = 100, exactly = 2) { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) }
        coVerify(exactly = 0) { sqsClient.deleteMessage(any<DeleteMessageRequest>()) }
    }

    @Test
    fun `should not delete message if processing is not successful`() = runTest {
        every { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) } returns createMessage("1") andThenJust Awaits
        coEvery { messageProcessor.processMessage(any()) } returns false

        launch(Dispatchers.Default) {
            sut = SqsConsumer(sqsClient, messageProcessor, sqsProperties, "queue-url", this.coroutineContext)
            sut.start()
        }

        coVerify(timeout = 100, exactly = 2) { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) }
        coVerify(exactly = 0) { sqsClient.deleteMessage(any<DeleteMessageRequest>()) }
    }

    @Test
    fun `should try again if there is an error while polling`() = runTest {
        every { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) } throws RuntimeException("fail")

        sut = SqsConsumer(sqsClient, messageProcessor, sqsProperties, "queue-url", this.coroutineContext)
        sut.start()

        launch(Dispatchers.Default) {
            coVerify(timeout = 500, atLeast = 2) { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) }
            sut.stop()
        }
    }

    @Test
    fun `just moves on when can't delete a message`() = runTest {
        // Arrange
        val listOfProcessedMessages = mutableListOf<SqsMessage>()
        val responses = listOf(createMessage("1"))
        every { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) } returnsMany responses andThenJust Awaits
        every { sqsClient.deleteMessage(any<DeleteMessageRequest>()) } throws RuntimeException("fail")
        coEvery { messageProcessor.processMessage(capture(listOfProcessedMessages)) } returns true

        // Act
        launch(Dispatchers.Default) {
            sut = SqsConsumer(sqsClient, messageProcessor, sqsProperties, "queue-url", this.coroutineContext)
            sut.start()
        }

        // Arrange
        coVerify(timeout = 100, exactly = 1) { sqsClient.deleteMessage(any<DeleteMessageRequest>()) }
    }

    @Test
    fun `stops polling when stopped`() = runTest {
        // Arrange
        every { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) } returns createMessage("1")
        every { sqsClient.deleteMessage(any<DeleteMessageRequest>()) } returns CompletableFuture.completedFuture(null)
        coEvery { messageProcessor.processMessage(any()) } returns true
        launch(Dispatchers.Default) {
            sut = SqsConsumer(sqsClient, messageProcessor, sqsProperties, "queue-url", this.coroutineContext)
            sut.start()
        }
        // Waits for at least one message received
        coVerify(timeout = 100, atLeast = 1) { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) }

        // Act
        every { sqsClient.receiveMessage(any<ReceiveMessageRequest>()) } throws RuntimeException("should not throw this")
        sut.stop()

        // Assert If no error is thrown, it means receiveMessage is not called again, which means the consumer is stopped
    }
}

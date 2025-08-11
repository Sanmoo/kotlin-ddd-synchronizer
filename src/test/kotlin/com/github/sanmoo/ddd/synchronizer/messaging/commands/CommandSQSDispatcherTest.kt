package com.github.sanmoo.ddd.synchronizer.messaging.commands

import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import selfie.SelfieSettings.Companion.expectSelfie
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.util.concurrent.CompletableFuture
import kotlin.test.Test

class CommandSQSDispatcherTest {
    private lateinit var sqsClient: SqsAsyncClient
    private lateinit var sut: CommandSQSDispatcher
    private val clock = Clock.fixed(OffsetDateTime.parse("2023-06-01T00:00:00.000Z").toInstant(), UTC)
    private var sqsRequestSlot = mutableListOf<SendMessageRequest>()

    @BeforeEach
    fun setUp() {
        sqsClient = mockk<SqsAsyncClient>()
        sut = CommandSQSDispatcher(sqsClient, "my-queue-url")
    }

    @Test
    fun testDispatch() {
        // Arrange
        every { sqsClient.sendMessage(capture(sqsRequestSlot)) } returns CompletableFuture.completedFuture(mockk())
        val commandList = listOf(
            CreateResourceAUpstream(
                createdAt = OffsetDateTime.now(clock),
                aggregateId = "asdf",
                id = "uuid",
                resourceA = ResourceA("id", "testingname")
            ),
            UpdateResourceADownstream(
                createdAt = OffsetDateTime.now(clock),
                aggregateId = "asdf",
                id = "uuid",
                resourceA = ResourceA("id", "testingname")
            )
        )

        // Act
        sut.dispatch(commandList)

        // Assert
        for (i in 1..sqsRequestSlot.size) {
            expectSelfie(sqsRequestSlot[i - 1].toString()).toMatchDisk("request $i")
        }
    }
}
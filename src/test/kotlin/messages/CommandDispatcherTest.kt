package messages

import builders.EventsBuilder.Companion.buildResourceACreatedEvent
import builders.EventsBuilder.Companion.buildResourceAUpdatedEvent
import builders.EventsBuilder.Companion.buildResourceBCreatedEvent
import com.diffplug.selfie.Selfie.expectSelfie
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.sanmoo.messages.*
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifyAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.util.concurrent.CompletableFuture

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class CommandDispatcherTest {
    @MockK
    private lateinit var sqsClient: SqsAsyncClient
    private lateinit var sut: CommandDispatcher
    private val messageSent = slot<SendMessageRequest>()

    @BeforeEach
    fun setUp() {
        val objectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
        sut = CommandDispatcher(sqsClient, objectMapper)
        every { sqsClient.sendMessage(capture(messageSent)) } returns CompletableFuture.completedFuture(mockk())
    }

    @AfterEach
    fun tearDown() {
        verifyAll { sqsClient.sendMessage(messageSent.captured) }
    }

    @Test
    fun dispatchCreateResourceADownstreamCommand() {
        sut.dispatch(
            CreateResourceADownstreamCommand(buildResourceACreatedEvent()),
            CommandRecipient.AGGREGATE_A_COMMANDS_INBOX
        )

        expectSelfie(messageSent.captured.toString()).toBe("SendMessageRequest(QueueUrl=aggregate_a_commands_inbox, MessageBody={\"event\":{\"type\":\"resource.a.created\",\"id\":\"123\",\"createdAt\":[2023,6,1,0,0],\"createdFromSystem\":\"UPSTREAM_SYSTEM\",\"eventId\":\"123\",\"type\":\"resource.a.created\"}}, MessageDeduplicationId=123, MessageGroupId=123)")
    }

    @Test
    fun dispatchUpdateResourceADownstreamCommand() {
        sut.dispatch(
            UpdateResourceADownstreamCommand(buildResourceAUpdatedEvent()), CommandRecipient
                .AGGREGATE_A_COMMANDS_INBOX
        )

        expectSelfie(messageSent.captured.toString()).toBe("SendMessageRequest(QueueUrl=aggregate_a_commands_inbox, MessageBody={\"event\":{\"type\":\"resource.a.updated\",\"id\":\"123\",\"updatedAt\":[2023,6,1,0,0],\"createdFromSystem\":\"UPSTREAM_SYSTEM\",\"eventId\":\"123\",\"type\":\"resource.a.updated\"}}, MessageDeduplicationId=123, MessageGroupId=123)")
    }

    @Test
    fun dispatchCreateResourceBDownstreamCommand() {
        sut.dispatch(
            CreateResourceBDownstreamCommand(buildResourceBCreatedEvent()),
            CommandRecipient.AGGREGATE_B_COMMANDS_INBOX
        )

        expectSelfie(messageSent.captured.toString()).toBe("SendMessageRequest(QueueUrl=aggregate_b_commands_inbox, MessageBody={\"event\":{\"type\":\"resource.b.created\",\"id\":\"123\",\"createdAt\":[2023,6,1,0,0],\"createdFromSystem\":\"UPSTREAM_SYSTEM\",\"eventId\":\"123\",\"type\":\"resource.b.created\"}}, MessageDeduplicationId=123, MessageGroupId=123)")
    }
}
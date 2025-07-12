package consumers

import com.diffplug.selfie.Selfie.expectSelfie
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.sanmoo.consumers.EventProcessor
import com.github.sanmoo.messages.CommandDispatcher
import com.github.sanmoo.messages.CreateResourceADownstreamCommand
import com.github.sanmoo.messages.CreateResourceBDownstreamCommand
import com.github.sanmoo.messages.UpdateResourceADownstreamCommand
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class EventProcessorTest {
    @MockK
    private lateinit var commandDispatcher: CommandDispatcher

    private lateinit var sut: EventProcessor

    @BeforeEach
    fun setUp() {
        sut = EventProcessor(
            ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule()),
            commandDispatcher
        )
    }

    @Test
    fun processEventResourceACreated() {
        val slotForCapturedCommand = slot<CreateResourceADownstreamCommand>()
        every { commandDispatcher.dispatch(capture(slotForCapturedCommand)) } just Runs

        sut.process(
            """
            {
                "type": "resource.a.created",
                "id": "123",
                "createdAt": "2023-06-01T00:00:00Z",
                "createdFromSystem": "UPSTREAM_SYSTEM"
            }
        """.trimIndent()
        )
        verifyAll { commandDispatcher.dispatch(any<CreateResourceADownstreamCommand>()) }
        expectSelfie(slotForCapturedCommand.captured.toString()).toBe("CreateResourceADownstreamCommand(event=ResourceACreatedEvent(id=123, createdAt=2023-06-01T00:00, createdFromSystem=UPSTREAM_SYSTEM))")
    }

    @Test
    fun processEventResourceAUpdated() {
        val slotForCapturedCommand = slot<UpdateResourceADownstreamCommand>()
        every { commandDispatcher.dispatch(capture(slotForCapturedCommand)) } just Runs

        sut.process(
            """
            {
                "type": "resource.a.updated",
                "id": "123",
                "updatedAt": "2023-06-01T00:00:00Z",
                "createdFromSystem": "UPSTREAM_SYSTEM"
            }
        """.trimIndent()
        )

        verifyAll { commandDispatcher.dispatch(any<UpdateResourceADownstreamCommand>()) }
        expectSelfie(slotForCapturedCommand.captured.toString()).toBe("UpdateResourceADownstreamCommand(event=ResourceAUpdatedEvent(id=123, updatedAt=2023-06-01T00:00, createdFromSystem=UPSTREAM_SYSTEM))")
    }

    @Test
    fun processEventResourceBCreated() {
        val slotForCapturedCommand = slot<CreateResourceBDownstreamCommand>()
        every { commandDispatcher.dispatch(capture(slotForCapturedCommand)) } just Runs

        sut.process(
            """
            {
                "type": "resource.b.created",
                "id": "123",
                "createdAt": "2023-06-01T00:00:00Z",
                "createdFromSystem": "UPSTREAM_SYSTEM"
            }
        """.trimIndent()
        )

        verifyAll { commandDispatcher.dispatch(any<CreateResourceBDownstreamCommand>()) }
        expectSelfie(slotForCapturedCommand.captured.toString()).toBe("CreateResourceBDownstreamCommand(event=ResourceBCreatedEvent(id=123, createdAt=2023-06-01T00:00, createdFromSystem=UPSTREAM_SYSTEM))")
    }

    @Test
    fun processUnknownEvent() {
        sut.process(
            """
            {
                "type": "resource.c.created",
                "id": "123",
                "createdAt": "2023-06-01T00:00:00Z",
                "createdFromSystem": "UPSTREAM_SYSTEM"
            }
        """.trimIndent()
        )

        verify { commandDispatcher wasNot called }
    }

    @Test
    fun discardEventOriginatedInDownstream() {
        sut.process(
            """
            {
                "type": "resource.a.created",
                "id": "123",
                "createdAt": "2023-06-01T00:00:00Z",
                "createdFromSystem": "DOWNSTREAM_SYSTEM"
            }
        """.trimIndent()
        )

        verify { commandDispatcher wasNot called }
    }
}
package messages

import com.diffplug.selfie.Selfie.expectSelfie
import com.github.sanmoo.messages.CreateResourceADownstreamCommand
import com.github.sanmoo.messages.Message
import com.github.sanmoo.messages.ResourceACreatedEvent
import com.github.sanmoo.util.StandardObjectMapper
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class MessageTest {
    @Test
    fun `serializes and de-serializes a message correctly`() {
        val event = ResourceACreatedEvent("123", LocalDateTime.parse("2023-06-01T00:00:00"), "UPSTREAM_SYSTEM", "123")
        val json = StandardObjectMapper.INSTANCE.writeValueAsString(event)
        expectSelfie(json).toMatchDisk()
        val deserializedMessageAsJson = StandardObjectMapper.INSTANCE.readTree(json)
        val message = Message.from(deserializedMessageAsJson)
        assertEquals(event, message)
    }

    @Test
    fun `serializes and de-serializes a command correctly`() {
        val event = CreateResourceADownstreamCommand(
            commandId = "123",
            event = ResourceACreatedEvent("123", LocalDateTime.parse("2023-06-01T00:00:00"), "UPSTREAM_SYSTEM", "123")
        )

        val json = StandardObjectMapper.INSTANCE.writeValueAsString(event)
        expectSelfie(json).toMatchDisk()

        val deserializedMessageAsJson = StandardObjectMapper.INSTANCE.readTree(json)
        val command = Message.from(deserializedMessageAsJson)
        assertEquals(event, command)
    }
}
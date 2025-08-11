package com.github.sanmoo.ddd.synchronizer.messaging.events

import com.github.sanmoo.ddd.synchronizer.messaging.Message
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import selfie.SelfieSettings.Companion.expectSelfie
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EventTest {
    companion object {
        @JvmStatic
        fun testData(): List<Arguments> {
            return listOf(
                arguments("resource-a-created-downstream", this.buildEventJson("resource.a.created.downstream")),
                arguments("resource-a-created-upstream", this.buildEventJson("resource.a.created.upstream")),
                arguments("resource-a-updated-downstream", this.buildEventJson("resource.a.updated.downstream")),
                arguments("resource-a-updated-upstream", this.buildEventJson("resource.a.updated.upstream"))
            )
        }

        fun buildEventJson(type: String): String {
            return """
                {
                    "type": "event",
                    "id": "abc",
                    "aggregateId": "123",
                    "createdAt": "2023-06-01T00:00:00.000Z",
                    "origination": "system-whatever",
                    "event": {
                        "type": "$type",
                        "data": {
                            "id": "123",
                            "name": "A"
                        }
                    }
                }
            """.trimIndent()
        }
    }

    @ParameterizedTest
    @MethodSource("testData")
    fun testFrom(scenarioDescription: String, json: String) {
        val message = Message.from(StandardObjectMapper.INSTANCE.readTree(json))
        expectSelfie(message).toMatchDisk(scenarioDescription)
    }

    @Test
    fun testFromWhenEventTypeIsUnknown() {
        val exception = assertFailsWith<Exception> {
            Message.from(StandardObjectMapper.INSTANCE.readTree(buildEventJson("unknown")))
        }

        assertEquals("Unknown event type: unknown", exception.message)
    }
}
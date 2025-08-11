package com.github.sanmoo.ddd.synchronizer.messaging.commands

import com.github.sanmoo.ddd.synchronizer.messaging.Message
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import selfie.SelfieSettings.Companion.expectSelfie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CommandTest {
    companion object {
        @JvmStatic
        fun testData(): List<Arguments> {
            return listOf(
                arguments("create-resource-a-upstream", this.buildCommandJson("create.resource.a.upstream")),
                arguments("update-resource-a-upstream", this.buildCommandJson("update.resource.a.upstream")),
                arguments("create-resource-a-downstream", this.buildCommandJson("create.resource.a.downstream")),
                arguments("update-resource-a-downstream", this.buildCommandJson("update.resource.a.downstream"))
            )
        }

        fun buildCommandJson(type: String): String {
            return """
                {
                    "type": "command",
                    "id": "abc",
                    "aggregateId": "123",
                    "createdAt": "2023-06-01T00:00:00.000Z",
                    "command": {
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
            Message.from(StandardObjectMapper.INSTANCE.readTree(buildCommandJson("unknown")))
        }

        assertEquals("Unknown command type: unknown", exception.message)
    }
}
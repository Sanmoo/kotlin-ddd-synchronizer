package com.github.sanmoo.ddd.synchronizer.messaging.commands

import com.github.sanmoo.ddd.synchronizer.messaging.Message
import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import selfie.SelfieSettings.Companion.expectSelfie
import java.time.OffsetDateTime
import kotlin.test.Test

class CreateResourceAUpstreamTest {
    @Test
    fun testFrom() {
        val json = """
                    {
                        "type": "command",
                        "id": "abc",
                        "aggregateId": "123",
                        "createdAt": "2023-06-01T00:00:00.000Z",
                        "command": {
                            "type": "create.resource.a.upstream",
                            "data": {
                                "id": "123",
                                "name": "A"
                            }
                        }
                    }
                    """.trimIndent()
        val objectNode = StandardObjectMapper.INSTANCE.readTree(json)
        expectSelfie(Message.from(objectNode)).toMatchDisk()
    }

    @Test
    fun testToJsonNode() {
        val command = CreateResourceAUpstream(
            createdAt = OffsetDateTime.parse("2023-06-01T00:00:00.000Z"),
            aggregateId = "123",
            id = "abc",
            resourceA = ResourceA("123", "A")
        )
        expectSelfie(command.toObjectNode()).toMatchDisk()
    }
}
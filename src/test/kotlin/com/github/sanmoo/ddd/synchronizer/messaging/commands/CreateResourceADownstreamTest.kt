package com.github.sanmoo.ddd.synchronizer.messaging.commands

import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import selfie.SelfieSettings.Companion.expectSelfie
import java.time.OffsetDateTime
import kotlin.test.Test

class CreateResourceADownstreamTest {
    @Test
    fun testToObjectNode() {
        val command = CreateResourceADownstream(
            createdAt = OffsetDateTime.parse("2023-06-01T00:00:00.000Z"),
            aggregateId = "123",
            id = "abc",
            resourceA = ResourceA("123", "A")
        )
        expectSelfie(command.toObjectNode()).toMatchDisk()
    }
}

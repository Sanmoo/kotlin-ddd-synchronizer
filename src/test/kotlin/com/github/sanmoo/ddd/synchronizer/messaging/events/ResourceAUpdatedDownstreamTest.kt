package com.github.sanmoo.ddd.synchronizer.messaging.events

import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import org.junit.jupiter.api.Test
import selfie.SelfieSettings.Companion.expectSelfie
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC

class ResourceAUpdatedDownstreamTest {
    private val clock = Clock.fixed(
        OffsetDateTime.parse("2023-06-01T00:00:00.000Z").toInstant
            (), UTC
    )

    @Test
    fun testToCommandList() {
        val event = ResourceAUpdatedDownstream(
            createdAt = OffsetDateTime.now(clock),
            aggregateId = "123",
            id = "abc",
            origination = "downstream-system",
            resourceA = ResourceA("123", "A")
        )

        val commandList = event.toCommandList(clock) { "uuid" }

        for (i in 1..commandList.size) {
            expectSelfie(commandList[i - 1]).toMatchDisk("command $i")
        }
    }
}
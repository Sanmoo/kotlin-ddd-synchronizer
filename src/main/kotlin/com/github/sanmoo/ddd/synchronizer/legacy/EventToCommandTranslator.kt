package com.github.sanmoo.ddd.synchronizer.legacy

import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.sanmoo.ddd.synchronizer.messaging.commands.Command
import com.github.sanmoo.ddd.synchronizer.messaging.commands.CreateResourceAUpstream
import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import java.time.Clock
import java.time.OffsetDateTime

class EventToCommandTranslator(val clock: Clock, val uuidProvider: () -> String) {
    fun translate(event: ObjectNode): Command {
        val type = event.get("type").textValue()
        val eventData = event.get("data")

        if ("resource.a.created.downstream" == type) {
            val aggregateId = "${eventData.get("id").textValue()}"

            val command = CreateResourceAUpstream(
                createdAt = OffsetDateTime.now(clock),
                aggregateId = aggregateId,
                id = uuidProvider(),
                resourceA = StandardObjectMapper.INSTANCE.treeToValue(eventData, ResourceA::class.java)
            )

            return command
        } else {
            throw Exception("Unknown event type: ${event.get("type").textValue()}")
        }
    }
}
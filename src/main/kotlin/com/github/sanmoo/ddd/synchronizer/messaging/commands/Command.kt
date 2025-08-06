package com.github.sanmoo.ddd.synchronizer.messaging.commands

import com.fasterxml.jackson.databind.JsonNode
import com.github.sanmoo.ddd.synchronizer.messaging.Message
import com.github.sanmoo.ddd.synchronizer.messaging.events.ResourceACreatedUpstream
import java.time.OffsetDateTime

open class Command(
    override val createdAt: OffsetDateTime,
    override val aggregateId: String
) : Message(aggregateId, createdAt) {
    companion object {
        fun from(createdAt: OffsetDateTime, aggregateId: String, node: JsonNode): Command {
            return when (node.get("type").textValue()) {
                "create.resource.a.downstream" -> CreateResourceADownstream.from(
                    aggregateId = aggregateId,
                    createdAt = createdAt,
                    node = node
                )
                else -> throw Exception("Unknown event type: ${node.get("type").textValue()}")
            }
        }
    }
}

package com.github.sanmoo.ddd.synchronizer.messaging.events

import com.fasterxml.jackson.databind.JsonNode
import com.github.sanmoo.ddd.synchronizer.messaging.Message
import java.time.OffsetDateTime

open class Event(
    override val createdAt: OffsetDateTime,
    override val aggregateId: String,
    open val origination: String,
    open val eventId: String,
) : Message(aggregateId, createdAt) {
    companion object {
        fun from(createdAt: OffsetDateTime, aggregateId: String, origination: String, eventId: String, node: JsonNode):
                Event {
            return when (node.get("type").textValue()) {
                "resource.a.created.upstream" -> ResourceACreatedUpstream.from(
                    aggregateId = aggregateId,
                    createdAt = createdAt,
                    node = node,
                    origination = origination,
                    eventId = eventId
                )

                else -> throw Exception("Unknown event type: ${node.get("type").textValue()}")
            }
        }
    }
}

package com.github.sanmoo.ddd.synchronizer.messaging.events

import com.fasterxml.jackson.databind.JsonNode
import com.github.sanmoo.ddd.synchronizer.messaging.Message
import java.time.OffsetDateTime

open class Event(
    override val createdAt: OffsetDateTime,
    override val aggregateId: String,
    open val origination: String,
    override val id: String,
) : Message(aggregateId, createdAt, id) {
    companion object {
        fun from(
            createdAt: OffsetDateTime,
            aggregateId: String,
            origination: String,
            id: String,
            node: JsonNode
        ): Event {
            return when (node.get("type").textValue()) {
                "resource.a.created.upstream" -> ResourceACreatedUpstream.from(
                    aggregateId = aggregateId,
                    createdAt = createdAt,
                    node = node,
                    origination = origination,
                    id = id
                )

                else -> throw Exception("Unknown event type: ${node.get("type").textValue()}")
            }
        }
    }
}

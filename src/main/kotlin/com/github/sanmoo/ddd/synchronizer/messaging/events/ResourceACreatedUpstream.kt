package com.github.sanmoo.ddd.synchronizer.messaging.events

import com.fasterxml.jackson.databind.JsonNode
import java.time.OffsetDateTime

data class ResourceACreatedUpstream(
    override val createdAt: OffsetDateTime,
    override val aggregateId: String,
    override val origination: String,
    override val eventId: String,
    val id: String,
    val name: String,
) : Event(createdAt, aggregateId, origination, eventId) {
    companion object {
        fun from(createdAt: OffsetDateTime, aggregateId: String, node: JsonNode, origination: String, eventId: String):
                ResourceACreatedUpstream =
            ResourceACreatedUpstream(
                createdAt = createdAt,
                aggregateId = aggregateId,
                origination = origination,
                eventId = eventId,
                id = node.get("data").get("id").textValue(),
                name = node.get("data").get("name").textValue()
            )
    }
}
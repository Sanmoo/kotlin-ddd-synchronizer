package com.github.sanmoo.ddd.synchronizer.messaging.commands

import com.fasterxml.jackson.databind.JsonNode
import java.time.OffsetDateTime

data class CreateResourceADownstream(
    override val createdAt: OffsetDateTime,
    override val aggregateId: String,
    val id: String,
    val name: String
) : Command(createdAt, aggregateId) {
    companion object {
        fun from(createdAt: OffsetDateTime, aggregateId: String, node: JsonNode): CreateResourceADownstream = CreateResourceADownstream(
            createdAt = createdAt,
            aggregateId = aggregateId,
            id = node.get("id").textValue(),
            name = node.get("name").textValue()
        )
    }
}
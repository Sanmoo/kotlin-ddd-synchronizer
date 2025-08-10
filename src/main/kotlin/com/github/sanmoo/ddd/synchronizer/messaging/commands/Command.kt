package com.github.sanmoo.ddd.synchronizer.messaging.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.sanmoo.ddd.synchronizer.messaging.Message
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import java.time.OffsetDateTime

open class Command(
    override val createdAt: OffsetDateTime,
    override val aggregateId: String,
    override val id: String,
) : Message(aggregateId, createdAt, id) {
    companion object {
        fun from(createdAt: OffsetDateTime, aggregateId: String, id: String, node: JsonNode): Command {
            return when (node.get("type").textValue()) {
                CREATE_RESOURCE_A_DOWNSTREAM -> CreateResourceADownstream.from(
                    aggregateId = aggregateId,
                    createdAt = createdAt,
                    id = id,
                    node = node
                )
                CREATE_RESOURCE_A_UPSTREAM -> CreateResourceAUpstream.from(
                    aggregateId = aggregateId,
                    createdAt = createdAt,
                    id = id,
                    node = node
                )
                else -> throw Exception("Unknown event type: ${node.get("type").textValue()}")
            }
        }
    }

    override fun toObjectNode(): ObjectNode {
        val jsonNode = super.toObjectNode()
        jsonNode.put("type", "command")
        jsonNode.set<JsonNode>("command", StandardObjectMapper.INSTANCE.createObjectNode())
        return jsonNode
    }
}

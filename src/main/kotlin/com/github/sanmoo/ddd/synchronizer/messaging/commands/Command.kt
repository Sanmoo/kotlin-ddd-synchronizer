package com.github.sanmoo.ddd.synchronizer.messaging.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.sanmoo.ddd.synchronizer.messaging.CREATE_RESOURCE_A_DOWNSTREAM
import com.github.sanmoo.ddd.synchronizer.messaging.CREATE_RESOURCE_A_UPSTREAM
import com.github.sanmoo.ddd.synchronizer.messaging.Message
import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import java.time.OffsetDateTime
import kotlin.jvm.java

abstract class Command(
    override val createdAt: OffsetDateTime,
    override val aggregateId: String,
    override val id: String,
) : Message(aggregateId, createdAt, id) {
    companion object {
        fun from(createdAt: OffsetDateTime, aggregateId: String, id: String, node: JsonNode): Command {
            return when (node.get("type").textValue()) {
                CREATE_RESOURCE_A_DOWNSTREAM -> CreateResourceADownstream(
                    aggregateId = aggregateId,
                    createdAt = createdAt,
                    id = id,
                    resourceA = StandardObjectMapper.INSTANCE.treeToValue(node.get("data"), ResourceA::class.java)
                )

                CREATE_RESOURCE_A_UPSTREAM -> CreateResourceAUpstream(
                    aggregateId = aggregateId,
                    createdAt = createdAt,
                    id = id,
                    resourceA = StandardObjectMapper.INSTANCE.treeToValue(node.get("data"), ResourceA::class.java)
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

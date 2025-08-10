package com.github.sanmoo.ddd.synchronizer.messaging.commands

import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import java.time.OffsetDateTime

data class CreateResourceAUpstream(
    override val createdAt: OffsetDateTime,
    override val aggregateId: String,
    override val id: String,
    val resourceA: ResourceA,
) : Command(createdAt, aggregateId, id) {
    override fun toObjectNode(): ObjectNode {
        val jsonNode = super.toObjectNode()
        val commandNode: ObjectNode = jsonNode.get("command") as ObjectNode
        commandNode.put("type", CREATE_RESOURCE_A_UPSTREAM)
        commandNode.putPOJO("data", resourceA)
        return jsonNode
    }
}
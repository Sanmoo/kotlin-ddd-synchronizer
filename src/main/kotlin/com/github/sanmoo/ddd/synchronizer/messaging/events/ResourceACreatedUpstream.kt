package com.github.sanmoo.ddd.synchronizer.messaging.events

import com.fasterxml.jackson.databind.JsonNode
import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import java.time.OffsetDateTime

data class ResourceACreatedUpstream(
    override val createdAt: OffsetDateTime,
    override val aggregateId: String,
    override val origination: String,
    override val id: String,
    val resourceA: ResourceA,
) : Event(createdAt, aggregateId, origination, id) {
    companion object {
        fun from(
            createdAt: OffsetDateTime,
            aggregateId: String,
            node: JsonNode,
            origination: String,
            id: String
        ): ResourceACreatedUpstream {
            return ResourceACreatedUpstream(
                createdAt = createdAt,
                aggregateId = aggregateId,
                origination = origination,
                id = id,
                resourceA = StandardObjectMapper.INSTANCE.convertValue(node.get("data"), ResourceA::class.java)
            )
        }
    }
}
package com.github.sanmoo.ddd.synchronizer.messaging.events

import com.fasterxml.jackson.databind.JsonNode
import com.github.sanmoo.ddd.synchronizer.messaging.Message
import com.github.sanmoo.ddd.synchronizer.messaging.commands.Command
import com.github.sanmoo.ddd.synchronizer.messaging.RESOURCE_A_CREATED_DOWNSTREAM
import com.github.sanmoo.ddd.synchronizer.messaging.RESOURCE_A_CREATED_UPSTREAM
import com.github.sanmoo.ddd.synchronizer.messaging.RESOURCE_A_UPDATED_DOWNSTREAM
import com.github.sanmoo.ddd.synchronizer.messaging.RESOURCE_A_UPDATED_UPSTREAM
import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import java.time.Clock
import java.time.OffsetDateTime

abstract class Event(
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
                RESOURCE_A_CREATED_UPSTREAM -> ResourceACreatedUpstream(
                    aggregateId = aggregateId,
                    createdAt = createdAt,
                    origination = origination,
                    id = id,
                    resourceA = StandardObjectMapper.INSTANCE.convertValue(node.get("data"), ResourceA::class.java)
                )

                RESOURCE_A_CREATED_DOWNSTREAM -> ResourceACreatedDownstream(
                    aggregateId = aggregateId,
                    createdAt = createdAt,
                    origination = origination,
                    id = id,
                    resourceA = StandardObjectMapper.INSTANCE.convertValue(node.get("data"), ResourceA::class.java)
                )

                RESOURCE_A_UPDATED_DOWNSTREAM -> ResourceAUpdatedDownstream(
                    aggregateId = aggregateId,
                    createdAt = createdAt,
                    origination = origination,
                    id = id,
                    resourceA = StandardObjectMapper.INSTANCE.convertValue(node.get("data"), ResourceA::class.java)
                )

                RESOURCE_A_UPDATED_UPSTREAM -> ResourceAUpdatedUpstream(
                    aggregateId = aggregateId,
                    createdAt = createdAt,
                    origination = origination,
                    id = id,
                    resourceA = StandardObjectMapper.INSTANCE.convertValue(node.get("data"), ResourceA::class.java)
                )

                else -> throw Exception("Unknown event type: ${node.get("type").textValue()}")
            }
        }
    }

    // Events are translated to a list of commands that the integrator is expected to process
    abstract fun toCommandList(clock: Clock, uuidProvider: () -> String): List<Command>
}

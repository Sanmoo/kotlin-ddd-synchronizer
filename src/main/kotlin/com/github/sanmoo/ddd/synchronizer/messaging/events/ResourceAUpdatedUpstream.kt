package com.github.sanmoo.ddd.synchronizer.messaging.events

import com.github.sanmoo.ddd.synchronizer.messaging.commands.Command
import com.github.sanmoo.ddd.synchronizer.messaging.commands.CreateResourceADownstream
import com.github.sanmoo.ddd.synchronizer.messaging.commands.UpdateResourceADownstream
import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.OffsetDateTime

data class ResourceAUpdatedUpstream(
    override val createdAt: OffsetDateTime,
    override val aggregateId: String,
    override val origination: String,
    override val id: String,
    val resourceA: ResourceA,
) : Event(createdAt, aggregateId, origination, id) {
    private val logger = LoggerFactory.getLogger(ResourceAUpdatedUpstream::class.java)

    override fun toCommandList(clock: Clock, uuidProvider: () -> String): List<Command> {
        if (origination == "downstream-system") {
            logger.info("Event $id is originated from upstream-system, no need to replicate down then")
            return emptyList()
        }

        return listOf(UpdateResourceADownstream(
            createdAt = OffsetDateTime.now(clock),
            aggregateId = aggregateId,
            id = uuidProvider(),
            resourceA = resourceA
        ))
    }
}
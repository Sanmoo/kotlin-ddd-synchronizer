package com.github.sanmoo.ddd.synchronizer.messaging.events

import com.github.sanmoo.ddd.synchronizer.messaging.commands.Command
import com.github.sanmoo.ddd.synchronizer.messaging.commands.CreateResourceAUpstream
import com.github.sanmoo.ddd.synchronizer.messaging.commands.UpdateResourceAUpstream
import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.OffsetDateTime

data class ResourceAUpdatedDownstream(
    override val createdAt: OffsetDateTime,
    override val aggregateId: String,
    override val origination: String,
    override val id: String,
    val resourceA: ResourceA,
) : Event(createdAt, aggregateId, origination, id) {
    private val logger = LoggerFactory.getLogger(ResourceACreatedUpstream::class.java)

    override fun toCommandList(clock: Clock, uuidProvider: () -> String): List<Command> {
        if (origination == "upstream-system") {
            logger.info("Event $id is originated from upstream-system, no need to replicate up then")
            return emptyList()
        }

        return listOf(UpdateResourceAUpstream(
            createdAt = OffsetDateTime.now(clock),
            aggregateId = aggregateId,
            id = id,
            resourceA = resourceA
        ))
    }
}

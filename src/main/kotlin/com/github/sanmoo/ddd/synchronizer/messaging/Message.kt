package com.github.sanmoo.ddd.synchronizer.messaging

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.sanmoo.ddd.synchronizer.messaging.commands.Command
import com.github.sanmoo.ddd.synchronizer.messaging.events.Event
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

open class Message(
    open val aggregateId: String,
    open val createdAt: OffsetDateTime,
    open val id: String
) {
    companion object {
        fun from(jsonNode: JsonNode): Message {
            val createdAt = OffsetDateTime.parse(jsonNode.get("createdAt").textValue(), DateTimeFormatter
                .ISO_OFFSET_DATE_TIME)
            val aggregateId = jsonNode.get("aggregateId").textValue()
            val id = jsonNode.get("id").textValue()

            when (jsonNode.get("type").textValue()) {
                "event" -> {
                    return Event.from(
                        aggregateId = aggregateId,
                        createdAt = createdAt,
                        node = jsonNode.get("event"),
                        origination = jsonNode.get("origination").textValue(),
                        id = id
                    )
                }
                "command" -> {
                    return Command.from(
                        aggregateId = aggregateId,
                        createdAt = createdAt,
                        id = id,
                        node = jsonNode.get("command")
                    )
                }
                else -> throw IllegalArgumentException("Unknown message type: ${jsonNode.get("type").textValue()}")
            }
        }
    }

    open fun toObjectNode(): ObjectNode {
        val mapper = StandardObjectMapper.INSTANCE
        val result = mapper.createObjectNode()
        result.put("id", id)
        result.put("aggregateId", aggregateId)
        result.put("createdAt", createdAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        return result
    }
}

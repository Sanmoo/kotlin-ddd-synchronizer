package com.github.sanmoo.ddd.synchronizer.messaging

import com.fasterxml.jackson.databind.JsonNode
import com.github.sanmoo.ddd.synchronizer.messaging.commands.Command
import com.github.sanmoo.ddd.synchronizer.messaging.events.Event
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

open class Message(
    open val aggregateId: String,
    open val createdAt: OffsetDateTime,
) {
    companion object {
        fun from(jsonNode: JsonNode): Message {
            val createdAt = OffsetDateTime.parse(jsonNode.get("createdAt").textValue(), DateTimeFormatter
                .ISO_OFFSET_DATE_TIME)
            val aggregateId = jsonNode.get("aggregateId").textValue()

            when (jsonNode.get("type").textValue()) {
                "event" -> {
                    return Event.from(
                        aggregateId = aggregateId,
                        createdAt = createdAt,
                        node = jsonNode.get("event"),
                        origination = jsonNode.get("origination").textValue(),
                        eventId = jsonNode.get("eventId").textValue()
                    )
                }
                "command" -> {
                    return Command.from(
                        aggregateId = aggregateId,
                        createdAt = createdAt,
                        node = jsonNode.get("command")
                    )
                }
                else -> throw IllegalArgumentException("Unknown message type: ${jsonNode.get("type").textValue()}")
            }
        }
    }
}

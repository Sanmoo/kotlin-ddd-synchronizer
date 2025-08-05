package com.github.sanmoo.ddd.synchronizer.messaging

import com.fasterxml.jackson.databind.JsonNode
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class ParsedMessage(
    val type: MessageType,
    val origination: String,
    val eventId: String,
    val aggregateId: String,
    val createdAt: OffsetDateTime,
    val event: Event
) {
    companion object {
        fun from(jsonNode: JsonNode): ParsedMessage {
            val createdAt = OffsetDateTime.parse(jsonNode.get("createdAt").textValue(), DateTimeFormatter
                .ISO_OFFSET_DATE_TIME)

            return ParsedMessage(
                type = MessageType.valueOf(jsonNode.get("type").textValue().toUpperCase()),
                origination = jsonNode.get("origination").textValue(),
                eventId = jsonNode.get("eventId").textValue(),
                aggregateId = jsonNode.get("aggregateId").textValue(),
                createdAt = createdAt,
                event = Event.from(jsonNode.get("eventData"))
            )
        }
    }
}

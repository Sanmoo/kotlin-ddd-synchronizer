package com.github.sanmoo.messages

import com.fasterxml.jackson.databind.JsonNode

sealed class Event(
    open val type: EventType,
    open val createdFromSystem: String,
    open val eventId: String
) : Message(MessageType.EVENT) {
    companion object {
        fun from(json: JsonNode): Message {
            val commandType = EventType.valueOf(json.get("type").asText())
            return commandType.buildEventFrom(json)
        }
    }
}


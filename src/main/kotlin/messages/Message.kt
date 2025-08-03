package com.github.sanmoo.messages

import com.fasterxml.jackson.databind.JsonNode

enum class MessageType {
    COMMAND,
    EVENT;
}

sealed class Message(
    val messageType: MessageType,
) {
    companion object {
        fun from(jsonNode: JsonNode): Message {
            val messageType = MessageType.valueOf(jsonNode.get("messageType").asText())

            if (messageType == MessageType.COMMAND) {
                return Command.from(jsonNode);
            }

            return Event.from(jsonNode);
        }
    }
}
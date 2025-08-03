package com.github.sanmoo.messages

import com.fasterxml.jackson.databind.JsonNode

sealed class Command(
    open val type: CommandType,
    open val commandId: String
) : Message(MessageType.COMMAND) {
    companion object {
        fun from(json: JsonNode): Message {
            val commandType = CommandType.valueOf(json.get("type").asText())
            return commandType.buildCommandFrom(json)
        }
    }
}

data class CreateResourceADownstreamCommand(
    val event: ResourceACreatedEvent,
    override val commandId: String
) : Command(CommandType.CREATE_RESOURCE_A_DOWNSTREAM_COMMAND, commandId)

data class UpdateResourceADownstreamCommand(val event: ResourceAUpdatedEvent, override val commandId: String) :
    Command(CommandType.UPDATE_RESOURCE_A_DOWNSTREAM_COMMAND, commandId)

data class CreateResourceBDownstreamCommand(val event: ResourceBCreatedEvent, override val commandId: String) :
    Command(CommandType.CREATE_RESOURCE_B_DOWNSTREAM_COMMAND, commandId)
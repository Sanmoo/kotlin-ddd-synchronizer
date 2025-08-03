package com.github.sanmoo.messages

import com.fasterxml.jackson.databind.JsonNode
import com.github.sanmoo.util.StandardObjectMapper

enum class CommandType(val buildCommandFrom: (JsonNode) -> Message) {
    CREATE_RESOURCE_A_DOWNSTREAM_COMMAND(
        fun(nodes: JsonNode): CreateResourceADownstreamCommand {
            return StandardObjectMapper.INSTANCE.readValue(
                nodes.traverse(),
                CreateResourceADownstreamCommand::class.java
            )
        }
    ),

    CREATE_RESOURCE_B_DOWNSTREAM_COMMAND(
        fun(nodes: JsonNode): CreateResourceBDownstreamCommand {
            return StandardObjectMapper.INSTANCE.readValue(
                nodes.traverse(),
                CreateResourceBDownstreamCommand::class.java
            )
        }
    ),

    UPDATE_RESOURCE_A_DOWNSTREAM_COMMAND(
        fun(nodes: JsonNode): UpdateResourceADownstreamCommand {
            return StandardObjectMapper.INSTANCE.readValue(
                nodes.traverse(),
                UpdateResourceADownstreamCommand::class.java
            )
        }
    );
}
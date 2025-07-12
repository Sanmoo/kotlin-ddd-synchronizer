package com.github.sanmoo.consumers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException
import com.github.sanmoo.messages.BaseEvent
import com.github.sanmoo.messages.CommandDispatcher
import com.github.sanmoo.messages.CommandRecipient
import com.github.sanmoo.messages.CreateResourceADownstreamCommand
import com.github.sanmoo.messages.CreateResourceBDownstreamCommand
import com.github.sanmoo.messages.ResourceACreatedEvent
import com.github.sanmoo.messages.ResourceAUpdatedEvent
import com.github.sanmoo.messages.ResourceBCreatedEvent
import com.github.sanmoo.messages.UpdateResourceADownstreamCommand
import java.util.logging.Logger

private const val DOWNSTREAM_SYSTEM_NAME = "DOWNSTREAM_SYSTEM"

class EventProcessor(
    val objectMapper: ObjectMapper,
    val commandDispatcher: CommandDispatcher
) {
    private val logger = Logger.getLogger(EventProcessor::class.java.name)

    fun process(serializedEvent: String) {
        try {
            val event = objectMapper.readValue(serializedEvent, BaseEvent::class.java)

            if (event.createdFromSystem == DOWNSTREAM_SYSTEM_NAME) {
                logger.info("Received event from downstream system. No need to propagate down")
                return
            }

            when (event) {
                is ResourceACreatedEvent -> process(event)
                is ResourceAUpdatedEvent -> process(event)
                is ResourceBCreatedEvent -> process(event)
            }
        } catch (e: InvalidTypeIdException) {
            logger.severe("Received unknown event: $serializedEvent")
            e.printStackTrace()
        }
    }

    private fun process(event: ResourceACreatedEvent) {
        commandDispatcher.dispatch(CreateResourceADownstreamCommand(event), CommandRecipient.AGGREGATE_A_COMMANDS_INBOX)
    }

    private fun process(event: ResourceAUpdatedEvent) {
        commandDispatcher.dispatch(UpdateResourceADownstreamCommand(event), CommandRecipient.AGGREGATE_A_COMMANDS_INBOX)
    }

    private fun process(event: ResourceBCreatedEvent) {
        commandDispatcher.dispatch(CreateResourceBDownstreamCommand(event), CommandRecipient.AGGREGATE_B_COMMANDS_INBOX)
    }
}
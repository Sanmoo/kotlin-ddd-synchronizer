package com.github.sanmoo.messages.handlers

import com.github.sanmoo.messages.CreateResourceADownstreamCommand
import com.github.sanmoo.messages.CreateResourceBDownstreamCommand
import com.github.sanmoo.messages.Message
import com.github.sanmoo.messages.MessageSender
import com.github.sanmoo.messages.ResourceACreatedEvent
import com.github.sanmoo.messages.ResourceAUpdatedEvent
import com.github.sanmoo.messages.ResourceBCreatedEvent
import com.github.sanmoo.messages.UpdateResourceADownstreamCommand

class MessageHandlerFactory(
    private val messageSender: MessageSender
) {
    fun createFor(message: Message): () -> Unit {
        when (message) {
            // Events
            is ResourceACreatedEvent -> return fun() {
                messageSender.send(
                    CreateResourceADownstreamCommand(message, message.id), message.eventId,
                    "resource-a-${message.id}"
                )
            }

            is ResourceAUpdatedEvent -> return fun() {
                messageSender.send(
                    UpdateResourceADownstreamCommand(message, message.id), message.eventId,
                    "resource-a-${message.id}"
                )
            }

            is ResourceBCreatedEvent -> return fun() {
                messageSender.send(
                    CreateResourceBDownstreamCommand(message, message.id), message.eventId,
                    "resource-a-${message.id}"
                )
            }

            // Commands
            is CreateResourceADownstreamCommand -> return { -> CreateResourceADownstreamCommandHandler().process(message) }
            is CreateResourceBDownstreamCommand -> return { -> CreateResourceBDownstreamCommandHandler().process(message) }
            is UpdateResourceADownstreamCommand -> return { -> UpdateResourceADownstreamCommandHandler().process(message) }
        }
    }
}
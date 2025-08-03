package com.github.sanmoo.messages

import com.github.sanmoo.messages.handlers.MessageHandlerFactory

class MessageHandler(
    private val handler: MessageSender,
    private val handlerFactory: MessageHandlerFactory
) {
    fun handle(message: Message) {
        when (message) {
            // Events
            is ResourceACreatedEvent -> println("message.handle()")
            is ResourceAUpdatedEvent -> println("message.handle()")
            is ResourceBCreatedEvent -> println("message.handle()")
            // Commands
            is CreateResourceADownstreamCommand -> println("message.handle()")
            is CreateResourceBDownstreamCommand -> println("message.handle()")
            is UpdateResourceADownstreamCommand -> println("message.handle()")
        }
    }
}
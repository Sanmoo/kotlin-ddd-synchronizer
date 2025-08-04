package com.github.sanmoo.ddd.synchronizer.messaging

import software.amazon.awssdk.services.sqs.model.Message

interface MessageProcessor {
    suspend fun processMessage(message: Message): Boolean
}

class DefaultMessageProcessor : MessageProcessor {
    /*
     * Return true to delete the message, false to keep it in the queue
     */
    override suspend fun processMessage(message: Message): Boolean {
        try {
            // TODO: Implement your message processing logic here
            println("Processing message: ${message.body()}")
            // If processing is successful, return true to delete the message
            return true
        } catch (e: Exception) {
            // Log the error and return false to keep the message in the queue
            println("Error processing message: ${e.message}")
            return false
        }
    }
}

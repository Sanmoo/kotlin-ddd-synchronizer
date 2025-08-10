package com.github.sanmoo.ddd.synchronizer.messaging

import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.model.Message

interface MessageProcessor {
    suspend fun processMessage(message: Message): Boolean
}

@Component
class DefaultMessageProcessor : MessageProcessor {
    /*
     * Return true to delete the message, false to keep it in the queue
     */
    override suspend fun processMessage(message: Message): Boolean {
        try {
            // TODO: Implement your message processing logic here
            val content = message.body()
            val jsonNode = StandardObjectMapper.INSTANCE.readTree(content)
            val parsed = com.github.sanmoo.ddd.synchronizer.messaging.Message.from(jsonNode)
            println("Message parsed: $parsed")
            // If processing is successful, return true to delete the message
            return true
        } catch (e: Exception) {
            // Log the error and return false to keep the message in the queue
            println("Error processing message: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
}

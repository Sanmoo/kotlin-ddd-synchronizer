package com.github.sanmoo.ddd.synchronizer.legacy

import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.sanmoo.ddd.synchronizer.legacy.persistency.models.OutboxMessage
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import org.javalite.activejdbc.Base
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.time.Clock
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

@Component
class OutboxProcessor(
    private val jdbcTemplate: JdbcTemplate,
    private val sqsClient: SqsAsyncClient,
    @Value("\${sqs.queue-url}")
    private val queueUrl: String
) {
    private val logger = LoggerFactory.getLogger(OutboxProcessor::class.java)
    private val isRunning = AtomicBoolean(false)
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val objectMapper = StandardObjectMapper.INSTANCE

    @EventListener(ApplicationReadyEvent::class)
    @Async
    fun start() {
        if (isRunning.compareAndSet(false, true)) {
            logger.info("Starting OutboxProcessor")
            scope.launch {
                Base.attach(jdbcTemplate.dataSource?.connection)
                while (isRunning.get()) {
                    try {
                        processOutbox()
                        delay(1000) // Poll every second
                    } catch (e: Exception) {
                        logger.error("Error in OutboxProcessor: ${e.message}", e)
                        delay(5000) // Wait 5 seconds on error before retrying
                    }
                }
            }
        }
    }

    @PreDestroy
    fun stop() {
        if (isRunning.compareAndSet(true, false)) {
            logger.info("Stopping OutboxProcessor")
            job.cancel()
        }
    }

    private suspend fun processOutbox() {
        // Use transaction to ensure we don't lose messages if processing fails
        Base.openTransaction()
        try {
            // Lock and fetch a batch of messages (limit 10 at a time)
            val messages = OutboxMessage.findBySQL("select * from outbox order by created_at asc limit 10 for update skip " +
                    "locked")

            if (messages.isNotEmpty()) {
                logger.debug("Processing ${messages.size} outbox messages")
                
                for (message in messages) {
                    try {
                        // Process the message
                        processMessage(message)
                        
                        // Delete the processed message
                        OutboxMessage.delete("id = ?", message.id)
                        logger.debug("Processed and deleted outbox message: {}", message.id)
                    } catch (e: Exception) {
                        logger.error("Failed to process outbox message ${message.id}: ${e.message}", e)
                        // Continue with next message even if one fails
                    }
                }
            }
            Base.commitTransaction()
        } catch (e: Exception) {
            Base.rollbackTransaction()
            throw e
        }
    }

    private fun processMessage(message: OutboxMessage) {
        logger.info("Processing outbox message: ${message.id} - ${message.get("event_body")}...")

        val command = EventToCommandTranslator(Clock.systemUTC()) { -> UUID::randomUUID.toString() }.translate(
            StandardObjectMapper.INSTANCE.readTree(message.get("event_body").toString()) as ObjectNode
        )

        val receiveRequest = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody(objectMapper.writeValueAsString(command.toObjectNode()))
            .messageGroupId("resource-a-${command.aggregateId}")
            .messageDeduplicationId(command.id)
            .build()

        sqsClient.sendMessage(receiveRequest).get()

        logger.info("Event ${message.id} processed")
    }
}
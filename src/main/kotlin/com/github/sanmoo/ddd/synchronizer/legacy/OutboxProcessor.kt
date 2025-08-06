package com.github.sanmoo.ddd.synchronizer.legacy

import com.github.sanmoo.ddd.synchronizer.legacy.persistency.models.Outbox
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import org.javalite.activejdbc.Base
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

@Component
class OutboxProcessor(
    private val jdbcTemplate: JdbcTemplate
) {
    private val logger = LoggerFactory.getLogger(OutboxProcessor::class.java)
    private val isRunning = AtomicBoolean(false)
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @EventListener(ApplicationReadyEvent::class)
    @Async
    fun start() {
        if (isRunning.compareAndSet(false, true)) {
            logger.info("Starting OutboxProcessor")
            scope.launch {
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
        Base.attach(jdbcTemplate.dataSource?.connection)
        jdbcTemplate.execute("BEGIN")
        try {
            // Lock and fetch a batch of messages (limit 10 at a time)
            val messages = Outbox.findAll()

//            val messages = jdbcTemplate.query(
//                """
//                SELECT id, event_body, created_at
//                FROM outbox
//                ORDER BY created_at
//                LIMIT 10
//                FOR UPDATE SKIP LOCKED
//                """.trimIndent(),
//                outboxRowMapper()
//            )

            if (messages.isNotEmpty()) {
                logger.debug("Processing ${messages.size} outbox messages")
                
                for (message in messages) {
                    try {
                        // Process the message
                        processMessage(message)
                        
                        // Delete the processed message
                        jdbcTemplate.update(
                            "DELETE FROM outbox WHERE id = ?",
                            message.id
                        )
                        logger.debug("Processed and deleted outbox message: ${message.id}")
                    } catch (e: Exception) {
                        logger.error("Failed to process outbox message ${message.id}: ${e.message}", e)
                        // Continue with next message even if one fails
                    }
                }
            }
            jdbcTemplate.execute("COMMIT")
        } catch (e: Exception) {
            jdbcTemplate.execute("ROLLBACK")
            throw e
        }
    }

    private fun processMessage(message: Outbox) {
        // TODO: Implement actual message processing logic
        // For now, just log the message
        logger.info("Processing outbox message: ${message.id} - ${message.get("event_body")}...")
        // Add your business logic here
    }
}
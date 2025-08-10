package com.github.sanmoo.ddd.synchronizer.messaging

import com.github.sanmoo.ddd.synchronizer.legacy.persistency.models.ResourceA
import com.github.sanmoo.ddd.synchronizer.messaging.commands.CommandSQSDispatcher
import com.github.sanmoo.ddd.synchronizer.messaging.commands.CreateResourceADownstream
import com.github.sanmoo.ddd.synchronizer.messaging.commands.CreateResourceAUpstream
import com.github.sanmoo.ddd.synchronizer.messaging.events.Event
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import org.javalite.activejdbc.Base
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.time.Clock
import java.util.*
import software.amazon.awssdk.services.sqs.model.Message as SqsMessage

@Component
class MessageProcessor(
    val clock: Clock,
    val commandDispatcher: CommandSQSDispatcher,
    val jdbcTemplate: JdbcTemplate
) {
    private val logger = LoggerFactory.getLogger(MessageProcessor::class.java)

    suspend fun processMessage(sqsMessage: SqsMessage): Boolean {
        try {
            val content = sqsMessage.body()
            val jsonNode = StandardObjectMapper.INSTANCE.readTree(content)
            val message = Message.from(jsonNode)

            if (message is Event) {
                commandDispatcher.dispatch(message.toCommandList(clock) { UUID.randomUUID().toString() })
            } else {
                if (message is CreateResourceAUpstream) {
                    logger.info("Create Resource A Upstream: Consuming... an API to create Resource A Upstream")
                    logger.info("Done")
                } else if (message is CreateResourceADownstream) {
                    if (!Base.hasConnection()) {
                        Base.attach(jdbcTemplate.dataSource?.connection)
                    }

                    Base.openTransaction()
                    try {
                        ResourceA.create("id", message.resourceA.id, "name", message.resourceA.name)
                        Base.commitTransaction()
                    } catch (e: Exception) {
                        Base.rollbackTransaction()
                        throw e
                    }

                    logger.info("Created Resource A Downstream successfully")
                }
            }

            logger.debug("Ended processing message id: ${message.id}")

            return true
        } catch (e: Exception) {
            // Log the error and return false to keep the message in the queue
            println("Error processing message: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
}

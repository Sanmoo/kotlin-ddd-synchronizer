package com.github.sanmoo.ddd.synchronizer.messaging

import com.github.sanmoo.ddd.synchronizer.legacy.persistence.models.ResourceARecord
import com.github.sanmoo.ddd.synchronizer.messaging.commands.*
import com.github.sanmoo.ddd.synchronizer.messaging.events.Event
import com.github.sanmoo.ddd.synchronizer.messaging.resources.ResourceA
import com.github.sanmoo.ddd.synchronizer.util.OBJECT_MAPPER
import org.javalite.activejdbc.Base
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.time.Clock
import software.amazon.awssdk.services.sqs.model.Message as SqsMessage

@Component
class MessageProcessor(
    val clock: Clock,
    val commandDispatcher: CommandSQSDispatcher,
    val jdbcTemplate: JdbcTemplate,
    val supplier: () -> String,
) {
    private val logger = LoggerFactory.getLogger(MessageProcessor::class.java)

    suspend fun processMessage(sqsMessage: SqsMessage): Boolean {
        val content = sqsMessage.body()
        val jsonNode = OBJECT_MAPPER.readTree(content)
        val message = Message.from(jsonNode)

        if (message is Event) {
            commandDispatcher.dispatch(message.toCommandList(clock, supplier))
        } else {
            if (message is CreateResourceAUpstream) {
                logger.info("Create Resource A Upstream: Consuming... an API to create Resource A Upstream")
                logger.info("Done")
            } else if (message is UpdateResourceAUpstream) {
                logger.info("Update Resource A Upstream: Consuming... an API to update Resource A Upstream")
                logger.info("Done")
            } else if (message is CreateResourceADownstream) {
                processResource(message.resourceA, true)
                logger.info("Created Resource A Downstream successfully")
            } else {
                processResource((message as UpdateResourceADownstream).resourceA, false)
                logger.info("Updated Resource A Downstream successfully")
            }
        }

        logger.debug("Ended processing message id: ${message.id}")

        return true
    }

    private fun processResource(resourceA: ResourceA, creation: Boolean) {
        if (!Base.hasConnection()) {
            Base.attach(jdbcTemplate.dataSource!!.connection)
        }
        Base.openTransaction()
        try {
            val resourceA = ResourceARecord.create("id", resourceA.id, "name", resourceA.name)
            if (creation) {
                resourceA.insert()
            } else {
                resourceA.saveIt()
            }
            Base.commitTransaction()
        } catch (e: Exception) {
            Base.rollbackTransaction()
            throw e
        }
    }
}

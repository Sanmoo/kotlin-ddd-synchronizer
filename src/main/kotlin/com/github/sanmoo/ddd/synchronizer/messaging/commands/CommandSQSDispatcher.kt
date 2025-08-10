package com.github.sanmoo.ddd.synchronizer.messaging.commands

import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

@Component
class CommandSQSDispatcher(
    private val sqsClient: SqsAsyncClient,
    @Value("\${sqs.queue-url}")
    private val queueUrl: String
) {
    private val objectMapper = StandardObjectMapper.INSTANCE

    fun dispatch(commands: List<Command>) {
        commands.map {
            SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(objectMapper.writeValueAsString(it.toObjectNode()))
                // TODO: resource1a1 should not be hardcoded here, instead should be provided by the Command object
                //  itself
                .messageGroupId("resource1a1${it.aggregateId}")
                .messageDeduplicationId(it.id)
                .build()
        }.map { sqsClient.sendMessage(it) }.forEach { it.get() }
    }
}

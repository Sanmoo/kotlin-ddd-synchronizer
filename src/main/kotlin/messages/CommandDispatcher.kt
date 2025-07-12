package com.github.sanmoo.messages

import com.fasterxml.jackson.databind.ObjectMapper
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

class CommandDispatcher(
    private val sqsAsyncClient: SqsAsyncClient,
    private val objectMapper: ObjectMapper
) {
    fun dispatch(command: CreateResourceADownstreamCommand, recipient: CommandRecipient) {
        println("Dispatching command: $command to Aggregate X command processor")
        dispatch(
            body = objectMapper.writeValueAsString(command),
            recipient = recipient,
            deduplicationId = command.event.eventId,
            groupId = command.event.id
        )
    }

    fun dispatch(command: UpdateResourceADownstreamCommand, recipient: CommandRecipient) {
        println("Dispatching command: $command to Aggregate X command processor")
        dispatch(
            body = objectMapper.writeValueAsString(command),
            recipient = recipient,
            deduplicationId = command.event.eventId,
            groupId = command.event.id
        )
    }

    fun dispatch(command: CreateResourceBDownstreamCommand, recipient: CommandRecipient) {
        println("Dispatching command: $command to Aggregate Y command processor")
        dispatch(
            body = objectMapper.writeValueAsString(command),
            recipient = recipient,
            deduplicationId = command.event.eventId,
            groupId = command.event.id
        )
    }

    private fun dispatch(body: String, recipient: CommandRecipient, deduplicationId: String, groupId: String) {
        val sendMessageRequest = SendMessageRequest
            .builder()
            .queueUrl(recipient.getQueueUrl())
            .messageDeduplicationId(deduplicationId)
            .messageGroupId(groupId)
            .messageBody(body)
            .build()
        sqsAsyncClient.sendMessage(sendMessageRequest).join()
    }
}
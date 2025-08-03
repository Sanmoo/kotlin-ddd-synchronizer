package com.github.sanmoo.messages

import com.github.sanmoo.util.StandardObjectMapper
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

class MessageSender(
    private val sqsAsyncClient: SqsAsyncClient,
    private val queueUrl: String,
) {
    fun send(message: Message, deduplicationId: String, groupId: String) {
        val body = StandardObjectMapper.INSTANCE.writeValueAsString(message)
        val sendMessageRequest = SendMessageRequest
            .builder()
            .queueUrl(queueUrl)
            .messageDeduplicationId(deduplicationId)
            .messageGroupId(groupId)
            .messageBody(body)
            .build()
        sqsAsyncClient.sendMessage(sendMessageRequest).get()
    }
}
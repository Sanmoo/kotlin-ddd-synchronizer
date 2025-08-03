package com.github.sanmoo

import com.github.sanmoo.consumers.sqs.SQSFactory
import com.github.sanmoo.consumers.sqs.SQSMessageConsumer
import com.github.sanmoo.messages.MessageSender
import com.github.sanmoo.messages.handlers.MessageHandlerFactory
import com.github.sanmoo.util.Environment

fun main() {
    Environment(System::getenv).verifyVariables(listOf("AWS_ACCESS_KEY_ID"))

    val sqsClient = SQSFactory().create()
    val queueUrl = "sdfsdf"

    SQSMessageConsumer(
        sqsClient,
        queueUrl,
        5,
        MessageHandlerFactory(MessageSender(sqsClient, queueUrl)),
    ).start()
}
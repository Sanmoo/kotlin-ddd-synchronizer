package com.github.sanmoo.consumers.sqs

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

class SQSMessageConsumer(
    private val sqsClient: SqsAsyncClient,
    private val processMessage: suspend (String) -> Unit,
    private val queueUrls: List<String>
) : Runnable {
    private val logger = LoggerFactory.getLogger(SQSMessageConsumer::class.java)

    fun startThread() {
        logger.info("Starting sqs consumer as daemon...")
        Thread(this).start()
    }

    override fun run(): Unit = runBlocking {
        queueUrls.map {
            launch {
                start(it)
            }
        }
    }

    private suspend fun start(queueUrl: String) = coroutineScope {
        logger.info("Starting consumption for queue: $queueUrl with long polling...")

        val response = sqsClient.receiveMessage(
            ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(20)
                .build()
        ).asDeferred().await()

        logger.info("Long pooling returned ${response.messages().size} messages")

        response.messages().forEach {
            logger.info("Will start processing message with id {}", it.messageId())

            launch { processMessage(it.body()) }.join()

            logger.info("Message with id {} processed, now deleting it", it.messageId())

            sqsClient.deleteMessage(
                DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(it.receiptHandle())
                    .build()
            ).asDeferred().await()

            logger.info("Message with id {} deleted", it.messageId())
        }
    }
}
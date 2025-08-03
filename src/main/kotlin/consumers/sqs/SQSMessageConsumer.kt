package com.github.sanmoo.consumers.sqs

import com.github.sanmoo.messages.handlers.MessageHandlerFactory
import com.github.sanmoo.messages.Message
import com.github.sanmoo.util.StandardObjectMapper
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors


class SQSMessageConsumer(
    private val sqsClient: SqsAsyncClient,
    private val queueUrl: String,
    private val parallelismFactor: Int,
    private val messageHandlerFactory: MessageHandlerFactory
) {
    private val logger = LoggerFactory.getLogger(SQSMessageConsumer::class.java)

    fun start(): CompletableFuture<Unit> {
        val result = CompletableFuture<Unit>()

        logger.info("Starting sqs consumer for queue: $queueUrl with parallelism factor: $parallelismFactor")

        val executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("sqs-consumer-", 0).factory())

        executor.use {
            val futures: List<CompletableFuture<Unit>> = (0 until parallelismFactor).map { i ->
                val completableFuture = CompletableFuture<Unit>()

                executor.submit(Callable {
                    try {
                        start(i + 1)
                    } catch (e: Exception) {
                        logger.error("Error in consumer thread $i", e)
                        completableFuture.completeExceptionally(e)
                    }
                })

                completableFuture
            }

            try {
                CompletableFuture.allOf(*futures.toTypedArray()).join()
                result.complete(Unit)
            } catch (e: Exception) {
                logger.error("Error in at least one consumer thread. Completing exceptionally", e)
                result.completeExceptionally(e)
            }
        }

        return result
    }

    private fun start(threadIdentifier: Int) {
        logger.info("Starting consuming process for queue $queueUrl in thread $threadIdentifier")

        while (true) {
            val response = sqsClient.receiveMessage(
                ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .waitTimeSeconds(20)
                    .build()
            ).get()

            logger.info("Long pooling returned ${response.messages().size} messages for thread $threadIdentifier")

            response.messages().forEach {
                logger.info("Will start processing message with id {} in thread $threadIdentifier", it.messageId())

                val message = Message.from(StandardObjectMapper.INSTANCE.readTree(it.body()))
                messageHandlerFactory.createFor(message).invoke()

                logger.info("Message with id {} processed in thread $threadIdentifier, now deleting it", it.messageId())

                sqsClient.deleteMessage(
                    DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(it.receiptHandle())
                        .build()
                ).get()

                logger.info("Message with id {} deleted", it.messageId())
            }
        }
    }
}
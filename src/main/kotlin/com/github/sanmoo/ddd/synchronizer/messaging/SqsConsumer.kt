package com.github.sanmoo.ddd.synchronizer.messaging

import com.github.sanmoo.ddd.synchronizer.config.SqsProperties
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.future.await
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

private const val MAX_PARALLEL_MESSAGES = 5

@Component
class SqsConsumer(
    private val sqsClient: SqsAsyncClient,
    private val messageProcessor: MessageProcessor,
    private val sqsProperties: SqsProperties,
    @Value("\${sqs.queue-url}")
    private val queueUrl: String,
    @Qualifier("queuePolling")
    private val coroutineContext: CoroutineContext
) {
    private val logger = LoggerFactory.getLogger(SqsConsumer::class.java)
    private val isRunning = AtomicBoolean(false)
    private val supervisor = SupervisorJob()
    private val messageChannel = Channel<Message>(Channel.UNLIMITED)
    private val coroutineScope = CoroutineScope(coroutineContext + supervisor)

    @EventListener(ApplicationReadyEvent::class)
    fun start() {
        if (isRunning.compareAndSet(false, true)) {
            logger.info("Starting SQS consumer")
            coroutineScope.launch {
                repeat(MAX_PARALLEL_MESSAGES) {
                    launchProcessor()
                }
                startPolling()
            }
        }
    }

    @PreDestroy
    fun stop() {
        if (isRunning.compareAndSet(true, false)) {
            logger.info("Stopping SQS consumer")
            supervisor.cancel()
            coroutineScope.cancel()
        }
    }

    private suspend fun launchProcessor() = coroutineScope.launch {
        while (isRunning.get()) {
            val message = messageChannel.receive()

            try {
                val success = messageProcessor.processMessage(message)
                if (success) {
                    deleteMessage(message)
                }
            } catch (e: Exception) {
                logger.error("Error processing message: ${e.message}", e)
            }
        }
    }

    private suspend fun startPolling() {
        while (isRunning.get()) {
            try {
                val receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(sqsProperties.maxNumberOfMessages)
                    .waitTimeSeconds(sqsProperties.waitTimeSeconds)
                    .visibilityTimeout(10)
                    .build()

                val response = sqsClient.receiveMessage(receiveRequest).await()

                response.messages().forEach { message ->
                    messageChannel.send(message)
                }
            } catch (e: Exception) {
                logger.error("Error polling SQS: ${e.message}", e)
                delay(5000) // Wait before retrying on error
            }
        }
    }

    private suspend fun deleteMessage(message: Message) {
        try {
            val deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(message.receiptHandle())
                .build()

            withContext(Dispatchers.IO) {
                sqsClient.deleteMessage(deleteRequest).get()
            }
        } catch (e: Exception) {
            logger.error("Error deleting message: ${e.message}", e)
        }
    }
}

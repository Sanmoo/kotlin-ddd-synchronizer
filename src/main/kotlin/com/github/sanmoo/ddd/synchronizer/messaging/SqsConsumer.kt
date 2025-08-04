package com.github.sanmoo.ddd.synchronizer.messaging

import com.github.sanmoo.ddd.synchronizer.config.SqsProperties
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
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
    private val sqsProperties: SqsProperties
) : CoroutineScope {
    private val logger = LoggerFactory.getLogger(SqsConsumer::class.java)
    private val isRunning = AtomicBoolean(false)
    private val job = SupervisorJob()
    private val executor = Executors.newVirtualThreadPerTaskExecutor()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val messageChannel = Channel<Message>(Channel.UNLIMITED)

    @EventListener(ApplicationReadyEvent::class)
    fun start() {
        if (isRunning.compareAndSet(false, true)) {
            logger.info("Starting SQS consumer")
            launch {
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
            job.cancel()
            executor.shutdown()
        }
    }

    private fun CoroutineScope.launchProcessor() = launch {
        for (message in messageChannel) {
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
                    .queueUrl(sqsProperties.queueUrl)
                    .maxNumberOfMessages(sqsProperties.maxNumberOfMessages)
                    .waitTimeSeconds(sqsProperties.waitTimeSeconds)
                    .build()

                val response = withContext(Dispatchers.IO) {
                    sqsClient.receiveMessage(receiveRequest).get()
                }

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
                .queueUrl(sqsProperties.queueUrl)
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

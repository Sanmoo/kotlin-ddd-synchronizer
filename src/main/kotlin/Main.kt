package com.github.sanmoo

import com.github.sanmoo.consumers.CommandProcessor
import com.github.sanmoo.consumers.kinesis.KCLFactories
import com.github.sanmoo.consumers.kinesis.KinesisConsumer
import com.github.sanmoo.consumers.sqs.SQSFactory
import com.github.sanmoo.consumers.sqs.SQSMessageConsumer
import com.github.sanmoo.messages.CommandRecipient
import com.github.sanmoo.util.Environment
import software.amazon.kinesis.coordinator.Scheduler
import software.amazon.kinesis.retrieval.polling.PollingConfig

fun main() {
    Environment(System::getenv).verifyVariables(CommandRecipient.entries.map { it.envVarNameForQueueUrl }
        .toTypedArray())

    val configsBuilder = KCLFactories().createKCLConfigsBuilder()

    KinesisConsumer(
        Scheduler(
            configsBuilder.checkpointConfig(),
            configsBuilder.coordinatorConfig(),
            configsBuilder.leaseManagementConfig(),
            configsBuilder.lifecycleConfig(),
            configsBuilder.metricsConfig(),
            configsBuilder.processorConfig(),
            configsBuilder.retrievalConfig().retrievalSpecificConfig(PollingConfig(configsBuilder.kinesisClient()))
        )
    ).startThread()

    SQSMessageConsumer(
        SQSFactory().create(),
        CommandProcessor()::process,
        CommandRecipient.entries.map { it.getQueueUrl(System::getenv) }
    ).startThread()
}
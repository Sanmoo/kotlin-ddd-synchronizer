package com.github.sanmoo

import com.github.sanmoo.consumers.KCLFactories
import com.github.sanmoo.consumers.KinesisConsumer
import software.amazon.kinesis.coordinator.Scheduler
import software.amazon.kinesis.retrieval.polling.PollingConfig

fun main() {
    val kclFactories = KCLFactories()
    val configsBuilder = kclFactories.createKCLConfigsBuilder()
    val scheduler = Scheduler(
        configsBuilder.checkpointConfig(),
        configsBuilder.coordinatorConfig(),
        configsBuilder.leaseManagementConfig().failoverTimeMillis(0),
        configsBuilder.lifecycleConfig(),
        configsBuilder.metricsConfig(),
        configsBuilder.processorConfig(),
        configsBuilder.retrievalConfig().retrievalSpecificConfig(PollingConfig(configsBuilder.kinesisClient()))
    )
    val consumer = KinesisConsumer(scheduler)

    // TODO: Start KCL consumer
    consumer.startAsDaemon("single-worker")

    // TODO: Start Aggregator Commands Queue Consumer

    // Keep main thread alive
    Thread.currentThread().join()
}
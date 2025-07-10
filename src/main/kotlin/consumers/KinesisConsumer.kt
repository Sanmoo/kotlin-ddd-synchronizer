package com.github.sanmoo.consumers

import software.amazon.kinesis.coordinator.Scheduler
import java.util.logging.Logger

class KinesisConsumer(private val scheduler: Scheduler) {
    private var logger: Logger = Logger.getLogger(KinesisConsumer::class.toString())

    fun startAsDaemon(workerIdentifier: String) {
        logger.info("Starting worker $workerIdentifier...")

        Runtime.getRuntime().addShutdownHook(Thread {
            this.shutdown()
        })

        Thread(scheduler).apply {
            isDaemon = true
            start()
        }
    }

    private fun shutdown() {
        logger.info("Shutting down consumer...")
        scheduler.shutdown()
        logger.info("Consumer shutdown complete")
    }
}
package com.github.sanmoo.consumers.kinesis

import software.amazon.kinesis.coordinator.Scheduler
import java.util.logging.Logger

class KinesisConsumer(private val scheduler: Scheduler) {
    private var logger: Logger = Logger.getLogger(KinesisConsumer::class.toString())

    fun startThread() {
        logger.info("Starting kinesis consumer...")

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
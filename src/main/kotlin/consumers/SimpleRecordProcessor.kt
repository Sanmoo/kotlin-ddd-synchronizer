package com.github.sanmoo.consumers

import software.amazon.kinesis.lifecycle.events.*
import software.amazon.kinesis.processor.ShardRecordProcessor
import java.nio.charset.StandardCharsets
import java.util.logging.Logger

class SimpleRecordProcessor(val processor: EventProcessor) : ShardRecordProcessor {
    private var logger: Logger = Logger.getLogger(SimpleRecordProcessor::class.toString())

    override fun initialize(initializationInput: InitializationInput) {
        logger.info("Initializing record processor for shard: ${initializationInput.shardId()}")
    }

    override fun processRecords(processRecordsInput: ProcessRecordsInput) {
        processRecordsInput.records().forEach { record ->
            logger.info("Decoding record!!!")
            val data = StandardCharsets.UTF_8.decode(record.data()).toString()
            logger.info("Handled record: $data to processor")
            processor.process(data)
        }

        processRecordsInput.checkpointer().checkpoint()
    }

    override fun leaseLost(leaseLostInput: LeaseLostInput) {
        logger.info("Lease lost for shard")
    }

    override fun shardEnded(shardEndedInput: ShardEndedInput) {
        logger.info("Shard ended")
        shardEndedInput.checkpointer().checkpoint()
    }

    override fun shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput) {
        logger.info("Shutdown requested")
        shutdownRequestedInput.checkpointer().checkpoint()
    }
}
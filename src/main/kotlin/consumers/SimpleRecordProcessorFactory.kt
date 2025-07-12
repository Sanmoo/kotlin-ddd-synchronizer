package com.github.sanmoo.consumers

import software.amazon.kinesis.processor.ShardRecordProcessorFactory

class SimpleRecordProcessorFactory(
    val processor: EventProcessor
) : ShardRecordProcessorFactory {
    override fun shardRecordProcessor() = SimpleRecordProcessor(processor)
}
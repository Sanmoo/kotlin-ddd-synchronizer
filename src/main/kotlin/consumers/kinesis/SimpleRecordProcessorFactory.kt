package com.github.sanmoo.consumers.kinesis

import com.github.sanmoo.consumers.EventProcessor
import software.amazon.kinesis.processor.ShardRecordProcessorFactory

class SimpleRecordProcessorFactory(
    val processor: EventProcessor
) : ShardRecordProcessorFactory {
    override fun shardRecordProcessor() = SimpleRecordProcessor(processor)
}
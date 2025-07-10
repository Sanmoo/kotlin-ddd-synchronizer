package com.github.sanmoo.consumers

import software.amazon.kinesis.processor.ShardRecordProcessorFactory

class SimpleRecordProcessorFactory(
    private val processor: (String) -> Unit
) : ShardRecordProcessorFactory {
    override fun shardRecordProcessor() = SimpleRecordProcessor(processor)
}
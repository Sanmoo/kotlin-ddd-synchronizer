package com.github.sanmoo.consumers

import software.amazon.awssdk.arns.Arn
import software.amazon.kinesis.common.InitialPositionInStream
import software.amazon.kinesis.common.InitialPositionInStreamExtended
import software.amazon.kinesis.common.StreamConfig
import software.amazon.kinesis.common.StreamIdentifier
import software.amazon.kinesis.processor.FormerStreamsLeasesDeletionStrategy
import software.amazon.kinesis.processor.MultiStreamTracker
import java.time.Duration

class UpstreamStreamTracker : MultiStreamTracker {
    override fun streamConfigList(): List<StreamConfig?>? {
        return listOf(
            StreamConfig(
                StreamIdentifier.multiStreamInstance(
                    Arn.fromString("arn:aws:kinesis:us-east-1:000000000000:stream/resource-A-event-stream"),
                    1
                ),
                InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.LATEST)
            ),
            StreamConfig(
                StreamIdentifier.multiStreamInstance(
                    Arn.fromString("arn:aws:kinesis:us-east-1:000000000000:stream/resource-B-event-stream"),
                    1
                ),
                InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.LATEST)
            ),
        )
    }

    override fun formerStreamsLeasesDeletionStrategy(): FormerStreamsLeasesDeletionStrategy? {
        return object : FormerStreamsLeasesDeletionStrategy.AutoDetectionAndDeferredDeletionStrategy() {
            override fun waitPeriodToDeleteFormerStreams(): Duration? {
                return Duration.ofSeconds(15)
            }
        }
    }
}
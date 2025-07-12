package consumers

import com.github.sanmoo.consumers.kinesis.UpstreamStreamTracker
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Duration

class UpstreamStreamTrackerTest {
    @Test
    fun streamConfigList() {
        val sut = UpstreamStreamTracker()
        assertEquals(2, sut.streamConfigList()?.size)
        assertEquals("resource-A-event-stream", sut.streamConfigList() ?.get(0)?.streamIdentifier()?.streamName())
        assertEquals("resource-B-event-stream", sut.streamConfigList() ?.get(1)?.streamIdentifier()?.streamName())
        assertEquals(2, sut.streamConfigList()?.size)
    }

    @Test
    fun formerStreamsLeasesDeletionStrategy() {
        val sut = UpstreamStreamTracker()
        val result = sut.formerStreamsLeasesDeletionStrategy()?.waitPeriodToDeleteFormerStreams()
        assertEquals(Duration.ofSeconds(15), result)
    }
}
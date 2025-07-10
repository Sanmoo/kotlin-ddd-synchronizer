package consumers

import com.github.sanmoo.consumers.KCLFactories
import com.github.sanmoo.consumers.SimpleRecordProcessorFactory
import com.github.sanmoo.consumers.UpstreamStreamTracker
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class KCLFactoriesTest {
    @Test
    fun createKCLConfigsBuilder() {
        val sut = KCLFactories()
        sut.createKCLConfigsBuilder().apply {
            assertNotNull(kinesisClient())
            assertNotNull(cloudWatchClient())
            assertNotNull(dynamoDBClient())
            assertInstanceOf(UpstreamStreamTracker::class.java, streamTracker())
            assertNotNull(dynamoDBClient())
            assertEquals("integrator", applicationName())
            assertEquals("single-worker", workerIdentifier())
            assertInstanceOf(SimpleRecordProcessorFactory::class.java, shardRecordProcessorFactory())
        }
    }
}
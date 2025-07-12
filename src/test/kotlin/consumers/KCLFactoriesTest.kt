package consumers

import com.diffplug.selfie.Selfie.expectSelfie
import com.github.sanmoo.consumers.kinesis.KCLFactories
import com.github.sanmoo.consumers.kinesis.SimpleRecordProcessorFactory
import com.github.sanmoo.consumers.kinesis.UpstreamStreamTracker
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class KCLFactoriesTest {
    @Test
    fun createKCLConfigsBuilderWithExpectedProperties() {
        val sut = KCLFactories()
        val result = sut.createKCLConfigsBuilder()

        result.apply {
            assertNotNull(kinesisClient())
            assertNotNull(cloudWatchClient())
            assertNotNull(dynamoDBClient())
            assertInstanceOf(UpstreamStreamTracker::class.java, streamTracker())
            assertNotNull(dynamoDBClient())
            assertEquals("integrator", applicationName())
            assertEquals("single-worker", workerIdentifier())
        }

        assertInstanceOf(SimpleRecordProcessorFactory::class.java, result.shardRecordProcessorFactory())
        val createdProcessor = (result.shardRecordProcessorFactory() as SimpleRecordProcessorFactory).processor

        createdProcessor.apply {
            expectSelfie(this.objectMapper.registeredModuleIds.toString()).toBe("[com.fasterxml.jackson.module.kotlin.KotlinModule, jackson-datatype-jsr310]");
        }
    }
}
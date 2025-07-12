package consumers

import com.github.sanmoo.consumers.EventProcessor
import com.github.sanmoo.consumers.kinesis.SimpleRecordProcessorFactory
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleRecordProcessorFactoryTest {
    @Test
    fun create() {
        val processorFunMock = EventProcessor(mockk(), mockk())
        val sut = SimpleRecordProcessorFactory(processorFunMock)

        val result = sut.shardRecordProcessor()

        assertEquals(processorFunMock, result.processor)
    }

}
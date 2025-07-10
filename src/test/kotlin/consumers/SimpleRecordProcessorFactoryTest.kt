package consumers

import com.github.sanmoo.consumers.SimpleRecordProcessorFactory
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleRecordProcessorFactoryTest {
    @Test
    fun create() {
        val processorFunMock = mockk<(String) -> Unit>()
        val sut = SimpleRecordProcessorFactory(processorFunMock)

        val result = sut.shardRecordProcessor()

        assertEquals(processorFunMock, result.processor)
    }

}
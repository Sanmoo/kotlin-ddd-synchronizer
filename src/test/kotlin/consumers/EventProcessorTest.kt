package consumers

import com.github.sanmoo.consumers.EventProcessor
import org.junit.jupiter.api.Test

class EventProcessorTest {
    @Test
    fun process() {
        val eventProcessor = EventProcessor()
        // No assertions required for now, since it currently does nothing
        eventProcessor.process("test event")
    }
}
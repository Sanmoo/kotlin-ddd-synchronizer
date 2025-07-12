package consumers

import com.github.sanmoo.consumers.EventProcessor
import com.github.sanmoo.consumers.SimpleRecordProcessor
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.kinesis.lifecycle.events.InitializationInput
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput
import software.amazon.kinesis.lifecycle.events.ShardEndedInput
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput
import software.amazon.kinesis.retrieval.KinesisClientRecord
import java.nio.ByteBuffer
import kotlin.text.Charsets.UTF_8

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class SimpleRecordProcessorTest {
    @MockK
    lateinit var processor: EventProcessor

    lateinit var sut: SimpleRecordProcessor

    @BeforeEach
    fun setUp() {
        every { processor.process(any()) } just Runs
        sut = SimpleRecordProcessor(processor)
    }

    @AfterEach
    fun tearDown() {
        confirmVerified(processor)
    }

    @Test
    fun initialize() {
        val initializationInput = mockk<InitializationInput>(relaxed = true)
        sut.initialize(initializationInput)
        verifyAll {
            initializationInput.shardId()
        }
    }

    @Test
    fun processRecords() {
        // given
        val processRecordsInput = mockk<ProcessRecordsInput>()
        val mockedRecords = listOf<KinesisClientRecord>(mockk(), mockk())
        every { mockedRecords[0].data() } returns ByteBuffer.wrap("test0".toByteArray(UTF_8))
        every { mockedRecords[1].data() } returns ByteBuffer.wrap("test1".toByteArray(UTF_8))
        every { processRecordsInput.records() } returns mockedRecords
        every { processRecordsInput.checkpointer().checkpoint() } just Runs

        // when
        sut.processRecords(processRecordsInput)

        // then
        verifyAll {
            processRecordsInput.records()
            mockedRecords[0].data()
            mockedRecords[1].data()
            processor.process("test0")
            processor.process("test1")
            processRecordsInput.checkpointer().checkpoint()
        }
    }

    @Test
    fun leaseLost() {
        // no assertions since it just logs for now
        sut.leaseLost(mockk())
    }

    @Test
    fun shardEnded() {
        val input = mockk<ShardEndedInput>()
        every { input.checkpointer().checkpoint() } just Runs

        sut.shardEnded(input)

        verifyAll { input.checkpointer().checkpoint() }
    }

    @Test
    fun shutdownRequested() {
        val input = mockk<ShutdownRequestedInput>()
        every { input.checkpointer().checkpoint() } just Runs

        sut.shutdownRequested(input)

        verifyAll { input.checkpointer().checkpoint() }
    }
}
package consumers

import com.github.sanmoo.consumers.KinesisConsumer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.kinesis.coordinator.Scheduler

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class KinesisConsumerTest {
    @MockK
    lateinit var scheduler: Scheduler

    lateinit var sut: KinesisConsumer

    @BeforeEach
    fun setUp() {
        every { scheduler.shutdown() } just Runs
        every { scheduler.run() } just Runs
        sut = KinesisConsumer(scheduler)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun startAsDaemon() {
        // given
        mockkStatic(Runtime::class)
        val configuredShutdownHook = slot<Thread>()
        every { Runtime.getRuntime().addShutdownHook(capture(configuredShutdownHook)) } just Runs
        // Can't mock Thread due to bizarre error in MockK
        // TODO: investigate this further
//        mockkConstructor(Thread::class)
//        every { constructedWith<Thread>(EqMatcher(scheduler)).setDaemon(any()) } just Runs
//        every { constructedWith<Thread>(EqMatcher(scheduler)).start() } answers { }

        // when
        sut.startAsDaemon("worker")
        configuredShutdownHook.captured.start()

        // then
        verifyAll {
            Runtime.getRuntime().addShutdownHook(configuredShutdownHook.captured)
//            anyConstructed<Thread>().start()
//            anyConstructed<Thread>().isDaemon
            scheduler.shutdown()
            scheduler.run()
        }
    }

    @Test
    fun shutdown() {
    }
}
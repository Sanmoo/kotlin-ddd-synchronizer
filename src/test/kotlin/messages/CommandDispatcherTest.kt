package messages

import com.github.sanmoo.messages.CommandDispatcher
import com.github.sanmoo.messages.CreateResourceADownstreamCommand
import com.github.sanmoo.messages.CreateResourceBDownstreamCommand
import com.github.sanmoo.messages.UpdateResourceADownstreamCommand
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CommandDispatcherTest {
    private val sut = CommandDispatcher()
    // TODO add assertion for tests

    @Test
    fun dispatchCreateResourceADownstreamCommand() {
        sut.dispatch(mockk<CreateResourceADownstreamCommand>())
    }

    @Test
    fun dispatchUpdateResourceADownstreamCommand() {
        sut.dispatch(mockk<UpdateResourceADownstreamCommand>())
    }

    @Test
    fun dispatchCreateResourceBDownstreamCommand() {
        sut.dispatch(mockk<CreateResourceBDownstreamCommand>())
    }
}
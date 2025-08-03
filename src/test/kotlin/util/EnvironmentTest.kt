package util

import com.github.sanmoo.util.Environment
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifyAll
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class EnvironmentTest {
    @MockK
    private lateinit var getenv: (String) -> String?

    @Test
    fun verifyVariables() {
        every { getenv.invoke("NON_EXISTENT") } returns null
        every { getenv.invoke("EXISTENT1") } returns "A"
        every { getenv.invoke("EXISTENT2") } returns "B"

        assertThrows(IllegalStateException::class.java) {
            Environment(getenv).verifyVariables(listOf("NON_EXISTENT", "EXISTENT1"))
        }

        assertDoesNotThrow {
            Environment(getenv).verifyVariables(listOf("EXISTENT1", "EXISTENT2"))
        }

        verifyAll {
            getenv.invoke("NON_EXISTENT")
            getenv.invoke("EXISTENT1")
            getenv.invoke("EXISTENT2")
        }
    }
}
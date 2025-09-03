package com.github.sanmoo.ddd.synchronizer.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.Clock

class GenericConfigTest {
    @Test
    fun testClock() {
        val clock = GenericConfig().clock()
        assertEquals(Clock.systemDefaultZone(), clock)
    }

    @Test
    fun testUuidProvider() {
        val provider = GenericConfig().uuidProvider()
        assertNotNull(provider())
        assertNotNull(provider())
    }

    @Test
    fun testQueuePolling() {
        val coroutineScope = GenericConfig().queuePolling()
        assertNotNull(coroutineScope)
    }
}
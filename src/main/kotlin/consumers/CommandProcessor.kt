package com.github.sanmoo.consumers

import kotlinx.coroutines.coroutineScope

class CommandProcessor {
    suspend fun process(command: String): Unit = coroutineScope {
        println("Received command $command")
    }
}
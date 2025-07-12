package com.github.sanmoo.messages

class CommandDispatcher {
    fun dispatch(command: CreateResourceADownstreamCommand) {
        // TODO
        println("Dispatching command: $command to Aggregate X command processor")
    }

    fun dispatch(command: UpdateResourceADownstreamCommand) {
        // TODO
        println("Dispatching command: $command to Aggregate X command processor")
    }

    fun dispatch(command: CreateResourceBDownstreamCommand) {
        // TODO
        println("Dispatching command: $command to Aggregate Y command processor")
    }
}
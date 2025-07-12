package com.github.sanmoo.messages

enum class CommandRecipient {
    AGGREGATE_A_COMMANDS_INBOX,
    AGGREGATE_B_COMMANDS_INBOX;

    fun getQueueUrl(): String {
        // implement logic to get the queue URL, for example fetching it from environment variable
        return this.name.lowercase()
    }
}
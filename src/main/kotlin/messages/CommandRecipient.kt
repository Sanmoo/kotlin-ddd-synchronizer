package com.github.sanmoo.messages

enum class CommandRecipient(val envVarNameForQueueUrl: String) {
    AGGREGATE_A_COMMANDS_INBOX("AGGREGATE_A_COMMANDS_QUEUE_URL"),
    AGGREGATE_B_COMMANDS_INBOX("AGGREGATE_B_COMMANDS_QUEUE_URL");

    fun getQueueUrl(getenv: (String) -> String?): String {
        val queueUrl = getenv(envVarNameForQueueUrl)
        if (queueUrl == null) {
            throw IllegalStateException("Could not find value $envVarNameForQueueUrl in environment.")
        }
        return queueUrl
    }
}
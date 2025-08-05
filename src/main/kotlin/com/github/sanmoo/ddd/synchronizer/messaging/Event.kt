package com.github.sanmoo.ddd.synchronizer.messaging

import com.fasterxml.jackson.databind.JsonNode

data class Event(
    val id: String,
    val name: String
) {
    companion object {
        fun from(node: JsonNode): Event {
            return Event(
                id = node.get("data").get("id").textValue(),
                name = node.get("data").get("name").textValue()
            )
        }
    }
}

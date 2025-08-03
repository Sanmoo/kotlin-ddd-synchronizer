package com.github.sanmoo.messages

import com.fasterxml.jackson.databind.JsonNode
import com.github.sanmoo.util.StandardObjectMapper

enum class EventType(val value: String, val buildEventFrom: (JsonNode) -> Event) {
    RESOURCE_A_CREATED("resource.a.created", fun(nodes: JsonNode): ResourceACreatedEvent {
        return StandardObjectMapper.INSTANCE.readValue(
            nodes.traverse(),
            ResourceACreatedEvent::class.java
        )
    }),

    RESOURCE_A_UPDATED("resource.a.updated", fun(nodes: JsonNode): ResourceAUpdatedEvent {
        return StandardObjectMapper.INSTANCE.readValue(
            nodes.traverse(),
            ResourceAUpdatedEvent::class.java
        )
    }),

    RESOURCE_B_CREATED("resource.b.created", fun(nodes: JsonNode): Event {
        return StandardObjectMapper.INSTANCE.readValue(
            nodes.traverse(),
            ResourceBCreatedEvent::class.java
        )
    });
}
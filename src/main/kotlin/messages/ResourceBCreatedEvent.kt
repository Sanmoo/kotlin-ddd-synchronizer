package com.github.sanmoo.messages

import java.time.LocalDateTime

data class ResourceBCreatedEvent(
    var id: String,
    var createdAt: LocalDateTime,
    override val createdFromSystem: String,
    override val eventId: String
) : Event(EventType.RESOURCE_B_CREATED, createdFromSystem, eventId)
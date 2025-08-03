package com.github.sanmoo.messages

import java.time.LocalDateTime

data class ResourceAUpdatedEvent(
    var id: String,
    var updatedAt: LocalDateTime,
    override val createdFromSystem: String,
    override val eventId: String
) : Event(EventType.RESOURCE_A_UPDATED, createdFromSystem, eventId)
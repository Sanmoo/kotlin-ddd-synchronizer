package builders

import com.github.sanmoo.messages.ResourceACreatedEvent
import com.github.sanmoo.messages.ResourceAUpdatedEvent
import com.github.sanmoo.messages.ResourceBCreatedEvent
import java.time.LocalDateTime

class EventsBuilder {
    companion object {
        fun buildResourceACreatedEvent(
            id : String = "123",
            createdAt : LocalDateTime = LocalDateTime.parse("2023-06-01T00:00:00"),
            createdFromSystem : String = "UPSTREAM_SYSTEM",
            eventId : String = "123"
        ): ResourceACreatedEvent {
            return ResourceACreatedEvent(
                id = id,
                createdAt = createdAt,
                createdFromSystem = createdFromSystem,
                eventId = eventId
            )
        }

        fun buildResourceAUpdatedEvent(
            id : String = "123",
            updatedAt : LocalDateTime = LocalDateTime.parse("2023-06-01T00:00:00"),
            createdFromSystem : String = "UPSTREAM_SYSTEM",
            eventId : String = "123"
        ): ResourceAUpdatedEvent {
            return ResourceAUpdatedEvent(
                id = id,
                updatedAt = updatedAt,
                createdFromSystem = createdFromSystem,
                eventId = eventId
            )
        }

        fun buildResourceBCreatedEvent(
            id : String = "123",
            createdAt : LocalDateTime = LocalDateTime.parse("2023-06-01T00:00:00"),
            createdFromSystem : String = "UPSTREAM_SYSTEM",
            eventId : String = "123"
        ): ResourceBCreatedEvent {
            return ResourceBCreatedEvent(
                id = id,
                createdAt = createdAt,
                createdFromSystem = createdFromSystem,
                eventId = eventId
            )
        }
    }
}
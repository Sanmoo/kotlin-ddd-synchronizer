package com.github.sanmoo.messages

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ResourceACreatedEvent::class, name = "resource.a.created"),
    JsonSubTypes.Type(value = ResourceAUpdatedEvent::class, name = "resource.a.updated"),
    JsonSubTypes.Type(value = ResourceBCreatedEvent::class, name = "resource.b.created")
)
sealed class BaseEvent(
    open val type: String,
    open val createdFromSystem: String
)

@JsonTypeName("resource.a.created")
data class ResourceACreatedEvent(
    var id: String,
    var createdAt: java.time.LocalDateTime,
    override val createdFromSystem: String
) : BaseEvent("resource.a.created", createdFromSystem)

@JsonTypeName("resource.a.updated")
data class ResourceAUpdatedEvent(
    var id: String,
    var updatedAt: java.time.LocalDateTime,
    override val createdFromSystem: String
) : BaseEvent("resource.a.updated", createdFromSystem)

@JsonTypeName("resource.b.created")
data class ResourceBCreatedEvent(
    var id: String,
    var createdAt: java.time.LocalDateTime,
    override val createdFromSystem: String
) : BaseEvent("resource.b.created", createdFromSystem)

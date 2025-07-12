package com.github.sanmoo.messages

data class CreateResourceADownstreamCommand(val event: ResourceACreatedEvent)
data class UpdateResourceADownstreamCommand(val event: ResourceAUpdatedEvent)
data class CreateResourceBDownstreamCommand(val event: ResourceBCreatedEvent)

// TODO: implement upstream commands later
//class CreateResourceAUpstreamCommand(val downstreamId: String, val downstreamCreatedAt: LocalDateTime)
//class UpdateResourceAUpstreamCommand(val downstreamId: String, val downstreamUpdatedAt: LocalDateTime)
//class CreateResourceBUpstreamCommand(val downstreamId: String, val downstreamCreatedAt: LocalDateTime)

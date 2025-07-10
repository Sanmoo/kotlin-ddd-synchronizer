package com.github.sanmoo.messages

import kotlinx.serialization.Serializable

@Serializable
data class ResourceBEvent(var event: String)
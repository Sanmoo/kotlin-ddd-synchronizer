package com.github.sanmoo.ddd.synchronizer.legacy.persistency.models

import org.javalite.activejdbc.CompanionModel
import org.javalite.activejdbc.Model
import org.javalite.activejdbc.annotations.Table

@Table("outbox")
open class OutboxMessage(): Model() {
    companion object: CompanionModel<OutboxMessage>(OutboxMessage::class.java)
}
package com.github.sanmoo.ddd.synchronizer.legacy.persistency.models

import org.javalite.activejdbc.CompanionModel
import org.javalite.activejdbc.Model

open class Outbox(): Model() {
    companion object: CompanionModel<Outbox>(Outbox::class)
}
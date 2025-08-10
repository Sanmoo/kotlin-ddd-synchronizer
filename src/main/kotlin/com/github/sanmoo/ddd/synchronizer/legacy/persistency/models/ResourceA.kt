package com.github.sanmoo.ddd.synchronizer.legacy.persistency.models

import org.javalite.activejdbc.CompanionModel
import org.javalite.activejdbc.Model
import org.javalite.activejdbc.annotations.Table

@Table("resource_a")
open class ResourceA(): Model() {
    companion object: CompanionModel<ResourceA>(ResourceA::class.java)
}
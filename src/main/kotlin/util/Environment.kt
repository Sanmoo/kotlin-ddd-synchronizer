package com.github.sanmoo.util

class Environment(val getenv: (String) -> String?) {
    fun verifyVariables(names: Array<String>) {
        names.forEach {
            if (getenv(it) == null) {
                throw IllegalStateException("Environment variable $it is not set")
            }
        }
    }
}
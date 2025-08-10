package com.github.sanmoo.ddd.synchronizer.legacy

import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import selfie.SelfieSettings.Companion.expectSelfie
import java.time.Clock
import java.time.OffsetDateTime

class OutboxMessageToCommandTranslatorTest {
    companion object {
        @JvmStatic fun testTranslationArguments() = listOf(
            arguments(
                "resource.a.created.downstream", """{
                    "data": {"id": "hey", "name": "test"},
                    "id": "123", "source": "core-system",
                    "type": "resource.a.created.downstream",
                    "time": "2020-01-01T00:00:00.000Z"
                }""".trimMargin(),
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("testTranslationArguments")
    fun testTranslation(testDescription: String, eventBody: String) {
        val sut = EventToCommandTranslator(
            Clock.fixed(
                OffsetDateTime.parse("2023-06-01T00:00:00.000Z").toInstant(),
                Clock.systemUTC().zone
            )
        ) { -> "a51ed902-4e95-477e-9eae-85a41623a735" }

        val objectNodeUnderTest = StandardObjectMapper.INSTANCE.readTree(eventBody) as ObjectNode
        val command = sut.translate(objectNodeUnderTest)
        expectSelfie(command).toMatchDisk(testDescription)
    }
}
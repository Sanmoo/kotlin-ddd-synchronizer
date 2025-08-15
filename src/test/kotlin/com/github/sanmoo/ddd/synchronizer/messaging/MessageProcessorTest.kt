package com.github.sanmoo.ddd.synchronizer.messaging

import com.github.sanmoo.ddd.synchronizer.messaging.commands.Command
import com.github.sanmoo.ddd.synchronizer.messaging.commands.CommandSQSDispatcher
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.javalite.activejdbc.Base
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.jdbc.core.JdbcTemplate
import selfie.SelfieSettings.Companion.expectSelfie
import java.time.Clock
import kotlin.test.Test
import software.amazon.awssdk.services.sqs.model.Message as SqsMessage
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC

class MessageProcessorTest {
    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var sut: MessageProcessor
    private lateinit var commandDispatcher: CommandSQSDispatcher
    private val clock = Clock.fixed(OffsetDateTime.parse("2022-01-01T00:00:00Z").toInstant(), UTC)
    private val dispatchedCommands = slot<List<Command>>()
    private lateinit var db: EmbeddedDatabase

    @BeforeEach
    fun setUp() {
        jdbcTemplate = mockk()
        commandDispatcher = mockk()
        sut = MessageProcessor(
            clock = clock,
            jdbcTemplate = jdbcTemplate,
            commandDispatcher = commandDispatcher,
            supplier = { -> "123" }
        )

        every { commandDispatcher.dispatch(capture(dispatchedCommands)) } just Runs
    }

    fun setupEmbeddedDatabase() {
        val db = EmbeddedDatabaseBuilder()
            .generateUniqueName(true)
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .build()

        jdbcTemplate = JdbcTemplate(db)

        sut = MessageProcessor(
            clock = clock,
            jdbcTemplate = jdbcTemplate,
            commandDispatcher = commandDispatcher,
            supplier = { -> "123" }
        )

        Base.close()
    }

    private fun setupEventSQSMessage(): SqsMessage {
        val sqsMessage = mockk<SqsMessage>()

        every { sqsMessage.body() } returns """
            {
                "type": "event",
                "id": "abc",
                "aggregateId": "123",
                "createdAt": "2023-06-01T00:00:00.000Z",
                "origination": "system-whatever",
                "event": {
                    "type": "resource.a.created.upstream",
                    "data": {
                        "id": "123",
                        "name": "A"
                    }
                }
            }
        """.trimIndent()

        return sqsMessage
    }

    private fun setupCommandSQSMessage(type: String): SqsMessage {
        val sqsMessage = mockk<SqsMessage>()

        every { sqsMessage.body() } returns """
            {
                "type": "command",
                "id": "abc",
                "aggregateId": "123",
                "createdAt": "2023-06-01T00:00:00.000Z",
                "command": {
                    "type": "$type",
                    "data": {
                        "id": "123",
                        "name": "A"
                    }
                }
            }
        """.trimIndent()

        return sqsMessage
    }

    @Test
    fun testProcessEventMessage() = runTest {
        // Act
        val succeeded = sut.processMessage(setupEventSQSMessage())

        // Assert
        assertTrue(succeeded)
        expectSelfie(dispatchedCommands.captured).toMatchDisk()
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "create.resource.a.upstream",
        "update.resource.a.upstream",
    ])
    fun testProcessCommandsToUpstream(commandType: String) = runTest {
        val succeeded = sut.processMessage(setupCommandSQSMessage(commandType))
        assertTrue(succeeded)
    }

    @Test
    fun testProcessCommandsToDownstreamOnCreation() = runTest {
        setupEmbeddedDatabase()
        val succeeded = sut.processMessage(setupCommandSQSMessage("create.resource.a.downstream"))

        assertTrue(succeeded)

        expectSelfie(jdbcTemplate.queryForMap("SELECT * FROM resource_a").toString()).toMatchDisk()
    }

    @Test
    fun testProcessCommandsToDownstreamOnUpdate() = runTest {
        setupEmbeddedDatabase()
        jdbcTemplate.execute("INSERT INTO resource_a (id, name) VALUES ('123', 'Other')")

        val succeeded = sut.processMessage(setupCommandSQSMessage("update.resource.a.downstream"))

        assertTrue(succeeded)
        expectSelfie(jdbcTemplate.queryForMap("SELECT * FROM resource_a").toString()).toMatchDisk()
    }
}
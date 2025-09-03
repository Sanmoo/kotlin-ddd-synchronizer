package selfie

import com.diffplug.selfie.Camera
import com.diffplug.selfie.Selfie
import com.diffplug.selfie.Snapshot
import com.diffplug.selfie.StringSelfie
import com.diffplug.selfie.junit5.SelfieSettingsAPI
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.sanmoo.ddd.synchronizer.messaging.Message
import com.github.sanmoo.ddd.synchronizer.util.OBJECT_MAPPER
import software.amazon.awssdk.services.sqs.model.Message as SqsMessage

class SelfieSettings : SelfieSettingsAPI() {
    companion object {
        val OBJECT_NODE_CAMERA = Camera<ObjectNode> { objectNode: ObjectNode ->
            val prettyJson =
                OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode)
            Snapshot.of(prettyJson)
        }

        fun expectSelfie(message: SqsMessage): StringSelfie = Selfie.expectSelfie(message.toString())
        fun expectSelfie(message: Message): StringSelfie = Selfie.expectSelfie(message.toString())
        fun expectSelfie(messages: List<Message>): StringSelfie = Selfie.expectSelfie(messages.joinToString("\n"))
        fun expectSelfie(string: String): StringSelfie = Selfie.expectSelfie(string)
        fun expectSelfie(objectNode: ObjectNode): StringSelfie = Selfie.expectSelfie(objectNode, OBJECT_NODE_CAMERA)
    }
}
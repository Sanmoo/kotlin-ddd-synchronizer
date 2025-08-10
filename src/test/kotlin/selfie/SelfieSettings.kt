package selfie

import com.diffplug.selfie.Camera
import com.diffplug.selfie.Selfie
import com.diffplug.selfie.Snapshot
import com.diffplug.selfie.StringSelfie
import com.diffplug.selfie.junit5.SelfieSettingsAPI
import com.fasterxml.jackson.databind.node.ObjectNode
import com.github.sanmoo.ddd.synchronizer.messaging.Message
import com.github.sanmoo.ddd.synchronizer.util.StandardObjectMapper

class SelfieSettings : SelfieSettingsAPI() {
    companion object {
        val OBJECT_NODE_CAMERA = Camera<ObjectNode> { objectNode: ObjectNode ->
            val prettyJson =
                StandardObjectMapper.INSTANCE.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode)
            Snapshot.of(prettyJson)
        }

        fun expectSelfie(message: Message): StringSelfie = Selfie.expectSelfie(message.toString())
        fun expectSelfie(string: String): StringSelfie = Selfie.expectSelfie(string)
        fun expectSelfie(objectNode: ObjectNode): StringSelfie = Selfie.expectSelfie(objectNode, OBJECT_NODE_CAMERA)
    }
}
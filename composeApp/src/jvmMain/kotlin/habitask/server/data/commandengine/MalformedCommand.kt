package habitask.server.data.commandengine

import java.lang.IndexOutOfBoundsException

class MalformedCommand : RuntimeException() {
    override val message: String
        get() = "Malformed command"
}
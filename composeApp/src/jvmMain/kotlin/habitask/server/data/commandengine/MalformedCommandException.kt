package habitask.server.data.commandengine

class MalformedCommandException : RuntimeException() {
    override val message: String
        get() = "Malformed command"
}
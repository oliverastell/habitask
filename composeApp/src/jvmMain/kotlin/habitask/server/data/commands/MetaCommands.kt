package habitask.server.data.commands

import habitask.common.Logger
import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext



fun CommandContext.metaCommands(backend: ServerBackend) {
    word("execute") {
        greedyValue(backend) { value ->
            backend.executeCommand(value.toString(), onOutput = { output(it) })
        }
    }

    word("format") {
        word { variable ->
            word("in") {
                word { source ->
                    word("with") {
                        greedyValue(backend) { value ->
                            output(source.replace("{$variable}", value.toString()))
                        }
                    }
                }
            }
        }
    }

    word("for") {
        word { variable ->
            token("in") {
                token { a ->
                    val a = a.toIntOrNull()
                    token("to") {
                        token { b ->
                            val b = b.toIntOrNull()
                            if (a != null && b != null) greedyTextOrNextLine(backend) { command ->
                                for (i in a..b) {
                                    backend.executeCommand(
                                        command.replace("{$variable}", i.toString()),
                                        onOutput = { output(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    word("echo") {
        greedyTextOrNextLine(backend) { text ->
            output(text)
        }
    }
}
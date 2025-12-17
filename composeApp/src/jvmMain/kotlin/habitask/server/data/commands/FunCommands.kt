package habitask.server.data.commands

import habitask.common.Logger
import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext
import habitask.server.data.commandengine.parseCommand


fun CommandContext.funCommands(backend: ServerBackend) {
    word("welcome") {
        end {
            output(backend.welcomeMessage())
        }
    }

    word("parse") {
        greedyText { source ->
            val command = parseCommand(source)
            command.tokens.forEach {
                Logger.feedback("Token: \"${it.string}\"")
            }
        }
    }

    word("random") {
        token { a ->
            val a = a.toIntOrNull()

            if (a != null) end {
                output((0..a).random())
            }

            token("to") {
                token { b ->
                    val b = b.toIntOrNull()
                    if (a != null && b != null) end {
                        output((a..b).random())
                    }
                }
            }
        }
    }
}
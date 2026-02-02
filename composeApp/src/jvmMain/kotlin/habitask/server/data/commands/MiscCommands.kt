package habitask.server.data.commands

import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext
import habitask.server.data.commandengine.parseCommand


fun CommandContext.miscCommands(backend: ServerBackend) {
    word("misc") {
        word("help") {
            end {
                output("misc commands (do not prefix with 'misc'):")
                output("  welcome ................ outputs the welcome message")
                output("  parse [source] ......... outputs each token")
                output("  random [start]..[end] .. outputs a random int between [start] and [end]")
            }
        }
    }

    word("welcome") {
        end {
            output(backend.welcomeMessage())
        }
    }

    word("parse") {
        greedyText { source ->
            val command = parseCommand(source)
            command.tokens.forEach {
                output("Token: \"${it.string}\"")
            }
        }
    }

    word("random") {
        intRange { range ->
            end {
                output(range.random())
            }
        }
    }
}
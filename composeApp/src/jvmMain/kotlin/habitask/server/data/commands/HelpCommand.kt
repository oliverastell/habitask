package habitask.server.data.commands

import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext

fun CommandContext.helpCommand(backend: ServerBackend) = word("help") {
    end {
        output("haha loser needs help")
    }
}
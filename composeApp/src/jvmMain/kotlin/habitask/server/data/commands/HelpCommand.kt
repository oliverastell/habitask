package habitask.server.data.commands

import habitask.common.Logger
import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext


fun CommandContext.helpCommand(backend: ServerBackend) = word("help") {
    end {
        output("list of all help commands:")
        output("  help")
        output("  entity help")
        output("  task help")
        output("  server help")
        output("  meta help")
        output("  misc help")
    }
    Logger.debug("should end??")
}
package habitask.server.data.commands

import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext

fun CommandContext.serverCommands(backend: ServerBackend) {
    word("restart") {
        end {
            output("restarting server...")
            backend.restart()
        }
    }

    word("server") {
        word("port") {
            val configs = backend.dbManager.getServerConfigs()

            end {
                output(configs.port)
            }
            word { newPort ->
                val newPort = newPort.toIntOrNull()

                if (newPort != null && newPort in (0..65535)) end {
                    backend.dbManager.setServerConfigs(configs.copy(port = newPort))
                }
            }
        }
    }
}
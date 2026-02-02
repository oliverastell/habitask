package habitask.server.data.commands

import habitask.common.util.toDateTimeUnit
import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.time.Clock

fun CommandContext.serverCommands(backend: ServerBackend) = word("server") {

    word("help") {
        end {
            output("server commands:")
            output("  restart .. restarts the server")
            output("  cycle .... performs a check cycle")
            output("")
            output("  port ......... outputs the current port")
            output("  port [port] .. changes the port")
            output("")
            output("  reassign .................... reassigns every task")
            output("  reassign every .............. outputs the reassignment time")
            output("  reassign every [time unit] .. changes the automatic reassignment time")
        }
    }

    word("restart") {
        end {
            output("restarting server...")
            backend.restart()
        }
    }

    word("cycle") {
        end {
            backend.checkCycle()
        }
    }

    val configs = backend.dbManager.getServerConfigs()

    word("port") {
        end {
            output(configs.port)
        }
        integer { newPort ->
            if (newPort in (0..65535)) end {
                backend.dbManager.setServerConfigs(configs.copy(port = newPort))
            }
        }
    }

    word("reassign") {
        end {
            backend.reassignTasks()
        }

        word("every") {
            end {
                output(configs.reassignEvery)
            }

            greedyText { reassignEvery ->
                val reassignEvery = reassignEvery.toDateTimeUnit()
                backend.dbManager.setServerConfigs(configs.copy(
                    reassignEvery = reassignEvery,
                    nextReassignment = Clock.System.now().plus(1, reassignEvery, TimeZone.currentSystemDefault())
                ))
            }
        }
    }
}
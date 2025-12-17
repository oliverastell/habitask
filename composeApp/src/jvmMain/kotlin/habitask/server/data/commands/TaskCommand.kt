package habitask.server.data.commands

import habitask.common.util.toDateTimeUnit
import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext


fun CommandContext.taskCommand(
    backend: ServerBackend
) = word("task") {

    word("reassign") {
        end {
            backend.reassignTasks()
        }
    }

    word("create") {
        noEnd()

        greedyText { name ->
            output("\ndescribe this task:")
            backend.requestInput { description ->
                output("\ncycles every:")
                backend.requestInput { cyclesEvery ->
                    val cyclesEvery = cyclesEvery.toDateTimeUnit()

                    output("\ndue after (duration):")
                    backend.requestInput { dueAfter ->
                        val dueAfter = dueAfter.toDateTimeUnit()

                        output("\ncreating task: $name")
                        output("description: $description")
                        output("cycles every: $cyclesEvery")
                        output("due after: $dueAfter")

                        output("\nis this correct?")

                        backend.requestInput { yesOrNo ->
                            if (yesOrNo.length in 1..4 && yesOrNo.first().lowercaseChar() == 'y') {
                                backend.dbManager.newTask(
                                    name,
                                    description,
                                    cyclesEvery,
                                    dueAfter
                                )

                                output("task created!")
                            } else {
                                output("_no_ answered. terminating.")
                            }
                        }
                    }
                }
            }
        }
    }

}
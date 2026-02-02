package habitask.server.data.commands

import habitask.common.Logger
import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext



fun CommandContext.metaCommands(backend: ServerBackend) {
    word("meta") {
        word("help") {
            end {
                output("meta commands (do not prefix with 'meta'):")
                output("  execute [value]")
                output("")
                output("  format [var] in '[text]' with [value] .. any instance of {[var]} in [text] is replaced with [value]")
                output("   - example: format x in 'Hello {x}' with John")
                output("   - result is outputted")
                output("")
                output("  for [var] in [start]..[end] [command] .. any instance of {[var]} in [command] is repalced with [value]")
                output("   - result is executed")
                output("")
                output("echo [text] -- outputs [text]")
                output("")
                output("  NOTE: value could take the form: resultof [command], [text]")
            }
        }
    }

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
                intRange { range ->
                    greedyTextOrNextLine(backend) { command ->
                        for (i in range) {
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

    word("echo") {
        greedyTextOrNextLine(backend) { text ->
            output(text)
        }
    }
}
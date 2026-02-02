package habitask.server.data.commands

import habitask.common.data.info.EntityInfo
import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.Command
import habitask.server.data.commandengine.CommandContext
import habitask.server.data.commandengine.parseCommand

//fun CommandContext.selectGroups(
//    backend: ServerBackend,
//    limit: Int = 9999,
//    body: CommandContext.(List<GroupInfo>) -> Unit
//) {
//    token("@") {
//        word { id ->
//            val id = id.toIntOrNull()
//            if (id != null) {
//                val group = backend.dbManager.getGroupById(id)
//                require(group != null) { "No group found matching predicate" }
//                body(listOf(group))
//            }
//        }
//    }
//
//    word { word ->
//        val word = if (word == "*") "%" else word
//
//        val groups = backend.dbManager.getEntitiesByName(word, limit = limit+1)
//        require(groups.isNotEmpty()) { "No group found matching predicate" }
//        require(groups.size <= limit) { "Too many groups matching this predicate" }
//        body(groups)
//    }
//}

fun CommandContext.intRange(body: CommandContext.(IntRange) -> Unit) {
    integer { start ->
        token("..") {
            integer { end ->
                body(start..end)
            }
        }
    }
}

fun CommandContext.selectEntities(
    backend: ServerBackend,
    limit: Int = 9999,
    body: CommandContext.(List<EntityInfo>) -> Unit
) {
    token("@") {
        intRange { ids ->
            val entities = ids.mapNotNull { backend.dbManager.getEntityById(it) }
            require(entities.isNotEmpty()) { "No entity found matching predicate" }
            body(entities)
        }

        integer { id ->
            val entity = backend.dbManager.getEntityById(id)
            require(entity != null) { "No entity found matching predicate" }
            body(listOf(entity))
        }
    }

    word { word ->
        val word = if (word == "*") "%" else word

        val accounts = backend.dbManager.getEntitiesByName(word)
        require(accounts.isNotEmpty()) { "No entity found matching predicate" }
        require(accounts.size <= limit) { "Too many entities matching this predicate (limit=1)" }
        body(accounts)
    }
}

fun CommandContext.value(backend: ServerBackend, body: (Any) -> Unit) {
    word("resultof") {
        end {
            body("resultof")
        }

        word { command ->
            backend.executeCommand(command, onOutput = { message ->
                body(message)
            })
        }
    }

    word { value ->
        body(value)
    }
}

fun CommandContext.greedyValue(backend: ServerBackend, body: (Any) -> Unit) {
    word("resultof") {
        end {
            body("resultof")
        }

        greedyCommandOrNextline(backend) { command ->
            backend.executeCommand(command, onOutput = { message ->
                body(message)
            })
        }
    }
    greedyTextOrNextLine(backend) { value ->
        body(value)
    }
}

private fun CommandContext.flagsRecursor(flags: List<String>, body: CommandContext.(List<String>) -> Unit) {
    word { flag ->
        if (flag.startsWith("--")) {
            val flag = flag.substring(2).lowercase()

            flagsRecursor(flags + flag, body)
        }
    }

    body(flags)
}

fun CommandContext.flags(body: CommandContext.(list: List<String>) -> Unit) {
    flagsRecursor(listOf(), body)
}

fun CommandContext.greedyTextOrNextLine(backend: ServerBackend, body: CommandContext.(text: String) -> Unit) {
    end {
        backend.requestInput { text ->
            body(text)
        }
    }

    greedyText { text ->
        body(text)
    }
}

fun CommandContext.greedyCommandOrNextline(backend: ServerBackend, body: CommandContext.(command: Command) -> Unit) {
    end {
        backend.requestInput { text ->
            body(parseCommand(text))
        }
    }

    greedySubCommand { text ->
        body(text)
    }
}


package habitask.server.data.commands

import habitask.common.Logger
import habitask.common.data.info.EntityType
import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext

fun CommandContext.entityCommand(backend: ServerBackend) = word("entity") {
    word("create") {
        greedyText { name ->
            if (name.contains("@")) {
                Logger.error("Group names containing @ are reserved")
                return@greedyText
            }
            backend.dbManager.newEntity(name, null, EntityType.Group)
        }
    }

    word("list") {
        end {
            val groups = backend.dbManager.getEntities()

            groups.forEach { groupInfo ->
                output(groupInfo)
            }
        }
    }
}
package habitask.server.data.commands

import habitask.common.Logger
import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext



fun CommandContext.moveCommand(
    backend: ServerBackend
) = word("move") {
    selectEntities(backend) { entities ->
        word("into") {
            word("root") {
                end {
                    for (entity in entities) {
                        backend.dbManager.setEntityParent(entity.id, null)
                    }
                }
            }

            selectEntities(backend, limit = 1) { parents ->
                end {
                    val parent = parents.first()
                    for (entity in entities) {

                        if (backend.isEntityDescendantOf(parent.id, entity.id))
                            error("Circular relationships are not permitted")

                        backend.dbManager.setEntityParent(entity.id, parent.id)
                    }
                }
            }
        }
    }
}
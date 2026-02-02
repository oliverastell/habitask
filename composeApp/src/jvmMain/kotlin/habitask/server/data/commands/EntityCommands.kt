package habitask.server.data.commands

import habitask.common.Logger
import habitask.common.data.info.EntityInfo
import habitask.common.data.info.EntityInfo.EntityType
import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext


fun setEntityParentChecked(backend: ServerBackend, entity: EntityInfo, parentEntity: EntityInfo) {
    if (parentEntity.id == entity.id)
        return

    if (backend.isEntityDescendantOf(parentEntity.id, entity.id))
        backend.dbManager.setEntityParent(parentEntity.id, entity.parent)

    backend.dbManager.setEntityParent(entity.id, parentEntity.id)
}


fun CommandContext.entityCommands(
    backend: ServerBackend
) = word("entity") {

    word("help") {
        end {
            output("move command:")
            output("  move [child entities] into [parent entity]")
            output("")
            output("  create [name] ................ creates a dummy entity (aka a group)")
            output("")
            output("  NOTE: entity could take the form: @[id], [exact name], * (* means all entities)")
        }
    }

    word("create") {
        greedyText { name ->
            if (name.contains("@")) {
                Logger.error("Cannot name an entity with @")
                return@greedyText
            }
            backend.dbManager.newEntity(name, null, EntityType.Group)
        }
    }

    word("move") {
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
                            setEntityParentChecked(backend, entity, parent)
                        }
                    }
                }
            }
        }
    }
}



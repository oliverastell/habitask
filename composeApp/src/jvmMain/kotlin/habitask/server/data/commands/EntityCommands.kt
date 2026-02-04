package habitask.server.data.commands

import habitask.common.Logger
import habitask.common.data.info.EntityInfo
import habitask.common.data.info.EntityInfo.EntityType
import habitask.server.data.ServerBackend
import habitask.server.data.commandengine.CommandContext
import habitask.server.data.commands.selectEntities


fun setEntityParentChecked(backend: ServerBackend, entity: EntityInfo, parentEntity: EntityInfo) {
    if (parentEntity.id == entity.id)
        return

    if (backend.isEntityDescendantOf(parentEntity.id, entity.id))
        backend.dbManager.setEntityParent(parentEntity.id, entity.parent)

    backend.dbManager.setEntityParent(entity.id, parentEntity.id)
}

private fun CommandContext.moveCommand(
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
                        setEntityParentChecked(backend, entity, parent)
                    }
                }
            }
        }
    }
}


// Annotated for IA documentation
fun CommandContext.entityCommands(
    backend: ServerBackend
) {
    moveCommand(backend)

    word("entity") {
        // CommandContext.word("entity") checks for the word "entity"
        // This inner branch is called if the word is met

        word("help") {

            // Defines an endpoint. Only one endpoint can be called per command execution.
            // end {} checks if the command has no more tokens left to be consumed
            end {
                output("move command:")
                output("  move [child entities] into root")
                output("  move [child entities] into [parent entity]")
                output("")
                output("  create [name] ................ creates a dummy entity (aka a group)")
                output("")
                output("  NOTE: entity could take the following forms:")
                output("    * ------------- selects all")
                output("    @[id] --------- selects an entity by id")
                output("    [exact name] -- selects an entity by name")
            }
        }

        word("create") {

            // greedyText is another endpoint. Converts any remaining tokens(?) back into a string.
            greedyText { name ->
                if (name.contains("@")) {
                    Logger.error("Cannot name an entity with @")
                    return@greedyText
                }
                backend.dbManager.newEntity(name, null, EntityType.Group)
            }
        }

        moveCommand(backend)
    }
}


package habitask.server.data.h2.tables

import habitask.common.data.info.EntityType
import org.jetbrains.exposed.dao.id.IntIdTable



object EntityTable : IntIdTable("entity") {
    val name = text("name")
    val parent = reference("parent", EntityTable.id).nullable()
    val entityType = enumeration<EntityType>("entity_type")
}
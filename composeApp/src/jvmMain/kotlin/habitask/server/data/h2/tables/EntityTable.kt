package habitask.server.data.h2.tables

import habitask.common.data.info.EntityInfo
import habitask.common.data.info.EntityInfo.EntityType
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow


object EntityTable : IntIdTable("entity") {
    val name = text("name")
    val parent = reference("parent", EntityTable.id).nullable()
    val entityType = enumeration<EntityType>("entity_type")
}

fun ResultRow.toEntityInfo() = EntityInfo(
    id = get(EntityTable.id).value,
    name = get(EntityTable.name),
    parent = get(EntityTable.parent)?.value,
    entityType = get(EntityTable.entityType)
)
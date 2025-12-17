package habitask.server.data.h2.tables

import org.jetbrains.exposed.sql.Table

object AccessTable : Table("account_access") {
    val token = text("token").uniqueIndex()
    val entityId = reference("account_id", EntityTable.id)
}
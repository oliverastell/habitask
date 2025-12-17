package habitask.server.data.h2.tables

import habitask.server.data.h2.custom.datetimeunit
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object ServerConfigTable : Table("server_config") {
    val key = char("key").uniqueIndex().default('k')
    val name = text("name")
    val port = integer("port")
    val reassignEvery = datetimeunit("reassignevery")
}
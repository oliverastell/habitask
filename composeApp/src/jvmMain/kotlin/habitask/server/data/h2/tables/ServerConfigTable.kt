package habitask.server.data.h2.tables

import habitask.server.data.ServerConfigs
import habitask.server.data.h2.custom.datetimeunit
import habitask.server.data.h2.custom.instant
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import kotlin.time.ExperimentalTime

object ServerConfigTable : Table("server_config") {
    val key = char("key").uniqueIndex().default('k')
    val name = text("name")
    val port = integer("port")
    val reassignEvery = datetimeunit("reassign_every")
    val nextReassignment = instant("next_reassignment")
}

@OptIn(ExperimentalTime::class)
fun ResultRow.toServerConfigs() = ServerConfigs(
    name = get(ServerConfigTable.name),
    port = get(ServerConfigTable.port),
    reassignEvery = get(ServerConfigTable.reassignEvery),
    nextReassignment = get(ServerConfigTable.nextReassignment)
)
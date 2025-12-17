package habitask.server.data.h2.tables

import habitask.server.data.h2.custom.datetimeunit
import org.jetbrains.exposed.dao.id.IntIdTable

object TaskTable : IntIdTable("task") {
    val name = text("name")
    val description = text("description")
    val cycleEvery = datetimeunit("cycle_every")
    val dueAfter = datetimeunit("due_after")
}
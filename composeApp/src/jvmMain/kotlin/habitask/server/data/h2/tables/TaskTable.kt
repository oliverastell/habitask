package habitask.server.data.h2.tables

import habitask.common.data.info.TaskInfo
import habitask.server.data.h2.custom.datetimeunit
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object TaskTable : IntIdTable("task") {
    val name = text("name")
    val description = text("description")
    val cycleEvery = datetimeunit("cycle_every")
    val dueAfter = datetimeunit("due_after")
}

fun ResultRow.toTaskInfo() = TaskInfo(
    id = get(TaskTable.id).value,
    name = get(TaskTable.name),
    description = get(TaskTable.description),
    cycleEvery = get(TaskTable.cycleEvery),
    dueAfter = get(TaskTable.dueAfter)
)
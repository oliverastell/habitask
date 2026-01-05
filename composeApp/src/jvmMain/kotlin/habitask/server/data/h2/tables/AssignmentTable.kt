package habitask.server.data.h2.tables

import habitask.common.data.info.AssignmentInfo
import habitask.server.data.h2.custom.instant
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object AssignmentTable : IntIdTable("task_assignment") {
    val taskId = reference("task_id", TaskTable.id)
    val entityId = reference("entity_id", EntityTable.id).nullable()
    val dueTime = instant("due_time")
}

fun ResultRow.toAssignmentInfo() = AssignmentInfo(
    id = get(AssignmentTable.id).value,
    entityId = get(AssignmentTable.entityId)?.value,
    taskId = get(AssignmentTable.taskId).value,
    dueTime = get(AssignmentTable.dueTime),
)
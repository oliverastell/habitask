package habitask.server.data.h2.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object TaskAssignmentTable : IntIdTable("task_assignment") {
    val taskId = reference("task_id", TaskTable.id)
    val entityId = reference("group_id", EntityTable.id)
    val dueTime = timestamp("due_time")
}
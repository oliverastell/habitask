package habitask.server.data.h2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import habitask.server.data.h2.tables.AccessTable
import habitask.server.data.h2.tables.TaskAssignmentTable
import habitask.server.data.h2.tables.TaskTable
import habitask.common.data.info.AccessInfo
import habitask.common.data.info.EntityInfo
import habitask.common.data.info.EntityType
import habitask.common.data.info.TaskAssignmentInfo
import habitask.common.util.toByteArray
import habitask.common.data.info.TaskInfo
import habitask.server.data.h2.tables.ServerConfigTable
import habitask.server.data.ServerConfigs
import habitask.server.data.h2.custom.replace
import habitask.server.data.h2.tables.EntityTable
import kotlinx.datetime.DateTimeUnit
import kotlinx.io.bytestring.buildByteString
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class DatabaseManager(val db: Database) {
    var entityTableChanged by mutableIntStateOf(0)
    var accessTableChanged by mutableIntStateOf(0)
    var taskAssignmentTableChanged by mutableIntStateOf(0)
    var tasksTableTableChanged by mutableIntStateOf(0)
    var serverConfigTableChanged by mutableIntStateOf(0)

    fun initializeTables() {
        transaction(db) {
            SchemaUtils.create(EntityTable)
            SchemaUtils.create(AccessTable)
            SchemaUtils.create(TaskAssignmentTable)
            SchemaUtils.create(TaskTable)
            SchemaUtils.create(ServerConfigTable)
        }
    }

    private fun <T> trans(statement: Transaction.() -> T): T = transaction(db, statement)

    // Converters
    fun ResultRow.toEntityInfo() = EntityInfo(
        id = get(EntityTable.id).value,
        name = get(EntityTable.name),
        parent = get(EntityTable.parent)?.value,
        entityType = get(EntityTable.entityType)
    )

    fun ResultRow.toTaskAssignmentInfo() = TaskAssignmentInfo(
        id = get(TaskAssignmentTable.id).value,
        entityId = get(TaskAssignmentTable.entityId).value,
        taskId = get(TaskAssignmentTable.taskId).value,
        dueTime = get(TaskAssignmentTable.dueTime),
    )

    fun ResultRow.toTaskInfo() = TaskInfo(
        id = get(TaskTable.id).value,
        name = get(TaskTable.name),
        description = get(TaskTable.description),
        cycleEvery = get(TaskTable.cycleEvery),
        dueAfter = get(TaskTable.dueAfter)
    )

    @OptIn(ExperimentalTime::class)
    fun ResultRow.toServerConfigs() = ServerConfigs(
        name = get(ServerConfigTable.name),
        port = get(ServerConfigTable.port),
        reassignEvery = get(ServerConfigTable.reassignEvery),
    )

    fun getEntityById(id: Int): EntityInfo? =
        trans {
            EntityTable
                .selectAll()
                .where { EntityTable.id eq id }
                .firstOrNull()
        }?.toEntityInfo()

    fun getEntityByToken(token: String): EntityInfo? =
        trans {
            (AccessTable leftJoin EntityTable)
                .selectAll()
                .where { AccessTable.token eq token }
                .firstOrNull()
        }?.toEntityInfo()

    fun getEntitiesByName(
        name: String,
        ignoreCase: Boolean = true,
        ignoreWhitespace: Boolean = true,
        limit: Int? = null
    ): List<EntityInfo> =
        trans {
            EntityTable
                .selectAll()
                .apply {
                    where {
                        EntityTable.name.apply {
                            if (ignoreCase)
                                lowerCase()
                        }.apply {
                            if (ignoreWhitespace)
                                replace(" ", "")
                                    .replace("\t", "")
                                    .replace("\n", "")
                                    .replace("\r", "")
                        } like name.apply {
                            if (ignoreCase)
                                lowercase()
                        }.apply {
                            if (ignoreWhitespace)
                                replace(" ", "")
                                    .replace("\t", "")
                                    .replace("\n", "")
                                    .replace("\r", "")
                        }
                    }
                    if (limit != null)
                        limit(limit)
                }
                .map { it.toEntityInfo() }
        }

    // Getters
    fun getEntities() =
        trans {
            EntityTable
                .selectAll()
                .map { it.toEntityInfo() }
        }

    fun getEntitiesWithParent(parentId: Int?) =
        trans {
            EntityTable
                .selectAll()
                .where { EntityTable.parent eq parentId }
                .map { it.toEntityInfo() }
        }

    fun getTasks() =
        trans {
            TaskTable
                .selectAll()
                .map { it.toTaskInfo() }
        }

    fun getTaskAssignmentsByEntityId(entityId: Int) =
        trans {
            TaskAssignmentTable
                .selectAll()
                .where { TaskAssignmentTable.entityId eq entityId }
                .map { it.toTaskAssignmentInfo() }
        }

//    fun getTaskAssignmentsByGroupId(groupId: Int) =
//        transaction(db) {
//            TaskAssignmentTable
//                .selectAll()
//                .where { TaskAssignmentTable.groupId eq groupId }
//                .toList()
//        }.map { it.toTaskAssignmentInfo() }

    fun getTaskById(taskId: Int) =
        trans {
            TaskTable
                .selectAll()
                .where { TaskTable.id eq taskId }
                .first()
        }.toTaskInfo()

//    fun getAccountsByGroupId(groupId: Int): List<AccountInfo> =
//        transaction(db) {
//            AccountTable.selectAll().where {
//                AccountTable.groupId eq groupId
//            }.toList()
//        }.map { it.toAccountInfo() }
//
//    @OptIn(ExperimentalTime::class)
//    fun getActiveTasksByGroupId(groupId: Int): List<TaskAssignmentInfo> =
//        transaction(db) {
//            (TaskTable leftJoin TaskAssignmentTable)
//                .selectAll()
//                .where(TaskAssignmentTable.groupId eq groupId)
//                .toList()
//        }.map { it.toTaskAssignmentInfo() }

    fun getAssignedTaskByEntityId(id: Int): List<TaskAssignmentInfo> =
        trans {
            (TaskTable leftJoin TaskAssignmentTable)
                .selectAll()
                .where(TaskAssignmentTable.entityId eq id)
                .map { it.toTaskAssignmentInfo() }
        }

    fun getServerConfigs(): ServerConfigs =
        trans {
            ServerConfigTable.selectAll().first()
        }.toServerConfigs()

    fun areServerConfigsSetup(): Boolean =
        trans {
            ServerConfigTable.selectAll().firstOrNull()
        } != null

    // Replacement
//    fun setAccountGroup(accountInfo: AccountInfo, groupInfo: GroupInfo) {
//        transaction(db) {
//            AccountTable.update({ AccountTable.id eq accountInfo.id }) { statement ->
//                statement[AccountTable.groupId] = groupInfo.id
//            }
//        }
//        groupTableChanged++
//    }

    fun setEntityParent(entityId: Int, parentEntityId: Int?) {
        trans {
            EntityTable.update({ EntityTable.id eq entityId }) { statement ->
                statement[EntityTable.parent] = parentEntityId
            }
        }

        entityTableChanged++
    }

    fun setServerConfigs(serverConfigs: ServerConfigs) {
        trans {
            ServerConfigTable.upsert {
                it[ServerConfigTable.key] = 'k'
                it[ServerConfigTable.name] = serverConfigs.name
                it[ServerConfigTable.port] = serverConfigs.port
                it[ServerConfigTable.reassignEvery] = serverConfigs.reassignEvery
            }
        }

        serverConfigTableChanged++
    }

    // Creation
    @OptIn(ExperimentalTime::class)
    fun newAccountAccess(
        accountId: Int
    ): AccessInfo {
        val epoch = Clock.System.now().epochSeconds
        val nanos = Clock.System.now().nanosecondsOfSecond

        val tokenBytes = buildByteString {
            append(accountId.toByteArray())
            append(epoch.toByteArray())
            append(entityTableChanged.toByteArray())
            append(nanos.hashCode().toByteArray())
        }

        val token = Base64.withPadding(Base64.PaddingOption.ABSENT).encode(tokenBytes.toByteArray())

        trans {
            AccessTable.insert {
                it[AccessTable.token] = token
                it[AccessTable.entityId] = accountId
            }
        }

        entityTableChanged++

        return AccessInfo(
            token = token,
            accountId = accountId
        )
    }

    fun newEntity(
        name: String,
        parent: Int?,
        entityType: EntityType,
    ): EntityInfo {
        val id = transaction(db) {
            EntityTable.insertAndGetId {
                it[EntityTable.name] = name
                it[EntityTable.parent] = parent
                it[EntityTable.entityType] = entityType
            }
        }

        entityTableChanged++

        return EntityInfo(
            id.value,
            name,
            parent,
            entityType
        )
    }

    @OptIn(ExperimentalTime::class)
    fun newTaskAssignment(
        taskId: Int,
        entityId: Int,
        dueTime: Instant
    ): TaskAssignmentInfo {
        val id = transaction(db) {
            TaskAssignmentTable.insertAndGetId {
                it[TaskAssignmentTable.taskId] = taskId
                it[TaskAssignmentTable.entityId] = entityId
                it[TaskAssignmentTable.dueTime] = dueTime
            }
        }

        taskAssignmentTableChanged++

        return TaskAssignmentInfo(
            id = id.value,
            taskId = taskId,
            entityId = entityId,
            dueTime = dueTime
        )
    }

    fun newTask(
        name: String,
        description: String,
        cycleEvery: DateTimeUnit,
        dueAfter: DateTimeUnit
    ): TaskInfo {
         val id = transaction(db) {
             TaskTable.insertAndGetId {
                 it[TaskTable.name] = name
                 it[TaskTable.description] = description
                 it[TaskTable.cycleEvery] = cycleEvery
                 it[TaskTable.dueAfter] = dueAfter
             }
         }

        tasksTableTableChanged++

        return TaskInfo(
            id = id.value,
            name = name,
            description = description,
            cycleEvery = cycleEvery,
            dueAfter = dueAfter
        )
    }

    // Deletion
    fun deleteEntity(id: Int) {
        trans {
            AccessTable.deleteWhere {
                AccessTable.entityId eq id
            }
            EntityTable.update({ EntityTable.parent eq id }) { statement ->
                statement[EntityTable.parent] = null
            }
            EntityTable.deleteWhere(1) {
                EntityTable.id eq id
            }
        }

        entityTableChanged++
    }

    fun deleteAllTaskAssignments() {
        trans {
            TaskAssignmentTable.deleteAll()
        }

        taskAssignmentTableChanged++
    }

    fun deleteTaskAssignment(id: Int) {
        trans {
            TaskAssignmentTable.deleteWhere {
                TaskAssignmentTable.id eq id
            }
        }

        taskAssignmentTableChanged++
    }
}
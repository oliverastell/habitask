package habitask.server.data.h2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import habitask.server.data.h2.tables.AccessTable
import habitask.server.data.h2.tables.AssignmentTable
import habitask.server.data.h2.tables.TaskTable
import habitask.common.data.info.AccessInfo
import habitask.common.data.info.EntityInfo
import habitask.common.data.info.EntityInfo.EntityType
import habitask.common.data.info.AssignmentInfo
import habitask.common.util.toByteArray
import habitask.common.data.info.TaskInfo
import habitask.server.data.h2.tables.ServerConfigTable
import habitask.server.data.ServerConfigs
import habitask.server.data.filemanagers.ServerFileManager
import habitask.server.data.h2.custom.replace
import habitask.server.data.h2.tables.EntityTable
import habitask.server.data.h2.tables.toAssignmentInfo
import habitask.server.data.h2.tables.toEntityInfo
import habitask.server.data.h2.tables.toServerConfigs
import habitask.server.data.h2.tables.toTaskInfo
import kotlinx.datetime.DateTimeUnit
import kotlinx.io.bytestring.buildByteString
import org.jetbrains.exposed.sql.Expression
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
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class DatabaseManager(val fileManager: ServerFileManager) {
    var entityTableChanged by mutableIntStateOf(0)
    var accessTableChanged by mutableIntStateOf(0)
    var assignmentTableChanged by mutableIntStateOf(0)
    var tasksTableTableChanged by mutableIntStateOf(0)
    var serverConfigTableChanged by mutableIntStateOf(0)

    fun initializeTables() {
        trans {
            SchemaUtils.create(EntityTable)
            SchemaUtils.create(AccessTable)
            SchemaUtils.create(AssignmentTable)
            SchemaUtils.create(TaskTable)
            SchemaUtils.create(ServerConfigTable)
        }
    }

    private fun <T> trans(statement: Transaction.() -> T): T = transaction(fileManager.database!!, statement)

    // Converters


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
        ignoreWhitespace: Boolean = true
    ): List<EntityInfo> =
        trans {
            EntityTable
                .selectAll()
                .where {
                    var column: Expression<String> = EntityTable.name
                    var searchName = name

                    if (ignoreCase) {
                        column = column.lowerCase()
                        searchName = name.lowercase()
                    }

                    if (ignoreWhitespace) {
                        column = column
                            .replace(Regex("\\s+"), "")
                        searchName = searchName
                            .replace(Regex("\\s+"), "")
                    }

                    column like searchName
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

    fun getAssignmentsByEntityId(entityId: Int?) =
        trans {
            AssignmentTable
                .selectAll()
                .where { AssignmentTable.entityId eq entityId }
                .map { it.toAssignmentInfo() }
        }

    fun getTaskById(taskId: Int) =
        trans {
            TaskTable
                .selectAll()
                .where { TaskTable.id eq taskId }
                .firstOrNull()
        }?.toTaskInfo()

    fun getAssignmentById(assignmentId: Int) =
        trans {
            AssignmentTable
                .selectAll()
                .where { AssignmentTable.id eq assignmentId }
                .firstOrNull()
        }?.toAssignmentInfo()

    fun getServerConfigs(): ServerConfigs =
        trans {
            ServerConfigTable.selectAll().first()
        }.toServerConfigs()

    fun areServerConfigsSetup(): Boolean =
        trans {
            ServerConfigTable.selectAll().firstOrNull()
        } != null

    // Replacement
    fun setEntityParent(entityId: Int, parentEntityId: Int?) {
        trans {
            EntityTable.update({ EntityTable.id eq entityId }) { statement ->
                statement[EntityTable.parent] = parentEntityId
            }
        }

        entityTableChanged++
    }

    fun setAssignmentEntity(assignmentId: Int, entityId: Int?) {
        trans {
            AssignmentTable.update({ AssignmentTable.id eq assignmentId }) { statement ->
                statement[AssignmentTable.entityId] = entityId
            }
        }

        assignmentTableChanged++
    }

    fun setServerConfigs(serverConfigs: ServerConfigs) {
        trans {
            ServerConfigTable.upsert {
                it[ServerConfigTable.key] = 'k'
                it[ServerConfigTable.name] = serverConfigs.name
                it[ServerConfigTable.port] = serverConfigs.port
                it[ServerConfigTable.reassignEvery] = serverConfigs.reassignEvery
                it[ServerConfigTable.nextReassignment] = serverConfigs.nextReassignment
            }
        }

        serverConfigTableChanged++
    }

    // Creation
    @OptIn(ExperimentalTime::class)
    fun newAccess(
        accountId: Int
    ): AccessInfo {
        // this is not secure whatsoever but it works

        val epoch = Clock.System.now().epochSeconds
        val nanos = Clock.System.now().nanosecondsOfSecond

        val tokenBytes = buildByteString {
            append((accountId+1000000000).toByteArray())
            append(Random.nextBytes(2))
            append((epoch+1000000000).toByteArray())
            append(Random.nextBytes(2))
            append(entityTableChanged.toByteArray())
            append(Random.nextBytes(2))
            append((nanos+1000000000).hashCode().toByteArray())
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
        val id = trans {
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
    fun newAssignment(
        taskId: Int,
        entityId: Int?,
        dueTime: Instant,
        cycleTime: Instant
    ): AssignmentInfo {
        val id = trans {
            AssignmentTable.insertAndGetId {
                it[AssignmentTable.taskId] = taskId
                it[AssignmentTable.entityId] = entityId
                it[AssignmentTable.dueTime] = dueTime
                it[AssignmentTable.cycleTime] = cycleTime
            }
        }

        assignmentTableChanged++

        return AssignmentInfo(
            id = id.value,
            taskId = taskId,
            entityId = entityId,
            dueTime = dueTime,
            cycleTime = cycleTime
        )
    }

    fun newTask(
        name: String,
        description: String,
        cycleEvery: DateTimeUnit,
        dueAfter: DateTimeUnit
    ): TaskInfo {
         val id = trans {
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
            AssignmentTable.update({ AssignmentTable.entityId eq id }) { statement ->
                statement[AssignmentTable.entityId] = null
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

    fun deleteAllAssignments() {
        trans {
            AssignmentTable.deleteAll()
        }

        assignmentTableChanged++
    }

    fun deleteAssignment(id: Int) {
        trans {
            AssignmentTable.deleteWhere {
                AssignmentTable.id eq id
            }
        }

        assignmentTableChanged++
    }
}
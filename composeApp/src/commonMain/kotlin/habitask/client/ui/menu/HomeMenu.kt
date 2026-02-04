package habitask.client.ui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import habitask.client.data.ClientController
import habitask.client.data.Connection
import habitask.common.Logger
import habitask.common.data.info.ServerInfo
import habitask.common.data.info.AssignmentInfo
import habitask.client.ui.elements.TaskCard
import habitask.common.data.info.TaskInfo
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime

// Triple<Connection, AssignmentInfo, TaskInfo>
private data class AssignmentMeta(
    val connection: Connection,
    val assignmentInfo: AssignmentInfo,
    val taskInfo: TaskInfo
)
private suspend fun getFullAssignmentMeta(clientController: ClientController, connection: Connection): List<AssignmentMeta> {
    return clientController.getAssignments(connection).map {
        AssignmentMeta(connection, it, clientController.getTaskById(connection, it.taskId))
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun UpcomingTasksList(clientController: ClientController) {
    val tasks = remember { mutableStateListOf<AssignmentMeta>() }

    LaunchedEffect(Unit) {
        val connections = clientController.fm.getConnections()
        tasks.clear()

        for (connection in connections) {
            clientController.updateConnection(connection)
            if (clientController.isServerOffline(connection)) {
                Logger.debug("server offline")
                continue
            }

            if (!clientController.isAuthenticated(connection)) {
                Logger.debug("not authenticated")
                continue
            }

            tasks += getFullAssignmentMeta(clientController, connection)
        }
    }

    tasks.sortBy { it.assignmentInfo.dueTime }

    LazyColumn(
        reverseLayout = true
    ) {
        itemsIndexed(tasks) { i, assignmentMeta ->
            var show by remember { mutableStateOf(true) }

            AnimatedVisibility(show) {
                TaskCard(
                    name = assignmentMeta.taskInfo.name,
                    dueTime = assignmentMeta.assignmentInfo.dueTime,
                    description = assignmentMeta.taskInfo.description,
                    onComplete = {
                        runBlocking {
                            val success = clientController.completeAssignment(
                                assignmentMeta.connection,
                                assignmentMeta.assignmentInfo.id
                            )

                            Logger.debug("success? $success")

                            if (success) show = false
                        }
                    },
                    outsourced = assignmentMeta.assignmentInfo.entityId == null,
                    onOutsource = {
                        runBlocking {
                            val success = clientController.outsourceAssignment(
                                assignmentMeta.connection,
                                assignmentMeta.assignmentInfo.id
                            )

                            if (success) show = false
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}


@Composable
private fun ServerTasksList(clientController: ClientController) {}

enum class GroupBy {
    Server,
    Upcoming
}

@OptIn(ExperimentalTime::class)
@Composable
fun HomeMenu(
    clientController: ClientController,
    modifier: Modifier = Modifier
) {
    var groupBy by remember { mutableStateOf(GroupBy.Upcoming) }

    HomeAndServerMenuCommon(
        modifier = modifier.fillMaxSize().padding(16.dp),
        content = {
            when (groupBy) {
                GroupBy.Server ->
                    ServerTasksList(clientController)
                GroupBy.Upcoming ->
                    UpcomingTasksList(clientController)
            }
        },
        rowContent = {
            TextButton(onClick = { groupBy = GroupBy.Server }) {
                Text("Group by Server")
            }
            TextButton(onClick = { groupBy = GroupBy.Upcoming}) {
                Text("Upcoming")
            }
        }
    )
}
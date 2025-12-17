package habitask.client.ui.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import habitask.client.data.ClientController
import habitask.client.data.Connection
import habitask.common.Logger
import habitask.common.data.info.ServerInfo
import habitask.common.data.info.TaskAssignmentInfo
import habitask.client.ui.elements.TaskCard
import habitask.common.data.info.TaskInfo
import kotlin.collections.getValue
import kotlin.collections.setValue
import kotlin.time.ExperimentalTime

enum class GroupBy {
    Server,
    Upcoming
}

private suspend fun getTaskAssignmentsAndInfo(clientController: ClientController, connection: Connection): List<Pair<TaskAssignmentInfo, TaskInfo>> {
    return clientController.getActiveTasks(connection).map {
        it to clientController.getTaskById(connection, it.taskId)
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun UpcomingTasksList(clientController: ClientController) {
    val tasks = remember { mutableStateListOf<Pair<TaskAssignmentInfo, TaskInfo>>() }

    LaunchedEffect(Unit) {
        val connections = clientController.fm.getConnections()
        tasks.clear()

        for (connection in connections) {
            clientController.updateConnection(connection)
            if (clientController.isServerOffline(connection)) {
                Logger.debug("server offline")
                continue
            }

            tasks += getTaskAssignmentsAndInfo(clientController, connection)
        }
    }

    tasks.sortBy { it.first.dueTime }

    LazyColumn(
        reverseLayout = true
    ) {
        items(tasks) { task ->
            TaskCard(
                name = task.second.name,
                dueTime = task.first.dueTime,
                description = task.second.description
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ServerTasksList(clientController: ClientController) {
    val tasks = remember { mutableStateMapOf<ServerInfo, List<Pair<TaskAssignmentInfo, TaskInfo>>>() }

    LaunchedEffect(Unit) {
        val connections = clientController.fm.getConnections()

        tasks.clear()

        for (connection in connections) {
            clientController.updateConnection(connection)
            if (clientController.isServerOffline(connection)) {
                Logger.debug("server offline")
                continue
            }

            val serverInfo = clientController.getServerInfo(connection)
            tasks[serverInfo] = getTaskAssignmentsAndInfo(clientController, connection)
        }
    }

    LazyColumn(
        reverseLayout = true
    ) {
        items(tasks.toList()) { (serverInfo, tasks) ->
            this@LazyColumn.items(tasks) { task ->
                TaskCard(
                    name = task.second.name,
                    dueTime = task.first.dueTime,
                    description = task.second.description
                )
            }
            Text(serverInfo.name)
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun HomeMenu(
    clientController: ClientController,
    modifier: Modifier = Modifier
) {
    var groupBy by remember { mutableStateOf(GroupBy.Server) }

    HomeAndServerMenuCommon(
        modifier = modifier.fillMaxSize(),
        content = {
            when (groupBy) {
                GroupBy.Server ->
                    ServerTasksList(clientController)
                GroupBy.Upcoming ->
                    UpcomingTasksList(clientController)
            }
        },
        rowContent = {
            TextButton(onClick = {}) {
                Text("Group by Server")
            }
            TextButton(onClick = {}) {
                Text("Upcoming")
            }
        }
    )
}
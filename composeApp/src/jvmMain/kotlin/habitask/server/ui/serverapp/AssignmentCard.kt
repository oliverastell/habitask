package habitask.server.ui.serverapp

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import habitask.common.data.info.AssignmentInfo
import habitask.common.data.info.TaskInfo
import habitask.resources.Res
import habitask.resources.assignment_filled
import habitask.server.data.ServerBackend
import org.jetbrains.compose.resources.painterResource


@Composable
fun AssignmentCard(
    serverBackend: ServerBackend,
    assignmentInfo: AssignmentInfo,
    taskInfo: TaskInfo,
    indentation: Dp = 0.dp
) {
    Row {
        Spacer(Modifier.width(indentation))
        Icon(
            painterResource(Res.drawable.assignment_filled),
            null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.width(8.dp))
        Text(taskInfo.name, color = MaterialTheme.colorScheme.primary)

        Spacer(Modifier.width(8.dp))
        Text(
            "(@${assignmentInfo.id})",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}
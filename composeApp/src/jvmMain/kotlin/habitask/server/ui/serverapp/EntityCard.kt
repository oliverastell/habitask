package habitask.server.ui.serverapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import habitask.common.data.info.EntityInfo
import habitask.common.data.info.EntityInfo.EntityType
import habitask.resources.Res
import habitask.resources.arrow_drop_down
import habitask.resources.arrow_left
import habitask.resources.folder
import habitask.resources.folder_filled
import habitask.resources.group
import habitask.resources.group_filled
import habitask.resources.person_filled
import habitask.resources.supervised_person_filled
import habitask.server.data.ServerBackend
import org.jetbrains.compose.resources.painterResource

@Composable
fun EntityCard(
    serverBackend: ServerBackend,
    entityInfo: EntityInfo,
    indentation: Dp = 0.dp
) {
    val children = serverBackend.dbManager.getEntitiesWithParent(entityInfo.id)
    val taskAssignments = serverBackend.dbManager.getAssignmentsByEntityId(entityInfo.id)

    val expandable = children.isNotEmpty() || taskAssignments.isNotEmpty()

    var open by remember { mutableStateOf(false) }


    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("Delete") {
                    serverBackend.dbManager.deleteEntity(entityInfo.id)
                }
            )
        }
    ) {
        Row(
            modifier = Modifier.clickable { open = !open }
        ) {
            val icon = when (entityInfo.entityType) {
                EntityType.User ->
                    if (children.isEmpty()) Res.drawable.person_filled else Res.drawable.supervised_person_filled
                EntityType.Group ->
                    if (children.isEmpty()) Res.drawable.group else Res.drawable.group_filled
                EntityType.Folder ->
                    if (children.isEmpty()) Res.drawable.folder else Res.drawable.folder_filled
            }

            Spacer(Modifier.width(indentation))
            Icon(
                painterResource(icon),
                null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(8.dp))
            Text(entityInfo.name, color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.width(8.dp))
            Text(
                "(@${entityInfo.id})",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primaryContainer
            )

            Spacer(Modifier.weight(1f))

            if (expandable) {
                Icon(
                    painterResource(if (open) Res.drawable.arrow_drop_down else Res.drawable.arrow_left),
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }


    Box {
        AnimatedVisibility(
            !open && expandable,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row {
                Spacer(Modifier.width(indentation + 8.dp))
                Text("...", color = MaterialTheme.colorScheme.primaryContainer)
            }
        }

        AnimatedVisibility(
            open && expandable,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column {
                taskAssignments.fastForEach { taskAssignment ->
                    AssignmentCard(
                        serverBackend,
                        taskAssignment,
                        serverBackend.dbManager.getTaskById(taskAssignment.taskId) ?: return@fastForEach,
                        indentation = indentation + 8.dp
                    )
                }

                children.fastForEach { child ->
                    EntityCard(serverBackend, child, indentation + 8.dp)
                }
            }
        }
    }

}
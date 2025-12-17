package habitask.server.ui.serverapp

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import habitask.common.data.info.EntityInfo
import habitask.common.data.info.EntityType
import habitask.resources.Res
import habitask.resources.folder
import habitask.resources.folder_filled
import habitask.resources.group
import habitask.resources.group_filled
import habitask.resources.person_filled
import habitask.resources.supervised_person_filled
import habitask.server.data.ServerBackend
import org.jetbrains.compose.resources.painterResource


fun LazyListScope.entityCard(serverBackend: ServerBackend, entityInfo: EntityInfo, indentation: Dp = 0.dp) {
    val children = serverBackend.dbManager.getEntitiesWithParent(entityInfo.id)

    item {
        Row {
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
            Text("(@${entityInfo.id})", color = MaterialTheme.colorScheme.primaryContainer)
        }
    }

    children.fastForEach { child ->
        entityCard(serverBackend, child, indentation + 8.dp)
    }
}

//fun LazyListScope.groupCard(group: GroupInfo, accountInfos: List<AccountInfo>) {
//
//    if (group.name.contains("@")) {
//        items(accountInfos) { account ->
//            Row {
//                Icon(
//                    painterResource(Res.drawable.person_filled),
//                    null,
//                    tint = MaterialTheme.colorScheme.primary
//                )
//                Spacer(Modifier.width(8.dp))
//                Text(account.name, color = MaterialTheme.colorScheme.primary)
//                Spacer(Modifier.width(8.dp))
//                Text("(@${account.id})", color = MaterialTheme.colorScheme.primaryContainer)
//            }
//        }
//    } else {
//        item {
//            Row {
//                Icon(
//                    painterResource(Res.drawable.group_filled),
//                    null,
//                    tint = MaterialTheme.colorScheme.primary
//                )
//                Spacer(Modifier.width(8.dp))
//                Text(group.name, color = MaterialTheme.colorScheme.primary)
//                Spacer(Modifier.width(8.dp))
//                Text("(@${group.id})", color = MaterialTheme.colorScheme.primaryContainer)
//            }
//        }
//
//        item {
//            Column {
//                accountInfos.forEach { account ->
//                    Text(account.name)
//                }
//            }
//        }
//    }
//}
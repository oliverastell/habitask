package habitask.launcher.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import habitask.common.ui.Section
import habitask.launcher.ui.buttons.NewServerButton
import habitask.launcher.ui.buttons.OpenClientButton
import habitask.launcher.ui.buttons.OpenServerButton
import habitask.launcher.ui.buttons.RecentServerButton
import habitask.server.data.filemanagers.DesktopFileManager
import kotlinx.io.files.Path
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@Composable
fun LauncherApp(
    onOpenServer: (path: Path) -> Unit = {},
    onOpenClient: () -> Unit = {},
    desktopFileManager: DesktopFileManager,
) {
    val recentServers by remember { mutableStateOf(desktopFileManager.getRecentServers()) }

    Section(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            OpenClientButton(onOpenClient)
            Spacer(Modifier.height(4.dp))
            OpenServerButton(desktopFileManager, onOpenServer)
            Spacer(Modifier.height(4.dp))
            NewServerButton(desktopFileManager, onOpenServer)

            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            Box(Modifier.weight(1f)) {
                LazyColumn {
                    itemsIndexed(recentServers) { i, path ->
                        RecentServerButton(desktopFileManager, path, onOpenServer)
                    }
                }
            }
        }
    }
}
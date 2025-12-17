package habitask.launcher.ui.buttons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import habitask.launcher.ui.dialog.ServerDialog
import habitask.launcher.ui.elements.TextMenuButton
import habitask.server.data.filemanagers.DesktopFileManager
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

@Composable
fun NewServerButton(
    fm: DesktopFileManager,
    onOpenServer: (Path) -> Unit
) {
    var showPopup by remember { mutableStateOf(false) }

    TextMenuButton("New Server") {
        showPopup = true
    }

    if (showPopup)
        ServerDialog(
            onDismissRequest = { showPopup = false },
            onCheckValidity = { folderName ->
                // Folder must have name
                if (folderName.isEmpty())
                    return@ServerDialog "Name is empty"

                val path = Path(fm.paths.defaultServerDirectory, folderName)

                // If there is no meta, we can assume it's safe to create here
                val pathMeta = SystemFileSystem.metadataOrNull(path) ?: return@ServerDialog null

                // If the folder is not empty, we shouldn't create here
                if (pathMeta.isDirectory && SystemFileSystem.list(path).isNotEmpty())
                    return@ServerDialog "Folder is not empty"

                // A folder probably exists here, but it's empty so it's no worry
                return@ServerDialog null
            },
            onServerCreated = { folderName ->
                val path = Path(fm.paths.defaultServerDirectory, folderName)
                SystemFileSystem.createDirectories(path)
                onOpenServer(path)
            }
        )
}
package habitask.launcher.ui.buttons

import androidx.compose.runtime.Composable
import habitask.launcher.ui.elements.TextMenuButton
import habitask.server.data.filemanagers.DesktopFileManager
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.toKotlinxIoPath
import kotlinx.coroutines.runBlocking
import kotlinx.io.files.Path

@Composable
fun OpenServerButton(
    fm: DesktopFileManager,
    onOpenServer: (Path) -> Unit
) = TextMenuButton("Open Server") {
    runBlocking {
        val directory = FileKit.openDirectoryPicker(
            "Open Server",
            directory = PlatformFile(fm.paths.defaultServerDirectory)
        )

        if (directory != null) {
            onOpenServer(directory.toKotlinxIoPath())
        }
    }
}
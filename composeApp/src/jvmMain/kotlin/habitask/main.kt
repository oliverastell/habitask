package habitask

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLightLaf
import habitask.client.ui.ClientApp
import habitask.client.ui.theme.HabitaskTheme
import habitask.common.windows.ClientServerWindow
import habitask.common.windows.MenuWindow
import habitask.launcher.ui.LauncherApp
import habitask.server.data.filemanagers.DesktopFileManager
import habitask.server.ui.ServerApp
import kotlinx.io.files.Path
import javax.swing.UIManager

private sealed interface DesktopWindow {
    object Launcher : DesktopWindow
    object Client : DesktopWindow
    data class Server(val path: Path) : DesktopWindow
}

fun main() = application {
    UIManager.setLookAndFeel(
        if (isSystemInDarkTheme()) FlatDarkLaf() else FlatLightLaf()
    )

    val desktopDirectory = remember { DesktopFileManager.defaultManager }
    var currentWindow: DesktopWindow by remember { mutableStateOf(DesktopWindow.Launcher) }

    val openLauncher = { currentWindow = DesktopWindow.Launcher }
    val openClient = { currentWindow = DesktopWindow.Client }
    val openServer = { path: Path ->
        desktopDirectory.addRecentServer(path)
        currentWindow = DesktopWindow.Server(path)
    }

    HabitaskTheme {
        when (val currentWindow = currentWindow) {
            is DesktopWindow.Launcher -> MenuWindow {
                LauncherApp(
                    onOpenClient = openClient,
                    onOpenServer = openServer,
                    desktopFileManager = desktopDirectory
                )
            }

            is DesktopWindow.Server -> ClientServerWindow(
                "Server Terminal",
                homeButtonPressed = openLauncher,
                initialWindowSize = DpSize(1200.dp, 800.dp)
            ) { ServerApp(currentWindow.path) }


            is DesktopWindow.Client -> ClientServerWindow(
                "Client",
                homeButtonPressed = openLauncher,
                initialWindowSize = DpSize(400.dp, 800.dp)
            ) { ClientApp() }
        }
    }
}
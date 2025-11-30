package com.oliverastell.habitask

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.application
import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLightLaf
import com.oliverastell.habitask.data.ServerController
import com.oliverastell.habitask.data.classes.ServerInfo
import com.oliverastell.habitask.data.filemanagers.DesktopFileManager
import com.oliverastell.habitask.data.filemanagers.ServerFileManager
import com.oliverastell.habitask.ui.ClientApp
import com.oliverastell.habitask.ui.menuapp.MenuApp
import com.oliverastell.habitask.ui.serverapp.ServerApp
import com.oliverastell.habitask.ui.theme.HabitaskTheme
import com.oliverastell.habitask.ui.windows.ClientServerWindow
import com.oliverastell.habitask.ui.windows.MenuWindow
import kotlinx.io.files.Path
import javax.swing.UIManager

private interface DesktopWindow {
    object Menu : DesktopWindow
    object Client : DesktopWindow
    data class Server(val path: Path) : DesktopWindow
}

fun main() = application {
    UIManager.setLookAndFeel(
        if (isSystemInDarkTheme()) FlatDarkLaf() else FlatLightLaf()
    )

    val desktopDirectory = remember { DesktopFileManager.defaultManager }
    var window: DesktopWindow by remember { mutableStateOf(DesktopWindow.Menu) }

    HabitaskTheme {
        when (window) {
            is DesktopWindow.Menu -> MenuWindow {
                MenuApp(
                    onOpenClient = { window = DesktopWindow.Client },
                    onOpenServer = { path ->
                        desktopDirectory.addRecentServer(path)
                        window = DesktopWindow.Server(path)
                    },
                    desktopFileManager = desktopDirectory
                )
            }

            is DesktopWindow.Server -> {
                val workingDirectory = (window as DesktopWindow.Server).path
                val serverController = ServerController(
                    ServerFileManager(
                        workingDirectory
                    ),
                    serverInfo = ServerInfo(name = "Job server")
                )

                val runtime = serverController.newRuntime()
                runtime.start()

                ClientServerWindow(
                    title = "Habitask Server",
                    homeButtonPressed = {
                        runtime.stop()
                        window = DesktopWindow.Menu
                    }
                ) { ServerApp(workingDirectory) }
            }

            is DesktopWindow.Client -> ClientServerWindow(
                title = "Habitask Client",
                homeButtonPressed = { window = DesktopWindow.Menu }
            ) { ClientApp() }
        }
    }
}
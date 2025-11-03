package com.oliverastell.habitask

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowDecoration
import androidx.compose.ui.window.application
import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.FlatLightLaf
import com.oliverastell.habitask.menuapp.MenuApp
import com.oliverastell.habitask.serverapp.ServerApp
import io.github.numq.composedesktopwindowdecoration.decoration.WindowDecoration
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager

private interface DesktopWindow {
    object Menu : DesktopWindow
    object Client : DesktopWindow
    data class Server(val path: Path) : DesktopWindow
}

@Composable
fun Title(title: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
    ) {
        Text(
            title,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
        )
    }
}

@Composable
fun ApplicationScope.MenuWindow(
    content: @Composable () -> Unit
) = WindowDecoration(
    initialWindowSize = DpSize(300.dp, 400.dp),
    canToggleFullscreen = false,
    allDraggable = true,
    titleContent = { Title("Habitask") }
) {
    content()
}

@Composable
fun ApplicationScope.DefaultWindow(
    title: String,
    content: @Composable () -> Unit
) = WindowDecoration(
    titleContent = { Title(title) }
) {
    content()
}

fun main() = application {
    UIManager.setLookAndFeel(
        if (isSystemInDarkTheme()) FlatDarkLaf() else FlatLightLaf()
    )

    val desktopDirectory = remember { DesktopDirectory.defaultDirectory }
    var window: DesktopWindow by remember { mutableStateOf(DesktopWindow.Menu) }

    HabitaskTheme {
        when (window) {
            is DesktopWindow.Menu -> MenuWindow {
                MenuApp(
                    onOpenClient = { window = DesktopWindow.Client },
                    onOpenServer = {
                        val chooser = JFileChooser()
                        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                        chooser.currentDirectory = File(".")

                        val dialog = chooser.showOpenDialog(null)
                        if (dialog == JFileChooser.APPROVE_OPTION) {
                            window = DesktopWindow.Server(Path(chooser.selectedFile.path))
                        }
                    },
                    onOpenServerFolder = { path -> window = DesktopWindow.Server(path) },
                    workingDirectory = desktopDirectory
                )
            }
            is DesktopWindow.Server -> DefaultWindow(title="Habitask Server") { ServerApp((window as DesktopWindow.Server).path) }
            is DesktopWindow.Client -> DefaultWindow(title="Habitask Client") { App() }
        }
    }
}
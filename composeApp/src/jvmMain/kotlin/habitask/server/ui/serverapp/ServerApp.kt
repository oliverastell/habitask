package habitask.server.ui.serverapp

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import habitask.common.LogStreamBinding
import habitask.common.Logger
import habitask.server.data.ServerBackend
import habitask.server.data.filemanagers.ServerFileManager
import kotlinx.io.files.Path
import org.jetbrains.skiko.Cursor


@Composable
fun ServerApp(workingDirectory: Path) {
    val serverFileManager by remember { mutableStateOf(ServerFileManager(workingDirectory)) }
    val bindings = remember { mutableStateListOf<LogStreamBinding>() }

    val feedbackConsole = remember {
        val list = mutableStateListOf<String>()

        bindings += Logger.feedback.bindRaw { list.add(it.toString()) }
        bindings += Logger.error.bind("ERROR") { list.add(it.toString()) }

        list
    }

    val internalConsole = remember {
        val list = mutableStateListOf<String>()

        bindings += Logger.info.bind("INFO") { list.add("$it") }
        bindings += Logger.debug.bind("DEBUG") { list.add("$it") }
        bindings += Logger.warning.bind("WARNING") { list.add("$it") }
        bindings += Logger.error.bind("ERROR") { list.add("$it") }

        list
    }

    val backend by remember { mutableStateOf(ServerBackend(serverFileManager)) }

    DisposableEffect(Unit) {
        onDispose {
            backend.onClosing()
            bindings.fastForEach { it.close() }
            bindings.clear()
        }
    }

    Row {
        var ip by remember { mutableStateOf("Loading") }

        LaunchedEffect(Unit) {
            ip = backend.getPublicIp() ?: "Could not retrieve"
        }

        key(backend.dbManager.serverConfigTableChanged) {
            val configs = backend.dbManager.getServerConfigs()

            Sidebar(
                title = configs.name,
                ip = ip,
                port = configs.port.toString(),
                modifier = Modifier.fillMaxWidth(0.2f).padding(24.dp, 24.dp, 0.dp, 24.dp)
            ) {
                key(backend.dbManager.entityTableChanged) {
                    LazyColumn {
                        val rootEntities = backend.dbManager.getEntitiesWithParent(null)

                        rootEntities.forEach { entityInfo ->
                            entityCard(backend, entityInfo)
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp)
        ) {
            var width by remember { mutableIntStateOf(0) }
            val density = LocalDensity.current.density

            Row(Modifier.weight(1f).onGloballyPositioned { layout ->
                width = layout.size.width
            }) {
                var percentage by remember { mutableFloatStateOf(0.5f) }

                ConsoleWindow(
                    workingDirectory,
                    feedbackConsole,
                    modifier = Modifier.fillMaxHeight().weight(percentage)
                )

                Spacer(
                    modifier = Modifier
                        .width(16.dp)
                        .fillMaxHeight()
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                        .draggable(
                            rememberDraggableState { change ->
                                percentage = (percentage + (change * density) / width).coerceIn(0.05f..0.95f)
                            },
                            orientation = Orientation.Horizontal
                        )
                )

                ConsoleWindow(
                    workingDirectory,
                    internalConsole,
                    modifier = Modifier.fillMaxHeight().weight(1f-percentage)
                )
            }

            Spacer(Modifier.height(16.dp))

            CommandBar(commandSent = { command ->
                Logger.debug("command $command sent")

                try {
                    backend.inputToConsole(command, onOutput = {
                        Logger.feedback(it)
                    })
                } catch (e: Exception) {
                    Logger.error(e.message)
                }
            })
        }
    }
}
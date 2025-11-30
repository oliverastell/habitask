package com.oliverastell.habitask.ui.serverapp

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.oliverastell.habitask.data.LogStreamBinding
import com.oliverastell.habitask.data.Logger
import kotlinx.io.files.Path
import org.jetbrains.skiko.Cursor


@Composable
fun ServerApp(workingDirectory: Path) {
    val bindings = remember { mutableListOf<LogStreamBinding>() }

    DisposableEffect(Unit) {
        onDispose {
            bindings.fastForEach { it.close() }
        }
    }

    val feedbackConsole = remember {
        val list = mutableStateListOf<String>()

        bindings += Logger.feedback.bindRaw { list.add(it.toString()) }

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

    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
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

            CommandBar(commandSent = {
                Logger.debug("command $it sent")
                Logger.feedback("> $it")
            })
        }
    }

}
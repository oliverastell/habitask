package habitask.server.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import habitask.common.ui.Section


@Composable
fun CommandBar(commandSent: (String) -> Unit) {
    val history = remember { mutableStateListOf<String>() }
    var historyIndex by remember { mutableIntStateOf(0) }

    var commandInput by remember { mutableStateOf("") }

    val updateHistoryIndex = { newIndex: Int ->
        historyIndex = newIndex.coerceIn(0, history.size)
        commandInput = if (historyIndex == 0) "" else history[history.size-historyIndex]
    }

    val handleInput = {
        commandSent(commandInput)
        history.add(commandInput)
        commandInput = ""
    }

    Section(
        modifier = Modifier
            .height(50.dp)
    ) {
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(8.dp, 0.dp)) {
            BasicTextField(
                value = commandInput,
                singleLine = true,
                keyboardActions = KeyboardActions(onSend = { handleInput() }),
                textStyle = DefaultConsoleTextStyle(),
                onValueChange = {
                    commandInput = it
                },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .onKeyEvent {
                        when (it.key) {
                            Key.Enter if it.type == KeyEventType.KeyUp -> {
                                handleInput()
                                true
                            }
                            Key.DirectionUp if it.type == KeyEventType.KeyUp -> {
                                updateHistoryIndex(historyIndex+1)
                                true
                            }
                            Key.DirectionDown if it.type == KeyEventType.KeyUp -> {
                                updateHistoryIndex(historyIndex-1)
                                true
                            }
                            else -> {
                                false
                            }
                        }
                    }
            )
        }
    }
}
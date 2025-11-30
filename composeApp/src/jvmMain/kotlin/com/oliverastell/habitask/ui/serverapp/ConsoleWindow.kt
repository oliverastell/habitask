package com.oliverastell.habitask.ui.serverapp

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oliverastell.habitask.data.Logger
import kotlinx.io.files.Path


@Composable
private fun DefaultConsoleTextStyle() = LocalTextStyle.current.copy(
    color = MaterialTheme.colorScheme.onSurface,
    fontFamily = FontFamily.Monospace,
    fontSize = 14.sp
)

@Composable
fun CommandBar(commandSent: (String) -> Unit) {
    var commandInput by remember { mutableStateOf("") }

    val handleInput = {
        commandSent(commandInput)
        commandInput = ""
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .height(50.dp)
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            BasicTextField(
                value = commandInput,
                singleLine = true,
                keyboardActions = KeyboardActions(onSend = { handleInput() }),
                textStyle = DefaultConsoleTextStyle(),
                onValueChange = {
                    commandInput = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onKeyEvent {
                        if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                            handleInput()
                            true
                        } else {
                            false
                        }
                    }
            )
        }
    }
}

@Composable
fun ConsoleWindow(
    workingDirectory: Path,
    lines: List<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        SelectionContainer {
            LazyColumn(
                reverseLayout = true,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState()),
            ) {
                items(lines.size) {
                    BasicText(
                        lines[lines.lastIndex-it],
                        style = DefaultConsoleTextStyle(),
                        softWrap = false
                    )
                }
            }
        }
    }
}
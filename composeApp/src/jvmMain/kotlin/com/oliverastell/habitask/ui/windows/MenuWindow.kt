package com.oliverastell.habitask.ui.windows

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import io.github.numq.composedesktopwindowdecoration.decoration.WindowDecoration


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
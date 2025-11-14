package com.oliverastell.habitask.windows

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import io.github.numq.composedesktopwindowdecoration.decoration.WindowDecoration
import java.awt.Frame
import javax.swing.SwingUtilities


@Composable
fun ApplicationScope.ClientServerWindow(
    title: String,
    homeButtonPressed: () -> Unit,
    content: @Composable () -> Unit
) = WindowDecoration(
    titleContent = {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clickable { homeButtonPressed() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Home,
                null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.width(8.dp))

        Title(title)
    }
) {
    content()
}
package com.oliverastell.habitask.ui.windows

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import io.github.numq.composedesktopwindowdecoration.decoration.WindowDecoration


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
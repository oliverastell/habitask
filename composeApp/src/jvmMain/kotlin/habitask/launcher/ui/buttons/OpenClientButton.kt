package habitask.launcher.ui.buttons

import androidx.compose.runtime.Composable
import habitask.launcher.ui.elements.TextMenuButton

@Composable
fun OpenClientButton(
    onOpenClient: () -> Unit
) = TextMenuButton(
    "Open Client",
    onClick = onOpenClient
)
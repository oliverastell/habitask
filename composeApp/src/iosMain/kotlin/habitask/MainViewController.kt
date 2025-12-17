package habitask

import androidx.compose.ui.window.ComposeUIViewController
import habitask.client.ui.ClientApp
import habitask.client.ui.theme.HabitaskTheme

fun MainViewController() = ComposeUIViewController {
    HabitaskTheme {
        ClientApp()
    }
}
package com.oliverastell.habitask

import androidx.compose.ui.window.ComposeUIViewController
import com.oliverastell.habitask.ui.ClientApp
import com.oliverastell.habitask.ui.theme.HabitaskTheme

fun MainViewController() = ComposeUIViewController {
    HabitaskTheme {
        ClientApp()
    }
}
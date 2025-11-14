package com.oliverastell.habitask

import androidx.compose.ui.window.ComposeUIViewController
import com.oliverastell.habitask.clientapp.App
import com.oliverastell.habitask.theme.HabitaskTheme

fun MainViewController() = ComposeUIViewController {
    HabitaskTheme {
        App()
    }
}
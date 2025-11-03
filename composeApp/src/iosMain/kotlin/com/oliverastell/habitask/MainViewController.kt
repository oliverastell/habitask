package com.oliverastell.habitask

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    HabitaskTheme {
        App()
    }
}
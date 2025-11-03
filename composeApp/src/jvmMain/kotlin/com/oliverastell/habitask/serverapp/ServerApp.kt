package com.oliverastell.habitask.serverapp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

@Composable
fun ServerApp(workingDirectory: Path) {
    var text by remember { mutableStateOf("") }


    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Column {
                Text(workingDirectory.toString())
                Box(modifier = Modifier.fillMaxWidth().height(128.dp)) {
                    BasicTextField(value = text, onValueChange = {
                        text = it
                    })
                }
            }
        }

    }

}
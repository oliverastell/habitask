package com.oliverastell.habitask.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@Composable
fun DialogFrame(
    dialogTitle: String,
    onDismissRequest: () -> Unit,
    optionsContent: @Composable RowScope.() -> Unit = {
        TextButton(onClick = { onDismissRequest() }) {
            Text("Ok")
        }
    },
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp)
            ) {
                Text(dialogTitle)
            }

            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                content = {
                    Column(modifier = Modifier.padding(16.dp)) {
                        content()
                    }
                }
            )

            Row(
                content = optionsContent,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp)
            )
        }
    }
}
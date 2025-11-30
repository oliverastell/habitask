package com.oliverastell.habitask.ui.menu.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.oliverastell.habitask.ui.elements.DialogFrame

@Composable
fun AddServerDialog(
    onDismissRequest: () -> Unit,
    onServerAdded: (displayName: String, address: String, port: Int) -> Unit
) {
    var displayName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("8080") }

    DialogFrame(
        "Adding Server",
        onDismissRequest = onDismissRequest,
        optionsContent = {
            TextButton(onClick = {
                onServerAdded(displayName, address, port.toInt())
            }) {
                Text("Add")
            }

            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text("Cancel")
            }
        }
    ) {
        Spacer(Modifier.height(20.dp))
        Text("Display Name")
        TextField(
            displayName,
            onValueChange = { displayName = it }
        )
        Text("Address")
        TextField(
            address,
            onValueChange = { address = it }
        )
        Text("Port")
        TextField(
            port,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            onValueChange = { port = it }
        )
    }
}
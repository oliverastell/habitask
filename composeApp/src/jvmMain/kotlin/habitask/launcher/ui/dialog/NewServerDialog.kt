package habitask.launcher.ui.dialog

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import habitask.common.ui.DialogCancelOption
import habitask.common.ui.DialogFrame
import habitask.common.ui.DialogOption

@Composable
fun NewServerDialog(
    onDismissRequest: () -> Unit,
    onCheckValidity: (folderName: String) -> String?,
    onServerCreated: (folderName: String) -> Unit
) {
    var folderName by remember { mutableStateOf("example") }

    DialogFrame(
        dialogTitle = "New Server",
        onDismissRequest = onDismissRequest,
        optionsContent = {
            DialogOption("Create") {
                onServerCreated(folderName.trim())
            }
            DialogCancelOption()
        }
    ) {
        Text("Path Name")
        TextField(folderName, onValueChange = {
            folderName = it
        })

        val error = onCheckValidity(folderName.trim())
        if (error != null)
            Text(error, color = MaterialTheme.colorScheme.error)
    }
}
package habitask.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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


class DialogOptionScope(
    rowScope: RowScope,
    val onDismissRequest: () -> Unit
) : RowScope by rowScope

@Composable
fun DialogOptionScope.DialogOption(
    text: String,
    onClick: () -> Unit
) = TextButton(onClick = onClick) {
    Text(text)
}

@Composable
fun DialogOptionScope.DialogCancelOption(
    text: String = "Cancel"
) = DialogOption(text, onClick = onDismissRequest)

@Composable
fun DialogFrame(
    dialogTitle: String,
    onDismissRequest: () -> Unit,
    optionsContent: @Composable DialogOptionScope.() -> Unit = {
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
                content = { DialogOptionScope(this, onDismissRequest).optionsContent() },
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp)
            )
        }
    }
}
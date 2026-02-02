package habitask.client.ui.menu.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitask.common.ui.DialogCancelOption
import habitask.common.ui.DialogFrame
import habitask.common.ui.DialogOption

@Composable
fun DisconnectServerDialog(
    willLikelyReplicate: Boolean,
    onDismissRequest: () -> Unit,
    onServerDisconnected: () -> Unit
) {
    DialogFrame(
        "Confirmation",
        onDismissRequest = onDismissRequest,
        optionsContent = {
            DialogOption("Yes") { onServerDisconnected() }
            DialogCancelOption()
        }
    ) {
        Text("Are you sure you want to disconnect?")
        if (!willLikelyReplicate) {
            Spacer(Modifier.height(20.dp))
            Card(
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("WARNING:", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Because the server is offline (or you are unauthenticated), " +
                        "all this will do is hide the server from your server list.")
                Text("Your account will remain existing on the server and you will " +
                        "not be able to delete it.", fontWeight = FontWeight.Bold)
            }
        }
    }
}
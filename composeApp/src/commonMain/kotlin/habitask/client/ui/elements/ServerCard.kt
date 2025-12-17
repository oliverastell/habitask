package habitask.client.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import habitask.resources.Res
import habitask.resources.door
import org.jetbrains.compose.resources.painterResource


@Composable
fun ServerCard(
    serverName: String,
    serverUrl: String,
    yourName: String,
    yourGroupName: String,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier.align(Alignment.CenterVertically).weight(1f)
            ) {
                Text(serverName)

                var revealed by remember { mutableStateOf(false) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Server URL:")
                    Spacer(Modifier.width(4.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright
                        ),
                        onClick = {
                            revealed = !revealed
                        },
                        shape = MaterialTheme.shapes.small

                    ) {
                        Box {
                            Text(serverUrl, modifier = Modifier.padding(4.dp, 1.dp))
                            if (!revealed)
                                Surface(Modifier.matchParentSize(), color = MaterialTheme.colorScheme.surface) { }
                        }
                    }
                }

                Text("Your name: $yourName")
                Text("Your group: $yourGroupName")
            }
            Row(
                modifier = Modifier.align(Alignment.Bottom)
            ) {
                // Disconnect from server
                FloatingActionButton(
                    onClick = onDisconnect
                ) {
                    Icon(painterResource(Res.drawable.door), "Disconnect")
                }
            }
        }
    }
}
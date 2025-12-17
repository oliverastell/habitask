package habitask.server.ui.serverapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitask.resources.Res
import habitask.resources.help
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sidebar(
    title: String,
    ip: String,
    port: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    ProvideTextStyle(LocalTextStyle.current.copy(
        color = MaterialTheme.colorScheme.primary
    )) {
        Column(modifier) {
            Text(
                title, style = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )
            SelectionContainer { Text(ip, softWrap = false) }
            Row {
                Text("port: ")
                SelectionContainer { Text(port, softWrap = false) }

                Spacer(Modifier.weight(1f))

                TooltipBox(
                    modifier = Modifier.weight(1f),
                    tooltip = {
                        RichTooltip(
                            title = { Text("Unable to connect?") }
                        ) {
                            Text(
                                "If you're away from home, your port might not be port forwarded. " +
                                        "If you're at home, your network firewall might prevent you from using your public IP locally. " +
                                        "Try assigning this server's local IP as the backup IP."
                            )
                        }
                    },
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
                    state = rememberTooltipState(isPersistent = true),
                ) {
                    Icon(
                        painterResource(Res.drawable.help),
                        contentDescription = "Show connection help",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }



            Spacer(modifier = Modifier.weight(1f))

            content()
        }
    }
}
package habitask.client.ui.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import habitask.common.Logger
import habitask.common.util.formatTimeRelative
import habitask.client.ui.util.UpdatingEffect
import habitask.common.ui.Section
import habitask.resources.Res
import habitask.resources.check
import habitask.resources.menu
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun TaskCard(
    name: String,
    dueTime: Instant,
    description: String,
    onComplete: () -> Unit,
    onOutsource: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    var now by remember { mutableStateOf(Clock.System.now()) }
    UpdatingEffect(1.seconds) {
        now = Clock.System.now()
    }

    Section(modifier) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier.align(Alignment.CenterVertically).weight(1f)
            ) {
                Text(name)
                Text(formatTimeRelative(now, dueTime, futurePrefix = "due within ", pastPrefix = "due "))

                AnimatedVisibility(expanded) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(Modifier.height(8.dp))

                        Box {
                            Text(description)
                        }

                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            TextButton(onClick = onOutsource) {
                                Text("I can't complete this task!")
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.align(Alignment.Bottom)
            ) {
                // Complete Task
                SmallFloatingActionButton(
                    onClick = onComplete
                ) {
                    Icon(painterResource(Res.drawable.check), "Finished")
                }

                Spacer(Modifier.width(4.dp))

                // Menu Button
                SmallFloatingActionButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Icon(painterResource(Res.drawable.menu), "More")
                }
            }
        }
    }
}
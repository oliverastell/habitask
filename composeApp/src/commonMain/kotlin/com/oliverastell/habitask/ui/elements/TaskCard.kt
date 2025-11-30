package com.oliverastell.habitask.ui.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.oliverastell.habitask.data.Logger
import com.oliverastell.habitask.data.util.formatTimeRelative
import com.oliverastell.habitask.ui.util.UpdatingEffect
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
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    var now by remember { mutableStateOf(Clock.System.now()) }
    UpdatingEffect(1.seconds) {
        now = Clock.System.now()
    }

    Card(
        modifier,
        colors = if (dueTime >= now) CardDefaults.cardColors() else CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {

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
                            TextButton(onClick = { Logger.info("Task cannot be completed") }) {
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
                    onClick = {

                    }
                ) {
                    Icon(Icons.Default.Check, "Finished")
                }

                Spacer(Modifier.width(4.dp))

                // Menu Button
                SmallFloatingActionButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Icon(Icons.Default.Menu, "More")
                }
            }
        }
    }
}
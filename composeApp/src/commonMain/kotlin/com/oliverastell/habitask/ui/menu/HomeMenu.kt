package com.oliverastell.habitask.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oliverastell.habitask.data.classes.TaskInfo
import com.oliverastell.habitask.ui.elements.TaskCard
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun HomeMenu(
    taskInfos: List<TaskInfo>,
    modifier: Modifier = Modifier
) {
    HomeAndServerMenuCommon(
        modifier = modifier.fillMaxSize(),
        columnContent = {
            items(taskInfos.sortedBy { it.dueTime }) {
                TaskCard(
                    name = it.name,
                    dueTime = Instant.fromEpochSeconds(it.dueTime),
                    description = it.description
                )
            }
        },
        rowContent = {
            TextButton(onClick = {}) {
                Text("Group by Server")
            }
            TextButton(onClick = {}) {
                Text("Upcoming")
            }
        }
    )
}
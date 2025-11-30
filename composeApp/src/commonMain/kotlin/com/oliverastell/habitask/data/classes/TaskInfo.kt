package com.oliverastell.habitask.data.classes

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@OptIn(ExperimentalTime::class)
data class TaskInfo(
    val name: String,
    val dueTime: Long,
    val description: String
) {
    val dueTimeInstant
        get() = Instant.fromEpochSeconds(dueTime)
}
package habitask.common.data.info

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@OptIn(ExperimentalTime::class)
data class TaskAssignmentInfo(
    val id: Int,
    val taskId: Int,
    val entityId: Int,
    val dueTime: Instant
)
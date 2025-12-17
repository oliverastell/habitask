package habitask.client.data.networking

import habitask.common.data.info.TaskAssignmentInfo
import kotlinx.serialization.Serializable

@Serializable
data class TaskAssignmentsResponse(
    val tasks: List<TaskAssignmentInfo>
)
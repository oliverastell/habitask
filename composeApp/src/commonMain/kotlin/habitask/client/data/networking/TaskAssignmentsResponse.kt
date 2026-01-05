package habitask.client.data.networking

import habitask.common.data.info.AssignmentInfo
import kotlinx.serialization.Serializable

@Serializable
data class TaskAssignmentsResponse(
    val tasks: List<AssignmentInfo>
)
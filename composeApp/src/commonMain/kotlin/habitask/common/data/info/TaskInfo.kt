package habitask.common.data.info

import habitask.client.data.Connection
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.serializers.DateTimeUnitSerializer
import kotlinx.serialization.Serializable

@Serializable
data class TaskInfo(
    val id: Int,
    val name: String,
    val description: String,

    @Serializable(with = DateTimeUnitSerializer::class)
    val cycleEvery: DateTimeUnit,

    @Serializable(with = DateTimeUnitSerializer::class)
    val dueAfter: DateTimeUnit,
)
package habitask.common.data.info

import kotlinx.serialization.Serializable

@Serializable
data class AccessInfo(
    val token: String,
    val accountId: Int
)
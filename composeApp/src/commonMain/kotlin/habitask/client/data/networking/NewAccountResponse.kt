package habitask.client.data.networking

import habitask.common.data.info.AccessInfo
import habitask.common.data.info.EntityInfo
import kotlinx.serialization.Serializable

@Serializable
data class NewAccountResponse(
    val entityInfo: EntityInfo,
    val accessInfo: AccessInfo
)

package habitask.common.data.info

import habitask.client.data.Connection
import kotlinx.serialization.Serializable

@Serializable
data class ServerInfo(
    val name: String,
    val port: Int
)
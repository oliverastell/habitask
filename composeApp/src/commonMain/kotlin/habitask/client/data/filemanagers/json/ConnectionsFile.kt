package habitask.client.data.filemanagers.json

import habitask.client.data.Connection
import kotlinx.serialization.Serializable

@Serializable
data class ConnectionsFile(
    val connections: List<Connection>
)

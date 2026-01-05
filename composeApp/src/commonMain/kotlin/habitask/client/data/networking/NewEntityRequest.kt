package habitask.client.data.networking

import kotlinx.serialization.Serializable

@Serializable
data class NewEntityRequest(
    val name: String
)
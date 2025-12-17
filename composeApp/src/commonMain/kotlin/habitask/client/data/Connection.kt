package habitask.client.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class Connection(
    val address: String,
    val backupAddress: String?,
    val port: Int,
    val token: String? = null,

    @Transient
    var currentlyUsingBackup: Boolean = false
) {
    val url
        get() = if (!currentlyUsingBackup)
            "http://$address:$port"
        else
            "http://$backupAddress:$port"

    fun withToken(token: String) = Connection(
        address,
        backupAddress,
        port,
        token
    )
}
package habitask.server.data

import habitask.common.data.info.ServerInfo
import kotlinx.datetime.DateTimeUnit
import kotlin.time.Instant

data class ServerConfigs(
    val name: String,
    val port: Int,
    val reassignEvery: DateTimeUnit,
    val nextReassignment: Instant
) {
    fun toServerInfo() = ServerInfo(
        name = name,
        port = port
    )
}
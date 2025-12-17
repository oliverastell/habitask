package habitask.server.data

import habitask.common.data.info.ServerInfo
import kotlinx.datetime.DateTimeUnit

data class ServerConfigs(
    val name: String,
    val port: Int,
    val reassignEvery: DateTimeUnit
) {
    fun toServerInfo() = ServerInfo(
        name = name,
        port = port
    )
}
package com.oliverastell.habitask.data.networking

import com.oliverastell.habitask.data.classes.ServerInfo
import kotlinx.serialization.Serializable

@Serializable
data class ServerInfoResponse(
    val serverInfo: ServerInfo
)
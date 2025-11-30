package com.oliverastell.habitask.data.filemanagers.json

import com.oliverastell.habitask.data.Connection
import kotlinx.serialization.Serializable

@Serializable
data class ConnectionsFile(
    val connections: List<Connection>
)

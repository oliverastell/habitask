package com.oliverastell.habitask.data

import kotlinx.serialization.Serializable



@Serializable
data class Connection(
    val address: String,
    val port: Int,
    val token: String? = null
) {
    val url
        get() = "http://$address:$port"

    fun withToken(token: String) = Connection(
        address,
        port,
        token
    )
}
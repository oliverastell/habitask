package com.oliverastell.habitask.data.networking

import com.oliverastell.habitask.data.classes.AccessInfo
import com.oliverastell.habitask.data.classes.UserInfo
import kotlinx.serialization.Serializable

@Serializable
data class NewAccountResponse(
    val userInfo: UserInfo,
    val accessInfo: AccessInfo
)

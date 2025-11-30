package com.oliverastell.habitask.data.networking

import com.oliverastell.habitask.data.classes.UserInfo
import kotlinx.serialization.Serializable

@Serializable
data class AccountInfoResponse(
    val userInfo: UserInfo
)
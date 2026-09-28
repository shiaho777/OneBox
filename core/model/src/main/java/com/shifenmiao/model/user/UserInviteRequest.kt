package com.shifenmiao.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserInviteRequest(
    val invitationCode: String
)

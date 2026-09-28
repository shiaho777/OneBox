package com.shifenmiao.model.transfer

import kotlinx.serialization.Serializable

/**
 * 手机端用于展示“多浏览器会话”的摘要信息。
 */
@Serializable
data class ChatSession(
    val channelId: String,
    val deviceName: String? = null,
    val lastTimestamp: Long,
    val lastSender: String? = null,
    val lastContent: String? = null,
    val messageCount: Int = 0
)

package com.shifenmiao.model.transfer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 聊天消息模型(file_transfer WS 协议,对端是 web/ 浏览器 JS,
 * 字段名与 type 枚举值 text/file/system 一个字符都不能变)。
 */
@Serializable
data class ChatMessage(
    @SerialName("id")
    val id: String = "",

    @SerialName("type")
    val type: MessageType = MessageType.TEXT,

    @SerialName("content")
    val content: String = "",

    @SerialName("sender")
    val sender: String = "", // "mobile" or "browser"

    @SerialName("timestamp")
    val timestamp: Long = 0L,

    @SerialName("fileName")
    val fileName: String? = null,

    @SerialName("fileSize")
    val fileSize: Long? = null,

    @SerialName("filePath")
    val filePath: String? = null
)

@Serializable
enum class MessageType {
    @SerialName("text")
    TEXT,

    @SerialName("file")
    FILE,

    @SerialName("system")
    SYSTEM
}


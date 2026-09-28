package com.shifenmiao.model.transfer

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 聊天消息模型(file_transfer WS 协议,对端是 web/ 浏览器 JS,
 * 字段名与 type 枚举值 text/file/system 一个字符都不能变)。
 */
@Serializable
data class ChatMessage(
    @SerializedName("id")
    @SerialName("id")
    val id: String = "",

    @SerializedName("type")
    @SerialName("type")
    val type: MessageType = MessageType.TEXT,

    @SerializedName("content")
    @SerialName("content")
    val content: String = "",

    @SerializedName("sender")
    @SerialName("sender")
    val sender: String = "", // "mobile" or "browser"

    @SerializedName("timestamp")
    @SerialName("timestamp")
    val timestamp: Long = 0L,

    @SerializedName("fileName")
    @SerialName("fileName")
    val fileName: String? = null,

    @SerializedName("fileSize")
    @SerialName("fileSize")
    val fileSize: Long? = null,

    @SerializedName("filePath")
    @SerialName("filePath")
    val filePath: String? = null
)

@Serializable
enum class MessageType {
    @SerializedName("text")
    @SerialName("text")
    TEXT,

    @SerializedName("file")
    @SerialName("file")
    FILE,

    @SerializedName("system")
    @SerialName("system")
    SYSTEM
}


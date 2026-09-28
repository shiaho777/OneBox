package com.shifenmiao.model.moderation

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SensitiveWordCheckRequest(
    @SerialName("scene")
    val scene: String,
    @SerialName("fields")
    val fields: List<SensitiveWordCheckField>
)

@Keep
@Serializable
data class SensitiveWordCheckField(
    @SerialName("key")
    val key: String,
    @SerialName("text")
    val text: String
)

@Keep
@Serializable
data class SensitiveWordCheckResponse(
    @SerialName("hit")
    val hit: Boolean = false,
    @SerialName("hits")
    val hits: List<SensitiveWordHit> = emptyList(),
    @SerialName("message")
    val message: String? = null
)

@Keep
@Serializable
data class SensitiveWordHit(
    @SerialName("key")
    val key: String,
    @SerialName("words")
    val words: List<String> = emptyList(),
    @SerialName("reason")
    val reason: String? = null
)

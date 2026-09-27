package com.shifenmiao.model.tts.mimo

import kotlinx.serialization.Serializable

@Serializable
data class MimoTTSRequest(
    val model: String,
    val messages: List<MimoMessage>,
    val audio: MimoAudioConfig,
    val stream: Boolean = false,
)

@Serializable
data class MimoMessage(
    val role: String,
    val content: String,
)

@Serializable
data class MimoAudioConfig(
    val format: String,
    val voice: String,
)

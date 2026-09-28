package com.shifenmiao.model.tts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TTSSpeechRequest(
    val model: String,
    val input: String,
    val voice: String,
    @SerialName("response_format")
    val responseFormat: String = "mp3",
    val speed: Double = 1.0,
)

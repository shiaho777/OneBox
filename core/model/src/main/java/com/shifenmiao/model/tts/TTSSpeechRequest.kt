package com.shifenmiao.model.tts

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TTSSpeechRequest(
    val model: String,
    val input: String,
    val voice: String,
    @SerializedName("response_format")
    @SerialName("response_format")
    val responseFormat: String = "mp3",
    val speed: Double = 1.0,
)

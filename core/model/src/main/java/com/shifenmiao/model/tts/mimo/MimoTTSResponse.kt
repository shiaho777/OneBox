package com.shifenmiao.model.tts.mimo

import kotlinx.serialization.Serializable

@Serializable
data class MimoTTSResponse(
    val choices: List<MimoChoice> = emptyList(),
)

@Serializable
data class MimoChoice(
    val message: MimoMessageWithAudio? = null,
)

@Serializable
data class MimoMessageWithAudio(
    val role: String = "",
    val content: String = "",
    val audio: MimoAudioData? = null,
)

@Serializable
data class MimoAudioData(
    val data: String = "",
    val format: String? = null,
)

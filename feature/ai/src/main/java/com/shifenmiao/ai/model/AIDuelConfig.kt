package com.shifenmiao.ai.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.shifenmiao.model.ModelProvider.AppJson
import com.shifenmiao.model.ai.AiEngine
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject

@Serializable
@Keep
@Parcelize
data class AIDuelConfig(
    @SerializedName("personaA")
    val personaA: String = "",
    @SerializedName("personaB")
    val personaB: String = "",
    @SerializedName("avatarA")
    val avatarA: String = "",
    @SerializedName("avatarB")
    val avatarB: String = "",
    @SerializedName("maxRounds")
    val maxRounds: Int = 5,
    @SerializedName("engineA")
    val engineA: AiEngine? = null,
    @SerializedName("engineB")
    val engineB: AiEngine? = null,
    @SerializedName("promptIdA")
    val promptIdA: Int = 0,
    @SerializedName("promptIdB")
    val promptIdB: Int = 0,
    @SerializedName("promptNameA")
    val promptNameA: String = "",
    @SerializedName("promptNameB")
    val promptNameB: String = "",
    @SerializedName("roleNameA")
    val roleNameA: String = "",
    @SerializedName("roleNameB")
    val roleNameB: String = ""
) : Parcelable

internal object AIDuelConfigCodec {
    // AiModel.provider 经 AiProviderKSerializer 序列化为与 Gson 一致的字符串格式,
    // 持久化在 conversation.prompt 里的旧数据可直接读
    fun encode(config: AIDuelConfig): String = AppJson.encodeToString(config)

    fun decodeOrNull(prompt: String): AIDuelConfig? = kotlin.runCatching {
        val obj = AppJson.parseToJsonElement(prompt).jsonObject

        fun str(key: String): String =
            (obj[key] as? JsonPrimitive)?.contentOrNull.orEmpty()

        fun int(key: String, default: Int): Int =
            (obj[key] as? JsonPrimitive)?.intOrNull ?: default

        val engineA = kotlin.runCatching {
            obj["engineA"]?.let { AppJson.decodeFromJsonElement(AiEngine.serializer(), it) }
        }.getOrNull()
        val engineB = kotlin.runCatching {
            obj["engineB"]?.let { AppJson.decodeFromJsonElement(AiEngine.serializer(), it) }
        }.getOrNull()

        AIDuelConfig(
            personaA = str("personaA"),
            personaB = str("personaB"),
            // 旧 JSON 里的 mode/topic 字段直接忽略，历史会话保持兼容
            avatarA = str("avatarA"),
            avatarB = str("avatarB"),
            maxRounds = int("maxRounds", 5),
            engineA = engineA,
            engineB = engineB,
            promptIdA = int("promptIdA", 0),
            promptIdB = int("promptIdB", 0),
            promptNameA = str("promptNameA"),
            promptNameB = str("promptNameB"),
            roleNameA = str("roleNameA"),
            roleNameB = str("roleNameB"),
        )
    }.getOrNull()
}

enum class DuelSpeaker {
    A,
    B;

    fun other(): DuelSpeaker = if (this == A) B else A
}

data class AIDuelState(
    val running: Boolean = false,
    val round: Int = 0,
    val speaker: DuelSpeaker = DuelSpeaker.A,
    val errorMessage: String = "",
)

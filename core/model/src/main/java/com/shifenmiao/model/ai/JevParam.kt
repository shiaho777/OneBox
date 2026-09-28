package com.shifenmiao.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * TypeSafe System One (Jev) 判断接口请求。
 *
 * state 与 questions 由调用方按业务自由拼装, 因此直接以 JsonObject 承载。
 * model 必须以 "jev" 开头(go-proxy 侧校验)。
 */
@Serializable
data class JevRequest(
    @SerialName("state")
    val state: JsonObject,
    @SerialName("model")
    val model: String,
    @SerialName("questions")
    val questions: JsonObject,
)

@Serializable
data class JevResponse(
    @SerialName("model")
    val model: String = "",
    @SerialName("answers")
    val answers: Map<String, JevChoiceAnswer> = emptyMap(),
    @SerialName("usage")
    val usage: JsonObject? = null,
)

/**
 * choice 判断题答案。
 *
 * 注意: 服务端实测 choice 是按 probabilities 采样的结果而非 argmax,
 * 需要稳定选择时应自行对 probabilities 取概率最高的键。
 */
@Serializable
data class JevChoiceAnswer(
    @SerialName("type")
    val type: String = "",
    @SerialName("choice")
    val choice: String = "",
    @SerialName("confidence")
    val confidence: Double = 0.0,
    @SerialName("probabilities")
    val probabilities: Map<String, Double> = emptyMap(),
)

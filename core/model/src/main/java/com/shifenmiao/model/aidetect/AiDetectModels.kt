package com.shifenmiao.model.aidetect

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 朱雀 AIGC 文本检测请求(go-proxy 代理 /ai-detect/text) */
@Keep
@Serializable
data class AiDetectTextRequest(
    @SerializedName("text")
    @SerialName("text")
    val text: String,
    @SerializedName("is_merge")
    @SerialName("is_merge")
    val isMerge: Boolean = true,
)

@Keep
@Serializable
data class AiDetectTextResponse(
    @SerializedName("status")
    @SerialName("status")
    val status: String? = null,
    @SerializedName("softmax_confidence")
    @SerialName("softmax_confidence")
    val softmaxConfidence: Float = 0f,
    /** 疑似 AI 占比(0-1,越高越可能是 AI) */
    @SerializedName("ratio_confidence")
    @SerialName("ratio_confidence")
    val ratioConfidence: Float = 0f,
    /** key "0"=人类、"1"=AI、"2"=疑似 AI */
    @SerializedName("labels_ratio")
    @SerialName("labels_ratio")
    val labelsRatio: Map<String, Float> = emptyMap(),
    @SerializedName("segment_labels")
    @SerialName("segment_labels")
    val segmentLabels: List<AiDetectSegmentLabel> = emptyList(),
    @SerializedName("msg")
    @SerialName("msg")
    val msg: String? = null,
)

@Keep
@Serializable
data class AiDetectSegmentLabel(
    @SerializedName("text")
    @SerialName("text")
    val text: String = "",
    /** 0=人类、1=AI、2=疑似 AI */
    @SerializedName("label")
    @SerialName("label")
    val label: Int = 0,
    @SerializedName("conf")
    @SerialName("conf")
    val conf: Float = 0f,
    @SerializedName("order")
    @SerialName("order")
    val order: Int = 0,
    @SerializedName("position")
    @SerialName("position")
    val position: List<Int> = emptyList(),
)

/** 朱雀 AIGC 图片检测请求(go-proxy 代理 /ai-detect/image) */
@Keep
@Serializable
data class AiDetectImageRequest(
    @SerializedName("imageBase64")
    @SerialName("imageBase64")
    val imageBase64: String? = null,
    @SerializedName("imageUrl")
    @SerialName("imageUrl")
    val imageUrl: String? = null,
)

@Keep
@Serializable
data class AiDetectImageResponse(
    @SerializedName("status")
    @SerialName("status")
    val status: String? = null,
    @SerializedName("data")
    @SerialName("data")
    val data: AiDetectImageData? = null,
    @SerializedName("message")
    @SerialName("message")
    val message: String? = null,
)

@Keep
@Serializable
data class AiDetectImageData(
    /** AI 生成置信度(0-1,越高越可能是 AI 生成) */
    @SerializedName("confidence")
    @SerialName("confidence")
    val confidence: Float = 0f,
)

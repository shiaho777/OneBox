package com.shifenmiao.imagegeneration.model

import kotlinx.serialization.Serializable

/** 独立于聊天模型的图片 Provider 配置。 */
@Serializable
data class ImageProviderConfig(
    // 默认值兜底 Gson 时代的 EncryptedSharedPreferences 旧数据(缺字段时 Gson 给 null)
    val id: String = "",
    val providerId: String = "",
    val displayName: String = "",
    val baseUrl: String = "",
    val apiToken: String = "",
    val proxyUrl: String = "",
    val proxyPath: String = "",
    val model: String = "",
    val enabled: Boolean = true,
) {
    val hasDirectConfig: Boolean
        get() = apiToken.isNotBlank()
}

data class ImageProviderDescriptor(
    val providerId: String,
    val displayName: String,
    val defaultBaseUrl: String,
    val defaultProxyUrl: String,
    val defaultProxyPath: String,
    val defaultModel: String,
    val availableModels: List<String>,
    val supportsGeneration: Boolean,
    val supportsEditing: Boolean,
    val maxInputImages: Int,
)

data class ImageGenerationRequest(
    val prompt: String,
    val inputImages: List<String> = emptyList(),
    val model: String? = null,
    val negativePrompt: String? = null,
    val outputSize: String? = null,
    val outputCount: Int = 1,
    val seed: Int? = null,
    val watermark: Boolean = false,
    val promptExtend: Boolean = true,
    val promptExtendMode: String = "direct",
    val enableThinking: Boolean = true,
)

data class GeneratedImage(
    val url: String,
)

data class ImageGenerationResult(
    val images: List<GeneratedImage>,
    val providerId: String,
    val model: String,
    val requestId: String? = null,
    val width: Int? = null,
    val height: Int? = null,
)

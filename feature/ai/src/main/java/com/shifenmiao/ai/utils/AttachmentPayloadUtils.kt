package com.shifenmiao.ai.utils

import com.shifenmiao.model.ModelProvider.AppJson
import com.shifenmiao.model.ai.AttachmentPayloadDto
import com.shifenmiao.model.ai.AttachedMedia
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

object AttachmentPayloadUtils {

    /**
     * 序列化附件列表为 JSON。
     *
     * @param stripLocalContent true 时排除 localContent（用于 DB 持久化），
     *                          false 时保留 localContent（用于 API 请求构建，避免缓存文件读取失败导致图片丢失）。
     */
    fun serialize(
        attachments: List<AttachedMedia>,
        stripLocalContent: Boolean = true
    ): String {
        return AppJson.encodeToString(attachments.map { AttachmentPayloadDto.from(it, stripLocalContent) })
    }

    fun deserialize(
        json: String
    ): List<AttachmentPayloadDto> {
        if (json.isBlank()) return emptyList()
        return AppJson.decodeFromString<List<AttachmentPayloadDto>>(json)
    }

    /**
     * 从 attachmentsJson 中剥离 localContent，仅保留 localPath 引用。
     *
     * 用途：DB 持久化前调用，避免 base64 字符串（约 100-700KB/张）导致 DB 膨胀。
     * live state 中保留 localContent 用于 API 请求构建，DB 仅存储 localPath。
     *
     * @return 剥离 localContent 后的 JSON；如果 json 为空或解析失败，返回原始 json。
     */
    fun stripLocalContent(json: String): String {
        if (json.isBlank()) return json
        return try {
            val dtos = deserialize(json)
            val strippedDtos = dtos.map { it.copy(localContent = null) }
            AppJson.encodeToString(strippedDtos)
        } catch (_: Exception) {
            // 反序列化失败时保留原始 JSON（不含 localContent 的旧格式也不会受影响）
            json
        }
    }

    fun resolveImageContentUrl(
        mimeType: String,
        localContent: String
    ): String {
        val normalized = localContent.trim()
        if (normalized.startsWith("data:", ignoreCase = true)) {
            return normalized
        }
        val safeMimeType = mimeType.takeIf { it.isNotBlank() } ?: "image/jpeg"
        return "data:$safeMimeType;base64,$normalized"
    }
}

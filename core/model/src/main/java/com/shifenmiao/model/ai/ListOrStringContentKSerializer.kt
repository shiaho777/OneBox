package com.shifenmiao.model.ai

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * kotlinx 版 ListOrStringContentTypeAdapter(Gson 清零,阶段⑤b):
 * OpenAI 多模态 content 字段的两种形态 —— StringContent 写裸字符串,
 * ListContent 写内容项数组,与原 Gson adapter 产物逐字节等价。
 */
object ListOrStringContentKSerializer : KSerializer<ListOrStringContent> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("ListOrStringContent")

    override fun serialize(encoder: Encoder, value: ListOrStringContent) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("ListOrStringContent 仅支持 JSON 编码")
        jsonEncoder.encodeJsonElement(
            when (value) {
                is ListOrStringContent.StringContent -> JsonPrimitive(value.content)
                is ListOrStringContent.ListContent -> JsonArray(
                    value.items.map { item -> item.toJson() }
                )
            }
        )
    }

    override fun deserialize(decoder: Decoder): ListOrStringContent {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("ListOrStringContent 仅支持 JSON 解码")
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> ListOrStringContent.StringContent(element.content)
            is JsonArray -> ListOrStringContent.ListContent(
                element.mapNotNull { it.toContentItemOrNull() }
            )
            else -> throw SerializationException("Unexpected JSON format for ListOrStringContent")
        }
    }

    private fun ContentItem.toJson(): JsonElement = when (this) {
        // 键顺序与原 Gson adapter 一致: type 在前
        is ContentItem.TextContent -> buildJsonObject {
            put("type", type)
            put("text", text)
        }
        is ContentItem.ImageContent -> buildJsonObject {
            put("type", type)
            put("image_url", buildJsonObject { put("url", imageUrl.url) })
        }
    }

    private fun JsonElement.toContentItemOrNull(): ContentItem? {
        val obj = this as? JsonObject ?: return null
        return when ((obj["type"] as? JsonPrimitive)?.contentOrNull) {
            "text" -> ContentItem.TextContent(
                text = obj["text"]?.jsonPrimitive?.contentOrNull.orEmpty()
            )
            "image_url" -> ContentItem.ImageContent(
                ImageUrl(
                    url = (obj["image_url"] as? JsonObject)
                        ?.get("url")?.jsonPrimitive?.contentOrNull.orEmpty()
                )
            )
            else -> null
        }
    }
}

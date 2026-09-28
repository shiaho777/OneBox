package com.shifenmiao.model.ai

import com.shifenmiao.model.toJsonElement
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Anthropic 请求模型里 `@RawValue Any` 字段的编码器(替代 Gson 的运行时反射序列化)。
 *
 * 按运行时类型写出:String/Number/Boolean → 裸字面量;List<ContentBlock> → 数组
 * (元素必须走 ContentBlock 自己的 serializer,不能走 JsonMaps.toJsonElement —
 *  它不认 @Serializable data class);Map → 对象。
 * 只需实现编码:Anthropic 请求是单向下行,响应侧由 SSE 帧模型承担。
 */
object AnthropicAnySerializer : KSerializer<Any> {

    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("AnthropicAnySerializer 仅支持 JSON 编码")
        jsonEncoder.encodeJsonElement(value.toAnthropicJson(jsonEncoder))
    }

    override fun deserialize(decoder: Decoder): Any =
        throw UnsupportedOperationException("Anthropic 请求模型仅用于编码")

    private fun Any?.toAnthropicJson(encoder: JsonEncoder): JsonElement = when (this) {
        null -> JsonNull
        is JsonElement -> this
        is String -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is Boolean -> JsonPrimitive(this)
        is ContentBlock -> encoder.json.encodeToJsonElement(ContentBlock.serializer(), this)
        is List<*> -> JsonArray(map { it.toAnthropicJson(encoder) })
        is Map<*, *> -> JsonObject(
            entries.associate { (key, v) -> key.toString() to v.toAnthropicJson(encoder) }
        )
        else -> toJsonElement()
    }
}

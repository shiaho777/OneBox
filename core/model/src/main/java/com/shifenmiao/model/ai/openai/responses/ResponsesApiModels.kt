package com.shifenmiao.model.ai.openai.responses

import com.shifenmiao.model.ai.ReasoningOptions
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement

/**
 * OpenAI Responses API 请求模型。
 * 这里仅承载请求侧稳定结构；流式事件解析放在 Adapter 中完成，避免上层依赖 provider 事件细节。
 *
 * sealed 层级不能用 kotlinx 默认多态序列化(判别字段 "type" 与协议自身的
 * type 字段冲突),经 [ResponsesApiInputItemSerializer] / [ResponsesApiContentItemSerializer]
 * 按运行时类型直接写具体形状,与 Gson 反射产物字段完全一致。
 */
@Serializable
data class ResponsesApiRequest(
    @SerialName("model")
    val model: String,
    @SerialName("input")
    val input: List<ResponsesApiInputItem> = emptyList(),
    @SerialName("stream")
    val stream: Boolean = true,
    /**
     * Responses API 同时支持 function tool 与内建 tool（如 web_search_preview），
     * 这里统一转成 JsonObject，避免 Any/Parcelize 序列化问题。
     */
    @SerialName("tools")
    val tools: List<JsonObject>? = null,
    @SerialName("reasoning")
    val reasoning: ReasoningOptions? = null,
    @SerialName("previous_response_id")
    val previousResponseId: String? = null,
)

@Serializable(ResponsesApiInputItemSerializer::class)
sealed class ResponsesApiInputItem {
    @Serializable
    data class Message(
        @SerialName("type") val type: String = "message",
        @SerialName("role") val role: String,
        @SerialName("content") val content: List<ResponsesApiContentItem>
    ) : ResponsesApiInputItem()

    @Serializable
    data class FunctionCall(
        @SerialName("type") val type: String = "function_call",
        @SerialName("call_id") val callId: String,
        @SerialName("name") val name: String,
        @SerialName("arguments") val arguments: String,
    ) : ResponsesApiInputItem()

    @Serializable
    data class FunctionCallOutput(
        @SerialName("type") val type: String = "function_call_output",
        @SerialName("call_id") val callId: String,
        @SerialName("output") val output: String,
    ) : ResponsesApiInputItem()
}

@Serializable(ResponsesApiContentItemSerializer::class)
sealed class ResponsesApiContentItem {
    @Serializable
    data class InputText(
        @SerialName("type") val type: String = "input_text",
        @SerialName("text") val text: String,
    ) : ResponsesApiContentItem()

    @Serializable
    data class InputImage(
        @SerialName("type") val type: String = "input_image",
        @SerialName("image_url") val imageUrl: String,
    ) : ResponsesApiContentItem()
}

/**
 * 内建 web search tool 的轻量占位对象。
 */
@Serializable
data class ResponsesWebSearchTool(
    @SerialName("type") val type: String = "web_search_preview"
)

/** 请求模型仅编码:按运行时类型委托具体子类序列化器,不带多态判别字段 */
object ResponsesApiInputItemSerializer : KSerializer<ResponsesApiInputItem> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ResponsesApiInputItem) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("ResponsesApiInputItem 仅支持 JSON 编码")
        val element = when (value) {
            is ResponsesApiInputItem.Message ->
                jsonEncoder.json.encodeToJsonElement(ResponsesApiInputItem.Message.serializer(), value)
            is ResponsesApiInputItem.FunctionCall ->
                jsonEncoder.json.encodeToJsonElement(ResponsesApiInputItem.FunctionCall.serializer(), value)
            is ResponsesApiInputItem.FunctionCallOutput ->
                jsonEncoder.json.encodeToJsonElement(ResponsesApiInputItem.FunctionCallOutput.serializer(), value)
        }
        jsonEncoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): ResponsesApiInputItem =
        throw UnsupportedOperationException("ResponsesApiInputItem 仅用于请求编码")
}

/** 请求模型仅编码:按运行时类型委托具体子类序列化器,不带多态判别字段 */
object ResponsesApiContentItemSerializer : KSerializer<ResponsesApiContentItem> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ResponsesApiContentItem) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("ResponsesApiContentItem 仅支持 JSON 编码")
        val element = when (value) {
            is ResponsesApiContentItem.InputText ->
                jsonEncoder.json.encodeToJsonElement(ResponsesApiContentItem.InputText.serializer(), value)
            is ResponsesApiContentItem.InputImage ->
                jsonEncoder.json.encodeToJsonElement(ResponsesApiContentItem.InputImage.serializer(), value)
        }
        jsonEncoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): ResponsesApiContentItem =
        throw UnsupportedOperationException("ResponsesApiContentItem 仅用于请求编码")
}

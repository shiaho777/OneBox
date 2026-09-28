import com.shifenmiao.model.ModelProvider
import com.shifenmiao.model.ai.*
import com.shifenmiao.model.jsonStringOf
import com.t8rin.imagetoolbox.core.domain.model.CipherType
import com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams
import com.t8rin.imagetoolbox.feature.draw.domain.DrawOnBackgroundParams
import com.t8rin.imagetoolbox.feature.image_stitch.domain.SavableCombiningParams
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchAlignment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

val appJson = ModelProvider.AppJson

fun check(name: String, cond: Boolean, detail: String = "") {
    println((if (cond) "PASS" else "FAIL") + " | " + name + (if (detail.isNotEmpty()) " | " + detail else ""))
    if (!cond) kotlin.system.exitProcess(1)
}

fun main() {
    // ── 问题1: Anthropic 请求体三形态 ──
    val textReq = AnthropicMessagesRequest(
        model = "claude-test",
        messages = listOf(AnthropicMessage(role = "user", content = "你好")),
    )
    val textJson = appJson.encodeToString(AnthropicMessagesRequest.serializer(), textReq)
    println("TEXT  | $textJson")
    check("anthropic-text", textJson.contains("\"content\":\"你好\""))

    val toolReq = AnthropicMessagesRequest(
        model = "claude-test",
        messages = listOf(AnthropicMessage(role = "user", content = "几点了")),
        tools = listOf(
            AnthropicTool(
                name = "get_time",
                description = "获取时间",
                inputSchema = mapOf(
                    "type" to "object",
                    "properties" to mapOf("tz" to mapOf("type" to "string")),
                )
            )
        ),
    )
    val toolJson = appJson.encodeToString(AnthropicMessagesRequest.serializer(), toolReq)
    println("TOOL  | $toolJson")
    check("anthropic-tool", toolJson.contains("\"input_schema\":{\"type\":\"object\""))

    // 工具调用块(assistant 回放, input 为 Map)
    val toolUseReq = AnthropicMessagesRequest(
        model = "claude-test",
        messages = listOf(
            AnthropicMessage(
                role = "assistant",
                content = listOf(
                    ContentBlock(type = "tool_use", id = "call_1", name = "get_time", input = mapOf("tz" to "Asia/Shanghai"))
                )
            )
        )
    )
    val toolUseJson = appJson.encodeToString(AnthropicMessagesRequest.serializer(), toolUseReq)
    println("TOOLUSE | $toolUseJson")
    check("anthropic-tooluse", toolUseJson.contains("\"input\":{\"tz\":\"Asia/Shanghai\"}"))

    val mmReq = AnthropicMessagesRequest(
        model = "claude-test",
        messages = listOf(
            AnthropicMessage(
                role = "user",
                content = listOf(
                    ContentBlock(type = "image", source = ImageSource(data = "QUJD")),
                    ContentBlock(type = "text", text = "这是什么"),
                )
            )
        )
    )
    val mmJson = appJson.encodeToString(AnthropicMessagesRequest.serializer(), mmReq)
    println("MM    | $mmJson")
    check("anthropic-multimodal", mmJson.contains("\"source\":{\"type\":\"base64\",\"media_type\":\"image/jpeg\",\"data\":\"QUJD\"}"))

    // ── 问题3+8: AiProvider 旧格式解码 ──
    val legacy = """{"type":"com.shifenmiao.model.ai.AiProvider.DeepSeek"}"""
    val decodedLegacy = appJson.decodeFromJsonElement(AiProviderKSerializer, kotlinx.serialization.json.Json.parseToJsonElement(legacy))
    check("provider-legacy-polymorphic", decodedLegacy == AiProvider.DeepSeek, decodedLegacy.toString())

    val gsonShape = """{"value":"kimi"}"""
    val decodedGson = appJson.decodeFromJsonElement(AiProviderKSerializer, kotlinx.serialization.json.Json.parseToJsonElement(gsonShape))
    check("provider-gson-object", decodedGson == AiProvider.Kimi, decodedGson.toString())

    val stringShape = "\"qwen\""
    val decodedStr = appJson.decodeFromJsonElement(AiProviderKSerializer, kotlinx.serialization.json.Json.parseToJsonElement(stringShape))
    check("provider-string", decodedStr == AiProvider.QWen, decodedStr.toString())

    // 问题8: value 为对象时不抛异常
    val weird = """{"value":{"nested":1}}"""
    val decodedWeird = appJson.decodeFromJsonElement(AiProviderKSerializer, kotlinx.serialization.json.Json.parseToJsonElement(weird))
    check("provider-weird-no-throw", decodedWeird == AiProvider.Default, decodedWeird.toString())

    // ── 问题4: ObjectSaver 类型经 serializer(Type) 往返(复刻 KotlinxJsonParser 调用) ──
    val parserJson = Json(ModelProvider.AppJson) { classDiscriminator = "Quality" }
    fun <T : Any> roundTrip(value: T, type: java.lang.reflect.Type, name: String) {
        @Suppress("UNCHECKED_CAST")
        val ser = serializer(type) as kotlinx.serialization.KSerializer<T>
        val json = parserJson.encodeToString(ser, value)
        val back = parserJson.decodeFromString(ser, json)
        check(name, back == value, json)
    }

    roundTrip(
        SavableCombiningParams(
            stitchMode = 1, spacing = 8, scaleSmallImagesToLarge = true,
            backgroundColor = 123, fadingEdgesMode = 2, alignment = StitchAlignment.Center
        ),
        SavableCombiningParams::class.java, "saver-SavableCombiningParams"
    )
    roundTrip(DrawOnBackgroundParams(width = 800, height = 600, color = 42),
        DrawOnBackgroundParams::class.java, "saver-DrawOnBackgroundParams")
    roundTrip(HelperGridParams(color = 7, cellWidth = 10f, cellHeight = 10f, linesWidth = 1f, enabled = true),
        HelperGridParams::class.java, "saver-HelperGridParams")
    roundTrip(CipherType.AES_NO_PADDING, CipherType::class.java, "saver-CipherType")

    println("ALL RUNTIME CHECKS PASSED")
}

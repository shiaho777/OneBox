package com.shifenmiao.model.pay.alipay

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import java.math.BigDecimal

/**
 * BigDecimal 序列化为 JSON 数字字面量(不加引号),与 Gson 时代的 number 产物对齐。
 * 读取兼容数字与字符串两种形态。
 */
object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        // JsonPrimitive(Number) 写不加引号的数字字面量,保持支付宝 goodsDetail[].price 的
        // number 类型(Gson 按 BigDecimal 数字写出,非字符串)
        if (encoder is JsonEncoder) {
            encoder.encodeJsonElement(JsonPrimitive(value))
        } else {
            encoder.encodeString(value.toPlainString())
        }
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal(decoder.decodeString())
    }
}

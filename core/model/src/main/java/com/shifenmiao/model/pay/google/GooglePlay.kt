package com.shifenmiao.model.pay.google

import com.shifenmiao.model.pay.PayResult
import com.shifenmiao.model.pay.PrePayResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 后端 GET /google/pay/products 返回的商品目录项: productId 与积分的映射 */
@Serializable
data class GooglePlayProduct(
    @SerialName("product_id") val productId: String = "",
    val points: Int = 0,
)

/** POST /google/pay/verify 请求体: 服务端验单并幂等发放积分 */
@Serializable
data class GooglePayVerifyRequest(
    @SerialName("product_id") val productId: String = "",
    @SerialName("purchase_token") val purchaseToken: String = "",
)

/** Play Billing 支付成功结果(只携带验单所需字段, 不泄露 billing 类型到 model 层) */
@Serializable
data class GooglePlayPayResult(
    val productId: String = "",
    val purchaseToken: String = "",
) : PayResult

/** Google Play "下单"结果: 客户端无需后端下单, 仅携带待购买的 productId */
@Serializable
data class GooglePlayOrder(
    val productId: String = "",
) : PrePayResponse

/** 商品目录 + Play 本地化价格合并后的 UI 展示模型 */
@Serializable
data class PlayProduct(
    val productId: String = "",
    val points: Int = 0,
    val title: String = "",
    val formattedPrice: String = "",
)

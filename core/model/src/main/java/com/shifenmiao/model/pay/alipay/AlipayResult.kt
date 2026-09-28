package com.shifenmiao.model.pay.alipay

import android.os.Parcelable
import com.shifenmiao.model.pay.PayResult
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * result字符串返回码 含义
 * 9000 订单支付成功。
 * 8000 正在处理中，支付结果未知（有可能已经支付成功），请查询商家订单列表中订单的支付状态。
 * 4000 订单支付失败。
 * 5000 重复请求。
 * 6001 用户中途取消。
 * 6002 网络连接出错。
 * 6004 支付结果未知（有可能已经支付成功），请查询商家订单列表中订单的支付状态。
 * 其它 其它支付错误。
 * 切记这个字符串中 result 不是对象，是字符串
 */
@Serializable
@Parcelize
data class AlipayResult(
    @SerialName("memo") val memo: String,
    @SerialName("result") val result: String,
    @SerialName("resultStatus") val resultStatus: String
) : Parcelable, PayResult

@Serializable
@Parcelize
data class Result(
    @SerialName("alipay_trade_app_pay_response") val alipayTradeAppPayResponse: AlipayTradeAppPayResponse,
    @SerialName("sign") val sign: String,
    @SerialName("sign_type") val signType: String
) : Parcelable

@Serializable
@Parcelize
data class AlipayTradeAppPayResponse(
    @SerialName("code") val code: String,
    @SerialName("msg") val msg: String,
    @SerialName("app_id") val appId: String,
    @SerialName("out_trade_no") val outTradeNo: String,
    @SerialName("trade_no") val tradeNo: String,
    @SerialName("total_amount") val totalAmount: String,
    @SerialName("seller_id") val sellerId: String,
    @SerialName("charset") val charset: String,
    @SerialName("timestamp") val timestamp: String
) : Parcelable
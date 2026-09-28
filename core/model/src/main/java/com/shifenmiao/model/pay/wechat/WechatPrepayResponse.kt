package com.shifenmiao.model.pay.wechat

import com.shifenmiao.model.pay.PrePayResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WechatPrepayResponse(
    val appId: String = "",
    val partnerId: String = "",
    val prepayId: String = "",
    @SerialName("package") val packageStr: String = "",
    val nonceStr: String = "",
    val timeStamp: String = "",
    val sign: String = ""
) : PrePayResponse
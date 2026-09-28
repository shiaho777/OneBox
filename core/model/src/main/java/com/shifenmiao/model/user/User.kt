package com.shifenmiao.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class User(
    val id: Int = 0,
    val username: String? = "",
    val nickname: String? = "",
    val avatar: String? = null,
    val email: String? = null,
    val provider: String? = null,
    val openid: String? = null,
    val points: Int? = 0,
    val invitationCode: String? = null,
    val phone: String? = null,
    val vipLevel: Int? = 0,
    val totalRechargeAmount: Double? = 0.0,
    /** 邮箱是否已验证:邮箱注册为 false,Google/微信登录为 true;旧缓存缺省按未验证处理 */
    val confirmed: Boolean = false,
    /** 账号创建时间(服务端 RFC3339);旧缓存没有该字段,按老用户豁免处理,见 TokenStorage.isVerified */
    val createdAt: String? = null,
) : Parcelable

@Parcelize
@Serializable
data class WechatLoginRequest(
    val app_id: String,
    val code: String
) : Parcelable

@Parcelize
@Serializable
data class GoogleLoginRequest(
    val id_token: String
) : Parcelable

@Parcelize
@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String
) : Parcelable

@Parcelize
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
) : Parcelable

@Parcelize
@Serializable
data class Login(
    val jwt: String = "",
    val user: User = User()
) : Parcelable

@Parcelize
@Serializable
data class WechatUserInfo(
    @SerialName("openid") val openid: String,
    @SerialName("nickname") val nickname: String,
    @SerialName("sex") val sex: Int,
    @SerialName("province") val province: String,
    @SerialName("city") val city: String,
    @SerialName("country") val country: String,
    @SerialName("headimgurl") val headimgurl: String,
    @SerialName("privilege") val privilege: List<String> = emptyList(), // Provide default value
    @SerialName("unionid") val unionid: String
) : Parcelable
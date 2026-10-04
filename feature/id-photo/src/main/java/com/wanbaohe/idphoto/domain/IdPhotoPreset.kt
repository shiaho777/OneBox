package com.wanbaohe.idphoto.domain

import android.content.Context
import androidx.annotation.StringRes

/**
 * 一条预置证件照规格。
 *
 * 名称/描述只存字符串资源 id：播种进库时按当前语言解析成本地化文案，
 * 因此每个「语言 × 库」里都是当地语言的预置尺寸。
 *
 * [key] 仅用于目录自身的可读性（与资源名对应），不落库。
 */
data class IdPhotoPreset(
    val key: String,
    val widthMm: Float,
    val heightMm: Float,
    val widthPx: Int,
    val heightPx: Int,
    @StringRes val nameRes: Int,
    @StringRes val descRes: Int,
) {
    fun toIdPhotoSize(context: Context): IdPhotoSize = IdPhotoSize(
        name = context.getString(nameRes),
        widthMm = widthMm,
        heightMm = heightMm,
        widthPx = widthPx,
        heightPx = heightPx,
        description = context.getString(descRes),
        isPreset = true,
    )
}

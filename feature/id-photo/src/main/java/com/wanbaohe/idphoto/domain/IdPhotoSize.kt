package com.wanbaohe.idphoto.domain

/**
 * 证件照尺寸规格
 *
 * 预置尺寸不再硬编码在这里，而是按语言环境定义在 [IdPhotoPresetCatalog]：
 * 播种时按当前语言取本地化名称/描述写进（按语言分库的）Room 库，
 * 因此 [name]/[description] 里存的就是该语言的展示文本，UI 直接用，无需再做语言映射。
 */
data class IdPhotoSize(
    val id: Long = 0,
    val name: String,                      // 名称（预置项为该语言的本地化名称，用户自建项为用户输入）
    val widthMm: Float,                    // 宽度（毫米）
    val heightMm: Float,                   // 高度（毫米）
    val widthPx: Int,                      // 宽度（像素，300dpi）
    val heightPx: Int,                     // 高度（像素，300dpi）
    val description: String = "",          // 描述
    val isPreset: Boolean = false,         // 是否预置
    val createdAt: Long = System.currentTimeMillis(),
) {
    /**
     * 获取宽高比
     */
    val aspectRatio: Float
        get() = widthPx.toFloat() / heightPx.toFloat()

    /**
     * 格式化尺寸显示
     */
    fun formatSize(): String = "${widthMm}×${heightMm}mm"

    /**
     * 格式化像素显示
     */
    fun formatPixels(): String = "${widthPx}×${heightPx}px"
}

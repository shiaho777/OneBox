package com.shifenmiao.marktodo.model

import androidx.compose.runtime.Immutable

/**
 * 待办标签：驱动首页标签筛选 chips，并为任务标签提供配色。
 *
 * 任务的 tags 存标签名，按 [name] 与标签关联。
 *
 * @property id Unique identifier for the tag.
 * @property name Display name (预置标签已按建库语言本地化).
 * @property colorArgb Optional chip accent color (ARGB); null = 使用系统主题色.
 * @property isPreset 是否为预置标签（预置标签不可删除）.
 */
@Immutable
data class TodoTag(
    val id: String,
    val name: String,
    val colorArgb: Int? = null,
    val isPreset: Boolean = false
)

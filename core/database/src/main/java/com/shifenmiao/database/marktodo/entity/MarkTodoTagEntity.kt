package com.shifenmiao.database.marktodo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 待办标签表：驱动首页顶部的标签筛选 chips，并为任务标签提供配色。
 *
 * 任务的 tags 仍存标签名（List<String>），按 name 与本表关联；
 * 预置标签(is_preset=1)在迁移/首启时按当前语言写入本地化名称。
 */
@Entity(tableName = "marktodo_tag")
data class MarkTodoTagEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color_argb") val colorArgb: Int? = null,
    @ColumnInfo(name = "sort_order") val sortOrder: Int = 0,
    @ColumnInfo(name = "is_preset") val isPreset: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),
)

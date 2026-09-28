package com.shifenmiao.model.prompt

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * DTO stands for Data Transfer Object.
 */
@Parcelize
@Serializable
data class PromptWithGroupsDTO(
    val prompt: PromptDTO,
    val groups: List<GroupDTO>
) : Parcelable

@Parcelize
@Serializable
data class PromptDTO(
    val id: Int,
    val name: String,
    val emoji: String,
    val prompt: String,
    val description: String,
    val templates: String = "",
    val placeholder: String = "",
    // 原 @Contextual Date:该类无调用方,kotlinx 无 Date serializer,降为 epoch millis
    val updateTime: Long,
    val canEdit: Boolean
) : Parcelable

@Parcelize
@Serializable
data class GroupDTO(
    val groupId: Int,
    val name: String
) : Parcelable
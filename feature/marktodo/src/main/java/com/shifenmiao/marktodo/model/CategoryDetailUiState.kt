package com.shifenmiao.marktodo.model

import androidx.compose.runtime.Immutable

/**
 * 分类详情页的 UI 状态
 * 采用不可变数据类确保状态更新的可预测性和性能
 */
@Immutable
data class CategoryDetailUiState(
    val category: TodoCategory? = null,
    val filteredTasks: List<TodoTask> = emptyList(),
    val tags: List<TodoTag> = emptyList(),
    val filterMode: TaskFilterMode = TaskFilterMode.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val hasTasks: Boolean get() = filteredTasks.isNotEmpty()
    val totalCount: Int get() = category?.totalCount ?: 0
    val completedCount: Int get() = category?.completedCount ?: 0
    val starredCount: Int get() = filteredTasks.count { it.isStarred }
}

/**
 * 任务筛选模式
 */
enum class TaskFilterMode {
    ALL,        // 全部
    ACTIVE,     // 未完成
    COMPLETED,  // 已完成
    STARRED     // 已标星
}

/**
 * 分类详情页的用户事件
 */
sealed interface CategoryDetailUiEvent {
    // 任务操作
    data class TaskClicked(val task: TodoTask) : CategoryDetailUiEvent
    data object AddTaskClicked : CategoryDetailUiEvent
    data class ToggleTaskComplete(val task: TodoTask) : CategoryDetailUiEvent
    data class ToggleTaskStar(val task: TodoTask) : CategoryDetailUiEvent
    data class DeleteTask(val task: TodoTask) : CategoryDetailUiEvent

    // 筛选
    data class ChangeFilter(val filterMode: TaskFilterMode) : CategoryDetailUiEvent

    // 主题操作（头部卡片的编辑/删除图标）
    data object EditCategoryClicked : CategoryDetailUiEvent
    data object DeleteCategoryClicked : CategoryDetailUiEvent
    data object DismissDeleteCategory : CategoryDetailUiEvent
    data object ConfirmDeleteCategory : CategoryDetailUiEvent

    // 导航
    data object NavigateBack : CategoryDetailUiEvent
}

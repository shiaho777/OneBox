package com.shifenmiao.marktodo.model

import androidx.compose.runtime.Immutable

/**
 * Represents the UI state for the MarkTodo screen.
 * Using immutable data classes ensures predictable state updates and better performance.
 */
@Immutable
data class MarkTodoUiState(
    val categories: List<TodoCategory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    /** 排序模式：长按卡片菜单「排序」进入，卡片可拖拽重排，返回键/顶栏完成退出 */
    val isReorderMode: Boolean = false,
    val statusTab: TodoStatusTab = TodoStatusTab.ACTIVE,
    val selectedTagName: String? = null, // null = 全部标签
    val tagFilterVisible: Boolean = false, // 标签筛选行默认隐藏，点击任务上的标签后显示
    val isGridView: Boolean = true
) {
    val hasCategories: Boolean get() = categories.isNotEmpty()
}

/**
 * 首页底部状态页签：全部 / 进行中 / 已完成
 */
enum class TodoStatusTab {
    ALL, ACTIVE, COMPLETED
}

/**
 * Represents dialog states using sealed interface for type safety.
 */
sealed interface DialogState {
    data object Dismissed : DialogState

    data class DeleteCategoryConfirm(
        val category: TodoCategory
    ) : DialogState
}

/**
 * Represents user actions/events in the MarkTodo screen.
 */
sealed interface MarkTodoUiEvent {
    // Category actions
    data class CategoryClicked(val category: TodoCategory) : MarkTodoUiEvent
    data object AddCategoryClicked : MarkTodoUiEvent
    data class EditCategoryClicked(val category: TodoCategory) : MarkTodoUiEvent
    data class DeleteCategory(val category: TodoCategory) : MarkTodoUiEvent
    data class ConfirmDeleteCategory(val category: TodoCategory) : MarkTodoUiEvent

    // Task actions
    data class TaskClicked(val task: TodoTask) : MarkTodoUiEvent
    data class AddTaskClicked(val category: TodoCategory? = null) : MarkTodoUiEvent
    data class ToggleTaskComplete(val task: TodoTask) : MarkTodoUiEvent
    data class ToggleTaskStar(val task: TodoTask) : MarkTodoUiEvent

    // 状态页签 / 标签筛选 / 视图切换
    data class SelectStatusTab(val tab: TodoStatusTab) : MarkTodoUiEvent
    /** null = 清除筛选并收起筛选行 */
    data class SelectTag(val tagName: String?) : MarkTodoUiEvent
    data class TagOnTaskClicked(val tagName: String) : MarkTodoUiEvent
    data class SetGridView(val isGrid: Boolean) : MarkTodoUiEvent

    // 排序模式（长按卡片菜单「排序」进入）
    data class SetReorderMode(val enabled: Boolean) : MarkTodoUiEvent
    /** 拖拽中的相邻交换（可见列表内的 from/to 下标；组件负责合并回全量顺序并持久化） */
    data class ReorderCategories(val fromIndex: Int, val toIndex: Int) : MarkTodoUiEvent

    // Dialog actions
    data object DismissDialog : MarkTodoUiEvent

    // Navigation
    data object NavigateBack : MarkTodoUiEvent
}

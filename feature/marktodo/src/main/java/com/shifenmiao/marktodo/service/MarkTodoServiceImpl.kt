package com.shifenmiao.marktodo.service

import com.shifenmiao.database.activity.ActivityLogRecorder
import com.shifenmiao.database.marktodo.entity.MarkTodoCategoryEntity
import com.shifenmiao.database.marktodo.entity.MarkTodoTagEntity
import com.shifenmiao.database.marktodo.entity.MarkTodoTaskEntity
import com.shifenmiao.database.marktodo.repo.MarkTodoRepository
import com.shifenmiao.interfaces.singleton.AppContext
import com.shifenmiao.marktodo.R
import com.shifenmiao.marktodo.data.iconFromKey
import com.shifenmiao.marktodo.data.toModel
import com.shifenmiao.marktodo.model.TodoCategory
import com.shifenmiao.marktodo.model.TodoTask
import com.shifenmiao.model.todo.CategoryInput
import com.shifenmiao.model.todo.CategoryLookup
import com.shifenmiao.model.todo.MarkTodoServiceInterface
import com.shifenmiao.model.todo.TaskInput
import com.shifenmiao.model.todo.TodoCategoryDto
import com.shifenmiao.model.todo.TodoTaskDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 待办清单业务门面 — UI 层与 Agent 层共用的唯一写入入口。
 *
 * 职责:
 *  - 入参校验
 *  - 调用 [MarkTodoRepository] 写库
 *  - 调用 [ActivityLogRecorder] 写审计日志
 *
 * 不做的事:
 *  - 不持有 UI 状态
 *  - 不暴露 Flow（订阅仍走 Repository，Component 直接 inject Repository 用作只读）
 */
@Singleton
class MarkTodoServiceImpl @Inject constructor(
    private val repository: MarkTodoRepository,
    private val activityLogRecorder: ActivityLogRecorder,
) : MarkTodoServiceInterface {

    // ── 分类操作 ────────────────────────────────────

    override suspend fun createCategory(
        input: CategoryInput,
        source: String,
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val categoryId = input.id?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
            val entity = MarkTodoCategoryEntity(
                id = categoryId,
                title = input.title.trim(),
                iconKey = input.iconKey,
                description = input.description?.trim()?.takeIf { it.isNotBlank() },
                colorArgb = input.colorArgb,
                isPinned = input.isPinned,
                // 新主题追加到列表末尾
                sortOrder = (repository.getMaxCategorySortOrder() ?: -1) + 1,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )
            repository.upsertCategory(entity)

            activityLogRecorder.recordMarkTodo(
                entityId = categoryId,
                entityType = "CATEGORY",
                actionType = "CREATE",
                source = source,
                title = AppContext.getString(R.string.marktodo_log_category_created, input.title),
                description = AppContext.getString(R.string.marktodo_log_category_created, input.title)
            )
            categoryId
        }
    }

    override suspend fun updateCategory(
        categoryId: String,
        input: CategoryInput,
        source: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val existing = repository.getCategoryWithTasks(categoryId)?.category
            val entity = MarkTodoCategoryEntity(
                id = categoryId,
                title = input.title.trim(),
                iconKey = input.iconKey,
                description = input.description?.trim()?.takeIf { it.isNotBlank() },
                colorArgb = input.colorArgb,
                isPinned = input.isPinned,
                sortOrder = existing?.sortOrder ?: input.sortOrder,
                createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )
            repository.upsertCategory(entity)

            activityLogRecorder.recordMarkTodo(
                entityId = categoryId,
                entityType = "CATEGORY",
                actionType = "UPDATE",
                source = source,
                title = AppContext.getString(R.string.marktodo_log_category_updated, input.title),
                description = AppContext.getString(R.string.marktodo_log_category_updated, input.title)
            )
        }
    }

    override suspend fun deleteCategory(
        categoryId: String,
        categoryTitle: String,
        source: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            repository.deleteCategory(categoryId)

            activityLogRecorder.recordMarkTodo(
                entityId = categoryId,
                entityType = "CATEGORY",
                actionType = "DELETE",
                source = source,
                title = AppContext.getString(R.string.marktodo_log_category_deleted, categoryTitle),
                description = AppContext.getString(R.string.marktodo_log_category_deleted_desc, categoryTitle)
            )
        }
    }

    override suspend fun reorderCategories(
        orderedIds: List<String>,
        source: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            orderedIds.forEachIndexed { index, categoryId ->
                repository.updateCategoryOrder(categoryId, index)
            }

            activityLogRecorder.recordMarkTodo(
                entityId = "batch",
                entityType = "CATEGORY",
                actionType = "REORDER",
                source = source,
                title = AppContext.getString(R.string.marktodo_log_categories_reordered),
                description = AppContext.getContext().getString(R.string.marktodo_log_categories_reordered_desc, orderedIds.size)
            )
        }
    }

    // ── 任务操作 ────────────────────────────────────

    override suspend fun createTask(
        input: TaskInput,
        source: String,
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val taskId = UUID.randomUUID().toString()
            val entity = MarkTodoTaskEntity(
                id = taskId,
                categoryId = input.categoryId,
                title = input.title.trim(),
                note = input.note?.trim()?.takeIf { it.isNotBlank() },
                startDate = System.currentTimeMillis(),
                dueDate = input.dueDateMillis,
                tags = input.tags,
                isCompleted = false,
                isStarred = false,
                priority = input.priority.coerceIn(0, 2),
                completedAt = null,
                // repository.addTask 会按分类内 max(sortOrder)+1 覆盖此值
                sortOrder = 0,
            )
            repository.addTask(entity)

            activityLogRecorder.recordMarkTodo(
                entityId = taskId,
                entityType = "TASK",
                actionType = "CREATE",
                source = source,
                title = AppContext.getString(R.string.marktodo_log_task_created, input.title),
                description = AppContext.getString(R.string.marktodo_log_task_created, input.title)
            )
            taskId
        }
    }

    override suspend fun updateTask(
        taskId: String,
        input: TaskInput,
        source: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val existing = repository.getTaskById(taskId)
            val entity = MarkTodoTaskEntity(
                id = taskId,
                categoryId = input.categoryId,
                title = input.title.trim(),
                note = input.note?.trim()?.takeIf { it.isNotBlank() },
                startDate = existing?.startDate ?: System.currentTimeMillis(),
                dueDate = input.dueDateMillis,
                tags = input.tags,
                isCompleted = existing?.isCompleted ?: false,
                isStarred = existing?.isStarred ?: false,
                priority = input.priority.coerceIn(0, 2),
                completedAt = existing?.completedAt,
                sortOrder = existing?.sortOrder ?: 0,
                createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            )
            repository.updateTask(entity)

            activityLogRecorder.recordMarkTodo(
                entityId = taskId,
                entityType = "TASK",
                actionType = "UPDATE",
                source = source,
                title = AppContext.getString(R.string.marktodo_log_task_updated, input.title),
                description = AppContext.getString(R.string.marktodo_log_task_updated, input.title)
            )
        }
    }

    override suspend fun deleteTask(
        taskId: String,
        taskTitle: String,
        source: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            repository.deleteTask(taskId)

            activityLogRecorder.recordMarkTodo(
                entityId = taskId,
                entityType = "TASK",
                actionType = "DELETE",
                source = source,
                title = AppContext.getString(R.string.marktodo_log_task_deleted, taskTitle),
                description = AppContext.getString(R.string.marktodo_log_task_deleted, taskTitle)
            )
        }
    }

    override suspend fun toggleTaskComplete(
        taskId: String,
        isCompleted: Boolean,
        taskTitle: String,
        source: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            repository.setTaskCompleted(taskId, isCompleted)

            activityLogRecorder.recordMarkTodo(
                entityId = taskId,
                entityType = "TASK",
                actionType = "TOGGLE_COMPLETE",
                source = source,
                title = AppContext.getString(
                    if (isCompleted) R.string.marktodo_log_task_completed
                    else R.string.marktodo_log_task_reopened,
                    taskTitle
                ),
                description = AppContext.getString(
                    if (isCompleted) R.string.marktodo_log_task_completed
                    else R.string.marktodo_log_task_reopened,
                    taskTitle
                )
            )
        }
    }

    override suspend fun toggleTaskStar(
        taskId: String,
        isStarred: Boolean,
        taskTitle: String,
        source: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            repository.setTaskStarred(taskId, isStarred)

            activityLogRecorder.recordMarkTodo(
                entityId = taskId,
                entityType = "TASK",
                actionType = "TOGGLE_STAR",
                source = source,
                title = AppContext.getString(
                    if (isStarred) R.string.marktodo_log_task_starred
                    else R.string.marktodo_log_task_unstarred,
                    taskTitle
                ),
                description = AppContext.getString(
                    if (isStarred) R.string.marktodo_log_task_starred
                    else R.string.marktodo_log_task_unstarred,
                    taskTitle
                )
            )
        }
    }

    override suspend fun reorderTasks(
        categoryId: String,
        orderedIds: List<String>,
        source: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            orderedIds.forEachIndexed { index, taskId ->
                repository.updateTaskOrder(taskId, index)
            }

            activityLogRecorder.recordMarkTodo(
                entityId = categoryId,
                entityType = "TASK",
                actionType = "REORDER",
                source = source,
                title = AppContext.getString(R.string.marktodo_log_tasks_reordered),
                description = AppContext.getContext().getString(R.string.marktodo_log_tasks_reordered_desc, orderedIds.size)
            )
        }
    }

    // ── 标签 ────────────────────────────────────────

    /**
     * 预置标签兜底种子（老库升级后表为空时补齐；新装由 FeatureDatabase onCreate 本地化写入）。
     *
     * @param localizedNames 预置标签 id → 当前语言名称（由调用方从字符串资源解析）
     */
    suspend fun ensurePresetTags(localizedNames: Map<String, String>): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val now = System.currentTimeMillis()
            val presets = localizedNames.entries.mapIndexed { index, (id, name) ->
                MarkTodoTagEntity(
                    id = id,
                    name = name,
                    colorArgb = PRESET_TAG_COLORS[id],
                    sortOrder = index,
                    isPreset = true,
                    createdAt = now,
                    updatedAt = now,
                )
            }
            repository.ensurePresetTags(presets)
        }
    }

    /** 新建自定义标签（追加到标签池末尾），返回新标签 id；同名（忽略大小写）标签已存在时直接返回其 id，不产生重复行 */
    suspend fun createTag(
        name: String,
        colorArgb: Int?,
        source: String,
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val trimmed = name.trim()
            require(trimmed.isNotEmpty()) { "Tag name is blank" }
            // 按名字关联的模型下重名是脏数据，并发/直调时在此兜底
            repository.getTags().firstOrNull { it.name.equals(trimmed, ignoreCase = true) }
                ?.let { return@runCatching it.id }
            val now = System.currentTimeMillis()
            val tagId = UUID.randomUUID().toString()
            repository.upsertTag(
                MarkTodoTagEntity(
                    id = tagId,
                    name = trimmed,
                    colorArgb = colorArgb,
                    sortOrder = (repository.getTags().maxOfOrNull { it.sortOrder } ?: -1) + 1,
                    isPreset = false,
                    createdAt = now,
                    updatedAt = now,
                )
            )

            activityLogRecorder.recordMarkTodo(
                entityId = tagId,
                entityType = "TAG",
                actionType = "CREATE",
                source = source,
                title = AppContext.getString(R.string.marktodo_log_tag_created, trimmed),
                description = AppContext.getString(R.string.marktodo_log_tag_created, trimmed)
            )
            tagId
        }
    }

    /** 删除自定义标签（预置标签不允许删除，调用方负责拦截） */
    suspend fun deleteTag(
        tagId: String,
        tagName: String,
        source: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            repository.deleteTag(tagId)

            activityLogRecorder.recordMarkTodo(
                entityId = tagId,
                entityType = "TAG",
                actionType = "DELETE",
                source = source,
                title = AppContext.getString(R.string.marktodo_log_tag_deleted, tagName),
                description = AppContext.getString(R.string.marktodo_log_tag_deleted, tagName)
            )
        }
    }

    // ── 只读查询（供 Component 与 Agent 使用）────────

    suspend fun getDashboard(): List<TodoCategory> = withContext(Dispatchers.IO) {
        repository.getDashboard().map { rel ->
            val safeIconKey = rel.category.iconKey.ifBlank { "inbox" }
            rel.category.toModel(
                icon = iconFromKey(safeIconKey),
                tasks = rel.tasks.map { it.toModel() }
            )
        }
    }

    suspend fun getCategoryWithTasks(categoryId: String): TodoCategory? = withContext(Dispatchers.IO) {
        repository.getCategoryWithTasks(categoryId)?.let { rel ->
            val safeIconKey = rel.category.iconKey.ifBlank { "inbox" }
            rel.category.toModel(
                icon = iconFromKey(safeIconKey),
                tasks = rel.tasks.map { it.toModel() }
            )
        }
    }

    suspend fun getTask(taskId: String): TodoTask? = withContext(Dispatchers.IO) {
        repository.getTaskById(taskId)?.toModel()
    }

    // ── DTO 读操作（实现接口，供 AgentTool 使用）────────

    override suspend fun getDashboardDto(): List<TodoCategoryDto> = withContext(Dispatchers.IO) {
        repository.getDashboard().map { rel ->
            TodoCategoryDto(
                id = rel.category.id,
                title = rel.category.title,
                iconKey = rel.category.iconKey.ifBlank { "inbox" },
                description = rel.category.description,
                colorArgb = rel.category.colorArgb,
                isPinned = rel.category.isPinned,
                tasks = rel.tasks.map { task ->
                    TodoTaskDto(
                        id = task.id,
                        title = task.title,
                        note = task.note,
                        startDate = task.startDate,
                        dueDate = task.dueDate,
                        tags = task.tags,
                        isCompleted = task.isCompleted,
                        isStarred = task.isStarred,
                        priority = task.priority,
                        completedAt = task.completedAt
                    )
                }
            )
        }
    }

    override suspend fun getTaskDto(taskId: String): TodoTaskDto? = withContext(Dispatchers.IO) {
        repository.getTaskById(taskId)
            ?.let { task ->
                TodoTaskDto(
                    id = task.id,
                    title = task.title,
                    note = task.note,
                    startDate = task.startDate,
                    dueDate = task.dueDate,
                    tags = task.tags,
                    isCompleted = task.isCompleted,
                    isStarred = task.isStarred,
                    priority = task.priority,
                    completedAt = task.completedAt
                )
            }
    }

    override suspend fun findCategoryByTitle(title: String): CategoryLookup? = withContext(Dispatchers.IO) {
        repository.getDashboard()
            .find { it.category.title.equals(title, ignoreCase = true) }
            ?.let { CategoryLookup(id = it.category.id, title = it.category.title) }
    }
}

/** 预置标签 id → ARGB 颜色（与 raw/marktodo_presets.sql 中的 color_argb 一致） */
private val PRESET_TAG_COLORS: Map<String, Int> = mapOf(
    "preset_work" to 0xFF5B8DEF.toInt(),
    "preset_study" to 0xFF8B5CF6.toInt(),
    "preset_life" to 0xFFEC4899.toInt(),
    "preset_health" to 0xFF10B981.toInt(),
    "preset_travel" to 0xFFF59E0B.toInt(),
)

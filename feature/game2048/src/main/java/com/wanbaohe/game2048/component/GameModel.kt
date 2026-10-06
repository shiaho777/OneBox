package com.wanbaohe.game2048.component

import androidx.compose.runtime.Immutable

// ─── 滑动方向 ──────────────────────────────────────────────────────────────────

/** 四个滑动方向 */
enum class Direction { Up, Down, Left, Right }

// ─── UI 状态快照 ───────────────────────────────────────────────────────────────

/**
 * 2048 游戏页面的完整 UI 状态
 *
 * @param grid      4×4 棋盘，0 表示空格
 * @param score     当前得分
 * @param bestScore 历史最高分
 * @param isGameOver 游戏是否结束（无法移动）
 * @param isWon     是否已达成 2048
 * @param hasShownWinDialog 是否已弹过胜利弹窗（避免重复弹窗）
 * @param soundEnabled 音效总开关（关时同时停 BGM）
 * @param invalidMoveNonce 无效滑动计数，每次无效滑动 +1，驱动棋盘抖动反馈
 */
@Immutable
data class Game2048UiState(
    val grid: List<List<Int>> = List(GRID_SIZE) { List(GRID_SIZE) { 0 } },
    val score: Int = 0,
    val bestScore: Int = 0,
    val isGameOver: Boolean = false,
    val isWon: Boolean = false,
    val hasShownWinDialog: Boolean = false,
    val soundEnabled: Boolean = true,
    val invalidMoveNonce: Int = 0,
) {
    companion object {
        const val GRID_SIZE = 4
    }
}


package com.wanbaohe.sudoku.logic

/**
 * 一条战绩。完成/放弃都会记一条, 用 [completed] 区分。
 *
 * @param timestampMillis 对局结束(完成或放弃)时刻
 * @param timeSec 本局用时(秒), 放弃局是放弃时的已用时
 */
data class SudokuRecord(
    val timestampMillis: Long,
    val difficulty: SudokuDifficulty,
    val timeSec: Int,
    val completed: Boolean
)

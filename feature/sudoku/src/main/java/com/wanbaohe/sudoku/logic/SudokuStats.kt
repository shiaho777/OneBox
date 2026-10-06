package com.wanbaohe.sudoku.logic

import java.util.Calendar

/**
 * 统计页的数据聚合, 全部纯函数, 输入战绩列表输出图表数据。
 */
object SudokuStats {

    fun totalGames(records: List<SudokuRecord>): Int = records.size

    fun completedGames(records: List<SudokuRecord>): Int = records.count { it.completed }

    fun unfinishedGames(records: List<SudokuRecord>): Int = totalGames(records) - completedGames(records)

    /** 0..1, 没有对局时返回 0。 */
    fun winRate(records: List<SudokuRecord>): Float {
        val total = totalGames(records)
        if (total == 0) return 0f
        return completedGames(records).toFloat() / total
    }

    /** 完成局的平均用时(秒), 无完成局返回 null。 */
    fun averageTimeSeconds(records: List<SudokuRecord>): Int? {
        val completed = records.filter { it.completed }
        if (completed.isEmpty()) return null
        return completed.sumOf { it.timeSec } / completed.size
    }

    /** 全部完成局里的最快用时(秒), 无完成局返回 null。 */
    fun bestTimeSeconds(records: List<SudokuRecord>): Int? =
        records.filter { it.completed }.minOfOrNull { it.timeSec }

    /** 每个难度的完成数, 难度分布柱状图用。 */
    fun completedByDifficulty(records: List<SudokuRecord>): Map<SudokuDifficulty, Int> =
        SudokuDifficulty.entries.associateWith { difficulty ->
            records.count { it.completed && it.difficulty == difficulty }
        }

    /**
     * 近 [days] 天(含今天)每日完成数, 完成趋势折线图用。
     * 返回长度 = days 的列表, 索引 0 是最早那天, 最后一天是今天。
     * 按本地时区的自然日分桶, 不用 UTC。
     */
    fun dailyCompletions(
        records: List<SudokuRecord>,
        days: Int,
        nowMillis: Long
    ): List<Int> {
        val firstDayStart = dayStartMillis(nowMillis) - (days - 1) * DAY_MILLIS
        val buckets = IntArray(days)
        records.forEach { record ->
            if (!record.completed) return@forEach
            val dayIndex = ((dayStartMillis(record.timestampMillis) - firstDayStart) / DAY_MILLIS).toInt()
            if (dayIndex in buckets.indices) buckets[dayIndex]++
        }
        return buckets.toList()
    }

    private const val DAY_MILLIS = 24L * 60 * 60 * 1000

    private fun dayStartMillis(millis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

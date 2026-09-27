package com.t8rin.imagetoolbox.core.crash.data

import com.t8rin.imagetoolbox.core.domain.remote.AnalyticsManager
import com.t8rin.imagetoolbox.core.domain.remote.ErrorEvent
import com.t8rin.imagetoolbox.core.domain.remote.ErrorKind
import com.t8rin.imagetoolbox.core.domain.remote.ErrorReporter
import com.t8rin.imagetoolbox.core.domain.remote.ErrorReporter.Companion.SOURCE_KEY
import com.t8rin.logger.makeLog
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 统一错误上报的实现: 把 [ErrorEvent] 交给当前渠道的原生崩溃/异常统计。
 *
 * 唯一的后端是 [AnalyticsManager], 它按 flavor 分别实现:
 * - google 渠道 → Firebase Crashlytics(CI 已配 mapping 上传, 堆栈可反解)
 * - 国内渠道 / foss → no-op, 接入国产崩溃统计后自然就有数据了
 *   (要改的只有对应 `AnalyticsManagerImpl`, 见其中注释)
 *
 * 所以"换一家崩溃统计"的改动面始终是一个文件: 调用方只认 [ErrorReporter],
 * 不需要知道背后是谁, 也不需要判断渠道。
 *
 * 上报链路的异常在这里全部吞掉 —— 它不能升级成业务里的新异常。
 */
@Singleton
class ErrorReporterImpl @Inject constructor(
    private val analyticsManager: AnalyticsManager,
) : ErrorReporter {

    override fun report(event: ErrorEvent) {
        runCatching {
            val throwable = event.throwable ?: ReportedAnomaly(
                source = event.source,
                detail = event.message,
            )
            val keys = event.extra + mapOf(
                SOURCE_KEY to event.source,
                "error_kind" to event.kind.name.lowercase(Locale.ROOT),
            )
            when (event.kind) {
                // 致命: 与 GlobalExceptionHandler 走同一条通道
                ErrorKind.CRASH -> analyticsManager.sendReport(throwable)
                // 非致命: 用户还能继续用, 但这事不该发生
                ErrorKind.ERROR, ErrorKind.ANOMALY ->
                    analyticsManager.reportNonFatal(throwable = throwable, keys = keys)
            }
        }.onFailure {
            it.makeLog("ErrorReporter")
        }
    }
}

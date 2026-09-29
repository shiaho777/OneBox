package com.wanbaohe.core.ui.review

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewManagerFactory
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.settings.di.SettingsStateEntryPoint
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.utils.makeLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

/**
 * Google Play 应用内评分(In-App Review),google 渠道专用实现。
 *
 * 弹出的是 Play 托管的半屏评分层,不离开 App。注意:
 * Play 对该弹层有频率配额(约每月一次),配额耗尽时 launchReviewFlow 静默不弹、
 * API 不回调结果,所以这里记的是"发起过尝试",不代表用户真的看到了弹层;
 * 冷却时间因此与 Play 配额量级对齐,偶尔烧掉的尝试不算浪费。
 *
 * 触发时机:文件保存成功 Toast 消失之后(AppToastHost.showFileSuccessToast →
 * AppActivity 注册的 successSaveHandler)。计数与冷却状态统一维护在全局设置 DataStore。
 */
object InAppReviewPrompt {

    /** 累计文件保存成功达到该次数后才考虑弹评分层 */
    private const val PROMPT_AFTER_SAVES = 3

    /** 每个安装最多发起弹出的次数 */
    private const val MAX_PROMPTS = 3

    /** 两次弹出之间的最短间隔,与 Play 配额量级对齐 */
    private val PROMPT_COOLDOWN_MS = TimeUnit.DAYS.toMillis(30)

    /** Toast 消失后稍作停顿,避免评分弹层紧跟 Toast 出现显得突兀 */
    private const val PROMPT_DELAY_MS = 1200L

    /** 防止排队的多个成功 Toast 并发触发重复弹层 */
    private val promptInFlight = AtomicBoolean(false)

    /**
     * 保存成功后的评分弹层入口:先累计保存次数,
     * 弹出次数与冷却时间都满足才向 Play 发起弹层。
     * 整体 runCatching 保护:评分弹层失败绝不能影响保存主流程。
     */
    fun maybePromptOnSuccess(activity: ComponentActivity) {
        activity.lifecycleScope.launch {
            runCatching {
                var settingsManager: SettingsManager? = null
                activity.entryPoint<SettingsStateEntryPoint> {
                    settingsManager = this.settingsManager
                }
                val settings = settingsManager ?: return@runCatching

                settings.registerSuccessfulSave()

                if (!promptInFlight.compareAndSet(false, true)) return@runCatching
                try {
                    if (settings.getSuccessfulSaveCount() < PROMPT_AFTER_SAVES) return@runCatching
                    if (settings.getInAppReviewPromptCount() >= MAX_PROMPTS) return@runCatching

                    val lastPromptAt = settings.getInAppReviewLastPromptAt()
                    if (lastPromptAt > 0 &&
                        System.currentTimeMillis() - lastPromptAt < PROMPT_COOLDOWN_MS
                    ) return@runCatching

                    delay(PROMPT_DELAY_MS)
                    if (!activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        return@runCatching
                    }

                    val manager = ReviewManagerFactory.create(activity)
                    val task = suspendCancellableCoroutine { continuation ->
                        manager.requestReviewFlow().addOnCompleteListener {
                            continuation.resume(it)
                        }
                    }
                    if (task.isSuccessful) {
                        // request 成功即记为一次尝试:配额耗尽时 launch 静默不弹且无回调,无法区分
                        settings.registerInAppReviewPrompted()
                        manager.launchReviewFlow(activity, task.result)
                    } else {
                        // 失败(无 Play Store/侧载/Play 服务异常)也进入冷却并重攒保存次数,
                        // 否则每次保存都会重试一次注定失败的 Play IPC
                        settings.registerInAppReviewFailedAttempt()
                    }
                } finally {
                    promptInFlight.set(false)
                }
            }.onFailure {
                if (it is CancellationException) throw it
                it.makeLog("InAppReviewPrompt")
            }
        }
    }
}

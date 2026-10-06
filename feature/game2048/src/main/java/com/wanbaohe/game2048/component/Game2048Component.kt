package com.wanbaohe.game2048.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.shifenmiao.base.audio.NetworkAudioPlayer
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.wanbaohe.game2048.data.Game2048Storage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 2048 游戏业务逻辑组件
 *
 * 职责：
 * 1. 维护 [Game2048UiState] 并通过 [uiState] 暴露给 Compose
 * 2. 处理滑动方向 → 调用 [GameEngine] → 更新状态
 * 3. 持久化最高分与棋盘快照到 MMKV
 * 4. 提供新游戏、继续游戏等交互方法
 * 5. 音效与 BGM 播放（统一走 core/base 的 [NetworkAudioPlayer]，与扫雷等游戏一致）
 */
class Game2048Component @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    dispatchersHolder: DispatchersHolder,
    private val audioPlayer: NetworkAudioPlayer,
) : BaseComponent(dispatchersHolder, componentContext) {

    companion object {
        /**
         * 音效托管在 R2(bucket onebox-images 的 audio/game2048/ 路径),
         * 国内海外同地址。合成脚本与源文件见 onebox-doc/audio/game2048/。
         */
        private const val SOUND_BASE = "https://images.oneboxable.com/audio/game2048"
        private const val SOUND_BGM = "$SOUND_BASE/bgm.ogg"
        private const val SOUND_MOVE = "$SOUND_BASE/move.ogg"
        private const val SOUND_MERGE = "$SOUND_BASE/merge.ogg"
        private const val SOUND_INVALID = "$SOUND_BASE/invalid.ogg"
        private const val SOUND_NEW_GAME = "$SOUND_BASE/new_game.ogg"
        private const val SOUND_WIN = "$SOUND_BASE/win.ogg"
        private const val SOUND_GAME_OVER = "$SOUND_BASE/game_over.ogg"

        private val ALL_SOUNDS = listOf(
            SOUND_BGM, SOUND_MOVE, SOUND_MERGE, SOUND_INVALID,
            SOUND_NEW_GAME, SOUND_WIN, SOUND_GAME_OVER,
        )
    }

    private val _uiState = MutableStateFlow(loadOrNewGame())
    val uiState = _uiState.asStateFlow()

    init {
        // 后台预热音效(下载到本地缓存), 首次滑动时基本已就绪, 不会有网络延迟
        componentScope.launch {
            ALL_SOUNDS.forEach { url -> audioPlayer.warmUp(url) }
        }
        // 进页即开播 BGM(思考型游戏, 安静循环)
        if (_uiState.value.soundEnabled) {
            playSound(SOUND_BGM, isBackground = true)
        }
        componentContext.lifecycle.doOnDestroy {
            audioPlayer.stopBackground()
            audioPlayer.stopEffect()
        }
    }

    // ─── 公开交互接口 ─────────────────────────────────────────────────────────

    /**
     * 向指定方向移动
     * - 无效移动（棋盘不变）不更新棋盘，播放无效音并驱动棋盘抖动反馈
     * - 每次有效移动后自动持久化快照
     */
    fun move(direction: Direction) {
        val current = _uiState.value
        if (current.isGameOver) return

        val (newGrid, addedScore, moved) = GameEngine.move(current.grid, direction)
        if (!moved) {
            playSound(SOUND_INVALID)
            _uiState.update { it.copy(invalidMoveNonce = it.invalidMoveNonce + 1) }
            return
        }

        val newScore = current.score + addedScore
        val newBest = maxOf(newScore, current.bestScore)
        val gameOver = GameEngine.isGameOver(newGrid)
        val won = GameEngine.hasWon(newGrid)

        // 更新最高分
        if (newBest > current.bestScore) {
            Game2048Storage.saveBestScore(newBest)
        }
        // 持久化当前棋盘
        Game2048Storage.saveBoard(newGrid, newScore)

        _uiState.update {
            it.copy(
                grid = newGrid,
                score = newScore,
                bestScore = newBest,
                isGameOver = gameOver,
                isWon = won,
            )
        }

        // 音效: 合并优先于普通移动; 胜负只在状态首次翻转时播一次
        when {
            won && !current.isWon -> playSound(SOUND_WIN)
            gameOver && !current.isGameOver -> playSound(SOUND_GAME_OVER)
            addedScore > 0 -> playSound(SOUND_MERGE)
            else -> playSound(SOUND_MOVE)
        }
    }

    /** 开始新游戏：重置棋盘和分数 */
    fun newGame() {
        Game2048Storage.clearBoard()
        val board = GameEngine.newBoard()
        val bestScore = Game2048Storage.loadBestScore()
        Game2048Storage.saveBoard(board, 0)

        _uiState.value = Game2048UiState(
            grid = board,
            score = 0,
            bestScore = bestScore,
            isGameOver = false,
            isWon = false,
            hasShownWinDialog = false,
            soundEnabled = _uiState.value.soundEnabled,
        )
        playSound(SOUND_NEW_GAME)
        // 开新局时 BGM 重来一轮(同 URL 循环播放期间不会重启, 先停再起)
        if (_uiState.value.soundEnabled) {
            audioPlayer.stopBackground()
            playSound(SOUND_BGM, isBackground = true)
        }
    }

    /** 标记胜利弹窗已展示，继续游戏 */
    fun dismissWinDialog() {
        _uiState.update { it.copy(hasShownWinDialog = true) }
    }

    /** 总音量开关: 关掉时同时停掉 BGM 与音效 */
    fun toggleSound() {
        val enabled = !_uiState.value.soundEnabled
        _uiState.update { it.copy(soundEnabled = enabled) }
        if (enabled) {
            playSound(SOUND_BGM, isBackground = true)
        } else {
            audioPlayer.stopBackground()
            audioPlayer.stopEffect()
        }
    }

    // ─── 音效 ────────────────────────────────────────────────────────────────

    /**
     * 播放音效。短音效用 [NetworkAudioPlayer.playEffect]（同 URL 已缓存，秒开）；
     * BGM 用 [NetworkAudioPlayer.playBackground]（循环，同 URL 播放中不重启）。
     * 播放失败静默吞掉（网络问题不该影响游戏本身）。
     */
    private fun playSound(url: String, isBackground: Boolean = false) {
        if (!_uiState.value.soundEnabled) return
        componentScope.launch {
            runCatching {
                if (isBackground) audioPlayer.playBackground(url) else audioPlayer.playEffect(url)
            }
        }
    }

    // ─── 私有方法 ──────────────────────────────────────────────────────────────

    /** 加载存档或创建新游戏 */
    private fun loadOrNewGame(): Game2048UiState {
        val bestScore = Game2048Storage.loadBestScore()
        val savedBoard = Game2048Storage.loadBoard()
        return if (savedBoard != null) {
            val savedScore = Game2048Storage.loadCurrentScore()
            Game2048UiState(
                grid = savedBoard,
                score = savedScore,
                bestScore = bestScore,
                isGameOver = GameEngine.isGameOver(savedBoard),
                isWon = GameEngine.hasWon(savedBoard),
                hasShownWinDialog = GameEngine.hasWon(savedBoard), // 恢复时不再弹窗
            )
        } else {
            val board = GameEngine.newBoard()
            Game2048Storage.saveBoard(board, 0)
            Game2048UiState(
                grid = board,
                score = 0,
                bestScore = bestScore,
            )
        }
    }

    @AssistedFactory
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit
        ): Game2048Component
    }
}


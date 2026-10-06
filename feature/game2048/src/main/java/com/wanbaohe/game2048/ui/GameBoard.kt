package com.wanbaohe.game2048.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.glass.GlassSurface
import com.t8rin.imagetoolbox.core.ui.widget.glass.GlassStyle
import com.wanbaohe.game2048.component.Direction
import kotlin.math.abs
import kotlin.math.roundToInt

/** 最小滑动距离(dp)，低于此阈值不触发移动。按 dp 取值，不同密度屏手感一致 */
private val MIN_SWIPE_DISTANCE_DP = 24.dp

/**
 * 4×4 游戏棋盘
 *
 * 功能：
 * 1. 以正方形容器渲染 4×4 方块网格
 * 2. 检测上下左右滑动手势，**拖动中一越过阈值就触发**，不等松手，手感更跟手；
 *    一次拖动只触发一个方向，松手后重置
 * 3. 无效滑动（棋盘无变化）时棋盘左右抖动两下，给出"动不了"的触觉化反馈
 * 4. 外层使用 [GlassSurface] 容器，支持毛玻璃模式
 *
 * @param grid             4×4 棋盘数据，0 表示空格
 * @param invalidMoveNonce 无效滑动计数，值变化时播放抖动动画
 * @param onSwipe          滑动方向回调
 */
@Composable
fun GameBoard(
    grid: List<List<Int>>,
    invalidMoveNonce: Int,
    onSwipe: (Direction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val minSwipeDistancePx = with(LocalDensity.current) { MIN_SWIPE_DISTANCE_DP.toPx() }

    // 记录拖动偏移量；hasTriggered 保证一次拖动只触发一个方向
    var dragX by remember { mutableFloatStateOf(0f) }
    var dragY by remember { mutableFloatStateOf(0f) }
    var hasTriggered by remember { mutableStateOf(false) }

    // ── 无效滑动抖动动画：nonce 变化时横向抖两下 ──
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(invalidMoveNonce) {
        if (invalidMoveNonce <= 0) return@LaunchedEffect
        repeat(2) {
            shakeOffset.animateTo(10f, tween(40))
            shakeOffset.animateTo(-10f, tween(40))
        }
        shakeOffset.animateTo(0f, tween(40))
    }

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .pointerInput(minSwipeDistancePx) {
                detectDragGestures(
                    onDragStart = {
                        dragX = 0f
                        dragY = 0f
                        hasTriggered = false
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (hasTriggered) return@detectDragGestures
                        dragX += dragAmount.x
                        dragY += dragAmount.y
                        val absX = abs(dragX)
                        val absY = abs(dragY)
                        // 拖动中一越过阈值立即触发，不等松手
                        if (absX > minSwipeDistancePx || absY > minSwipeDistancePx) {
                            val direction = if (absX > absY) {
                                if (dragX > 0) Direction.Right else Direction.Left
                            } else {
                                if (dragY > 0) Direction.Down else Direction.Up
                            }
                            hasTriggered = true
                            onSwipe(direction)
                        }
                    },
                    onDragEnd = {
                        dragX = 0f
                        dragY = 0f
                        hasTriggered = false
                    },
                    onDragCancel = {
                        dragX = 0f
                        dragY = 0f
                        hasTriggered = false
                    }
                )
            },
        style = GlassStyle.Medium,
        shape = RoundedCornerShape(20.dp),
        borderWidth = 0.dp,
    ) {
        // 4×4 网格
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (row in grid.indices) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (col in grid[row].indices) {
                        Box(modifier = Modifier.weight(1f)) {
                            TileView(
                                value = grid[row][col],
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}

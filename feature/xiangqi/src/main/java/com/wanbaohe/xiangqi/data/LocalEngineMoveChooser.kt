package com.wanbaohe.xiangqi.data

import com.wanbaohe.xiangqi.application.port.outbound.MoveDecision
import com.wanbaohe.xiangqi.data.local.LocalXiangqiEngine
import com.wanbaohe.xiangqi.domain.model.BoardState
import com.wanbaohe.xiangqi.domain.model.XiangqiMove
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户主动选择「本地引擎」时的主路径走棋。
 *
 * 与 [PikafishMoveChooser] 的云端失败兜底不同：这里是用户点名要用的引擎，
 * 出招即正常着法（`fallbackUsed = false`），不弹「引擎不可用」。
 * 引擎异常时才落到 [HeuristicMoveFallback] 作最后保命。
 */
@Singleton
class LocalEngineMoveChooser @Inject constructor(
    private val localEngine: LocalXiangqiEngine,
) {

    suspend fun choose(
        boardState: BoardState,
        fen: String,
        legalMoves: List<XiangqiMove>,
    ): MoveDecision? {
        if (legalMoves.isEmpty()) return null

        val local = localEngine.bestMove(fen, legalMoves.map { it.notationUcci })
            ?: return HeuristicMoveFallback.decision(boardState, legalMoves)

        val matched = legalMoves.firstOrNull {
            it.notationUcci.trim().equals(local.ucci.trim(), ignoreCase = true)
        } ?: return HeuristicMoveFallback.decision(boardState, legalMoves)

        val score = local.scoreCp?.let { "cp=$it" } ?: "score=?"
        return MoveDecision(
            move = matched,
            reason = "local-engine $score depth=${local.depth ?: 0}",
            rawResponse = "local-engine bestmove=${local.ucci}",
            fallbackUsed = false,
        )
    }
}

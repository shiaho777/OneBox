package com.shifenmiao.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 服务端棋类引擎走棋接口请求（go-proxy `POST /{chess|gomoku}/engine/bestmove`）。
 * 与象棋 XiangqiEngineRequest 同形态,供国际象棋(Stockfish)/五子棋(Rapfi)复用。
 *
 * movetime_ms 与 depth 二选一；都为空时服务端用默认 movetime。
 * skill: 0-20 越高越强, -1 表示不限制。
 */
@Serializable
data class BoardGameEngineRequest(
    @SerialName("fen")
    val fen: String,
    /** 服务端引擎标识（如 "stockfish"/"rapfi"），预留多引擎路由 */
    @SerialName("engine")
    val engine: String? = null,
    @SerialName("moves")
    val moves: String? = null,
    @SerialName("movetime_ms")
    val moveTimeMs: Int? = null,
    @SerialName("depth")
    val depth: Int? = null,
    @SerialName("skill")
    val skill: Int? = null,
)

@Serializable
data class BoardGameEngineResponse(
    @SerialName("bestmove")
    val bestmove: String = "",
    @SerialName("ponder")
    val ponder: String? = null,
    @SerialName("score_cp")
    val scoreCp: Int? = null,
    @SerialName("score_mate")
    val scoreMate: Int? = null,
    @SerialName("depth")
    val depth: Int? = null,
    @SerialName("pv")
    val pv: String? = null,
    @SerialName("time_ms")
    val timeMs: Long? = null,
)

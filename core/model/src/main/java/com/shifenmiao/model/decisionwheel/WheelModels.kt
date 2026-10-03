package com.shifenmiao.model.decisionwheel

/**
 * 决策转盘 Service 的读取模型。
 *
 * 由 Service 接口引用，供 AgentTool 层使用，不依赖 Compose。
 */

data class WheelDto(
    val id: String,
    val title: String,
    val options: List<WheelOptionDto>,
)

data class WheelOptionDto(
    val id: String,
    val name: String,
)

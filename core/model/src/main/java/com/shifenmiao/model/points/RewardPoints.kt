package com.shifenmiao.model.points

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class RewardPoints {
    var points: Int = 0
    var time: Long = Date().time
    var desc: String = ""
    var source: String = ""
    var bizId: String = ""
}


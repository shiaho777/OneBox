package com.t8rin.imagetoolbox.core.crash.data

/**
 * 业务异常(anomaly)的异常载体。
 *
 * 渠道崩溃统计都以"异常"为聚合单位, 而 anomaly 本身没有异常对象 —— 用这个类型承载,
 * 并把出错位置写进 message, 后台才能按 source 聚合成一条趋势,
 * 而不是散成几十条各自只出现一次的记录。
 */
class ReportedAnomaly(
    val source: String,
    val detail: String,
) : Exception("[$source] $detail")

package com.shifenmiao.model

import kotlinx.serialization.json.Json

object ModelProvider {

    /**
     * 全局共用的 kotlinx.serialization Json 实例(全项目唯一 JSON 入口)。
     * Json 不可变且线程安全,整个进程共用这一个实例。
     *
     * encodeDefaults = true 是对齐 Gson 时代的行为:Gson 序列化时总是写出所有字段
     * (仅省略 null),kotlinx 默认却会跳过等于声明默认值的字段;不打开此项会导致
     * 请求体/持久化 JSON 比历史产物少字段。
     */
    val AppJson: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        explicitNulls = false
        encodeDefaults = true
    }

}

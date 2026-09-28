package com.shifenmiao.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.shifenmiao.model.ai.AiProvider
import com.shifenmiao.model.ai.AiProviderTypeAdapter
import com.shifenmiao.model.ai.ListOrStringContent
import com.shifenmiao.model.ai.ListOrStringContentTypeAdapter
import com.shifenmiao.model.deserializer.DataValueTypeAdapter
import kotlinx.serialization.json.Json

object ModelProvider {

    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(ListOrStringContent::class.java, ListOrStringContentTypeAdapter())
            .registerTypeAdapter(AiProvider::class.java, AiProviderTypeAdapter())
            .registerTypeAdapter(DataValue::class.java, DataValueTypeAdapter())
            .create()
    }

    /**
     * 全局共用的 kotlinx.serialization Json 实例(Gson → kotlinx 迁移各阶段统一入口)。
     * Json 不可变且线程安全,整个进程共用这一个实例。
     *
     * encodeDefaults = true 是有意对齐 Gson 默认行为:Gson 序列化时总是写出所有字段
     * (仅省略 null),kotlinx 默认却会跳过等于声明默认值的字段;不打开此项会导致
     * 请求体/持久化 JSON 比 Gson 产物少字段(如 TTSSpeechRequest.response_format)。
     */
    val AppJson: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        explicitNulls = false
        encodeDefaults = true
    }

}
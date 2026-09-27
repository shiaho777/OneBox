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
     */
    val AppJson: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        explicitNulls = false
    }

}
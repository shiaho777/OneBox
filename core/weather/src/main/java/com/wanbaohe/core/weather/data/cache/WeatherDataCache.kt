package com.wanbaohe.core.weather.data.cache

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.shifenmiao.model.ModelProvider.AppJson
import com.wanbaohe.core.weather.domain.model.WeatherInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

@Serializable
data class CachedWeather(
    // 默认值兜底 Gson 时代缓存(缺 timestamp 时 Gson 给 0,按过期处理)
    val data: WeatherInfo = WeatherInfo(),
    val timestamp: Long = 0L
)

private val Context.weatherDataStore by preferencesDataStore(name = "weather_data_cache")

class WeatherDataCache(
    private val context: Context
) {
    private val validDuration = 30 * 60 * 1000L // 30 分钟有效期

    suspend fun getWeather(cityId: String): WeatherInfo? {
        val key = stringPreferencesKey(cityId)
        val json = context.weatherDataStore.data.map { preferences ->
            preferences[key]
        }.first()

        if (json == null) return null

        // 解析失败按无缓存处理(旧 Gson 格式字段名一致,正常可读)
        val cached = runCatching { AppJson.decodeFromString<CachedWeather>(json) }.getOrNull()
            ?: return null

        if (System.currentTimeMillis() - cached.timestamp > validDuration) {
            context.weatherDataStore.edit { preferences ->
                preferences.remove(key)
            }
            return null
        }
        return cached.data
    }

    suspend fun saveWeather(cityId: String, weatherInfo: WeatherInfo) {
        val key = stringPreferencesKey(cityId)
        val cached = CachedWeather(weatherInfo, System.currentTimeMillis())
        val json = AppJson.encodeToString(cached)

        context.weatherDataStore.edit { preferences ->
            preferences[key] = json
        }
    }
}

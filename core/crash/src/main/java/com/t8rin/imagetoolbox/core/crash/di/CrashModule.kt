/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.core.crash.di

import com.t8rin.imagetoolbox.core.crash.data.AnalyticsManagerImpl
import com.t8rin.imagetoolbox.core.crash.data.ErrorReporterImpl
import com.t8rin.imagetoolbox.core.domain.remote.AnalyticsManager
import com.t8rin.imagetoolbox.core.domain.remote.ErrorReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CrashModule {

    @Provides
    @Singleton
    fun analyticsManager(): AnalyticsManager = AnalyticsManagerImpl

    /**
     * 统一错误上报入口: 页面注入 [ErrorReporter] 就能上报, 背后落到当前渠道的
     * 原生崩溃/异常统计(google = Crashlytics; 国内与 foss 接入 SDK 前是 no-op)。
     */
    @Provides
    @Singleton
    fun errorReporter(impl: ErrorReporterImpl): ErrorReporter = impl

}

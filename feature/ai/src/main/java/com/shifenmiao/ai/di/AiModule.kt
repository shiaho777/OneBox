package com.shifenmiao.ai.di

import com.t8rin.imagetoolbox.core.data.workspace.AppWorkspaceResolver
import com.shifenmiao.ai.prompt.AndroidEnvironmentContextProvider
import com.shifenmiao.ai.prompt.EnvironmentContextProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * feature/ai 模块的 Hilt 依赖提供。
 */
@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideEnvironmentContextProvider(
        appWorkspaceResolver: AppWorkspaceResolver,
    ): EnvironmentContextProvider = AndroidEnvironmentContextProvider(appWorkspaceResolver)
}

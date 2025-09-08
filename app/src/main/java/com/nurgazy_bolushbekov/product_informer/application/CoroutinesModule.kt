package com.nurgazy_bolushbekov.product_informer.application

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {
    @Provides
    @Singleton
    fun providesApplicationScope(): CoroutineScope {
        // SupervisorJob позволяет дочерним корутинам не отменять друг друга при ошибке
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
package com.nurgazy_bolushbekov.product_informer.application

import android.content.Context
import com.nurgazy_bolushbekov.product_informer.product.image.ImageRepository
import com.nurgazy_bolushbekov.product_informer.product.image.ImageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageRepositoryModule {

    @Provides
    @Singleton
    fun provideImageRepository(
        @ApplicationContext context: Context
    ): ImageRepositoryImpl {
        return ImageRepositoryImpl(context)
    }
}
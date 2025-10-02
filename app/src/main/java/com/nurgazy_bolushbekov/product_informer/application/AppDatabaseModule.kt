package com.nurgazy_bolushbekov.product_informer.application

import android.content.Context
import androidx.room.Room
import com.nurgazy_bolushbekov.product_informer.barcode_collection.dao.BarcodeDocDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "product_informer_db"
        ).build()
    }

    @Provides
    fun provideBarcodeDocDao(database: AppDatabase): BarcodeDocDao {
        return database.barcodeDocDao()
    }
}
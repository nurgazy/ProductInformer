package com.nurgazy_bolushbekov.product_informer.application

import android.content.Context
import androidx.room.Room
import com.nurgazy_bolushbekov.product_informer.barcode_collection.dao.BarcodeDocDao
import com.nurgazy_bolushbekov.product_informer.product.dao.ProductDao
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
        )
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideBarcodeDocDao(database: AppDatabase): BarcodeDocDao {
        return database.barcodeDocDao()
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }
}
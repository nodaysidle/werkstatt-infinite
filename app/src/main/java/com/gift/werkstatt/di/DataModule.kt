package com.gift.werkstatt.di

import android.content.Context
import androidx.room.Room
import com.gift.werkstatt.data.local.dao.CanvasDao
import com.gift.werkstatt.data.local.db.WerkstattDatabase
import com.gift.werkstatt.data.local.mapper.CanvasEntityMapper
import com.gift.werkstatt.data.repository.CanvasRepositoryImpl
import com.gift.werkstatt.data.serialization.CanvasJsonCodec
import com.gift.werkstatt.domain.canvas.repository.CanvasRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCanvasRepository(impl: CanvasRepositoryImpl): CanvasRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WerkstattDatabase {
        return Room.databaseBuilder(
            context,
            WerkstattDatabase::class.java,
            "werkstatt_database"
        )
            .addMigrations(WerkstattDatabase.MIGRATION_1_2, WerkstattDatabase.MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideCanvasDao(database: WerkstattDatabase): CanvasDao = database.canvasDao()

    @Provides
    fun provideCanvasEntityMapper(codec: CanvasJsonCodec): CanvasEntityMapper {
        return CanvasEntityMapper(codec)
    }
}

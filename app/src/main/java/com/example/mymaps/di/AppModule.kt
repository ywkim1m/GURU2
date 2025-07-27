package com.example.mymaps.di

import android.content.Context
import androidx.room.Room
import com.example.mymaps.data.AppDatabase
import com.example.mymaps.model.SpotDao
import com.example.mymaps.repository.SpotRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // RoomDatabase 인스턴스 주입
    @Provides
    @Singleton
    fun provideDatabase(appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    // SpotDao 주입
    @Provides
    @Singleton
    fun provideSpotDao(database: AppDatabase): SpotDao {
        return database.spotDao()
    }

    // Repository도 주입
    @Provides
    @Singleton
    fun provideSpotRepository(spotDao: SpotDao): SpotRepository {
         return SpotRepository(spotDao)
     }
}

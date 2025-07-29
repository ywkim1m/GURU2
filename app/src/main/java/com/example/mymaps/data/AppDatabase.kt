package com.example.mymaps.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mymaps.model.Converters
import com.example.mymaps.model.MissionDao
import com.example.mymaps.model.MissionEntity
import com.example.mymaps.model.SpotDao
import com.example.mymaps.model.SpotEntity

@Database(entities = [SpotEntity::class, MissionEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun spotDao(): SpotDao
    abstract fun missionDao(): MissionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // 이미 있으면 반환, 없으면 새로 만듦 (싱글톤 보장)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
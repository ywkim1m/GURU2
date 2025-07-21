package com.example.mymaps.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mymaps.model.Converters
import com.example.mymaps.model.SpotDao
import com.example.mymaps.model.SpotEntity

@Database(entities = [SpotEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun spotDao(): SpotDao
}
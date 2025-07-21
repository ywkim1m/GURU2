package com.example.mymaps.model

import androidx.room.TypeConverter

class Converters {
    // SpotCategory -> String 으로 변환
    @TypeConverter
    fun fromCategory(category: SpotCategory): String {
        return category.name
    }

    // String -> SpotCategory 로 변환
    @TypeConverter
    fun toCategory(value: String): SpotCategory {
        return SpotCategory.valueOf(value)
    }
}
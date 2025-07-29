package com.example.mymaps

import android.app.Application
import com.example.mymaps.data.AppDatabase
import com.example.mymaps.repository.SpotRepository

class MyApp : Application() {
    // Room DB 인스턴스
    val database by lazy { AppDatabase.getDatabase(this) }
    // Repository 인스턴스
    val spotRepository by lazy { SpotRepository(database.spotDao()) }
}
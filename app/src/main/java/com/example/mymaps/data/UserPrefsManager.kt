package com.example.mymaps.data

import android.content.Context

class UserPrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // 유저 이름
    var userName: String?
        get() = prefs.getString("user_name", null)
        set(value) { prefs.edit().putString("user_name", value).apply() }

    // 유저 레벨
    var userLevel: Int
        get() = prefs.getInt("user_level", 1)
        set(value) { prefs.edit().putInt("user_level", value).apply() }

    // 뱃지
    var badges: Set<String>
        get() = prefs.getStringSet("badges_unlocked", emptySet()) ?: emptySet()
        set(value) { prefs.edit().putStringSet("badges_unlocked", value).apply() }

    /*// 뱃지 추가
    fun addBadge(badgeId: String) {
        badges = badges + badgeId
    }*/
}
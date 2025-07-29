package com.example.mymaps.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mymaps.model.Badge

class BadgeViewModel : ViewModel() {
    private val _badges = MutableLiveData<List<Badge>>(listOf(
        Badge("badge_1"),
        Badge("badge_2"),
        Badge("badge_3"),
        Badge("badge_4"),
        Badge("badge_5"),
        Badge("badge_6"),
        Badge("badge_7"),
        Badge("badge_8"),
        Badge("badge_9"),
        Badge("badge_10")
    ))
    val badges: LiveData<List<Badge>> = _badges
    // 뱃지 추가/획득 로직 등 추가
}
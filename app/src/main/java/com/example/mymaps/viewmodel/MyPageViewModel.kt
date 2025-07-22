package com.example.mymaps.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mymaps.data.UserPrefsManager

class MyPageViewModel(private val prefs: UserPrefsManager): ViewModel() {
    // LiveData: UI에서 observe하면 자동 업데이트
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _userLevel = MutableLiveData<Int>()
    val userLevel: LiveData<Int> get() = _userLevel

    private val _badges = MutableLiveData<Set<String>>()
    val badges: LiveData<Set<String>> get() = _badges

    fun loadUserData() {
        _userName.value = prefs.userName ?: ""
        _userLevel.value = prefs.userLevel
        _badges.value = prefs.badges
    }

    /*// 뱃지 추가
    fun addBadge(badgeId: String) {
        prefs.addBadge(badgeId)
        _badges.value = prefs.badges
    }*/
}
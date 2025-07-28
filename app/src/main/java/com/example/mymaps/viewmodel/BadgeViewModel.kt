package com.example.mymaps.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mymaps.model.Badge

class BadgeViewModel : ViewModel() {
    private val _badges = MutableLiveData<List<Badge>>(listOf(
        Badge("badge_1", "동네 초심자"),
        Badge("badge_2", "길찾기 연습생"),
        Badge("badge_3", "견습 도깨비 사냥꾼"),
        Badge("badge_4", "스팟 탐색가"),
        Badge("badge_5", "동네 정보통"),
        Badge("badge_6", "골목 달인"),
        Badge("badge_7", "핫플 개척자"),
        Badge("badge_8", "로컬 히어로"),
        Badge("badge_9", "전설의 도깨비"),
        Badge("badge_10", "동네 마스터"),
    ))
    val badges: LiveData<List<Badge>> = _badges
    // 뱃지 추가/획득 로직 등 추가
}
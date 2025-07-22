package com.example.mymaps.model

data class Badge(
    val id: String,
    val name: String,
    val images: Int
)

// 뱃지 리스트, 추후 images 수정 예시 R.drawable.badge1
val allBadges = listOf(
    Badge("badge_1", "첫 방문", 1),
    Badge("badge_2", "첫 방문", 1),
    Badge("badge_3", "첫 방문", 1),
    Badge("badge_4", "첫 방문", 1),
    Badge("badge_5", "첫 방문", 1),
    Badge("badge_6", "첫 방문", 1),
    Badge("badge_7", "첫 방문", 1),
    Badge("badge_8", "첫 방문", 1),
    Badge("badge_9", "첫 방문", 1),
    Badge("badge_10", "첫 방문", 1)
)

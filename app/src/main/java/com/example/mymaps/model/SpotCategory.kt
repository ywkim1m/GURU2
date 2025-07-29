package com.example.mymaps.model

enum class SpotCategory(val displayName: String) {
    WALKING_ROUTE("산책루트"),
    HIDDEN_GEM("숨은맛집"),
    PHOTO_SPOT("포토존"),
    SHOPPING("쇼핑"),
    PARK("공원"),
    ETC("기타");

    companion object {
        fun fromDisplayName(displayName: String): SpotCategory? =
            entries.find { it.displayName == displayName }

        fun toDisplayName(categoryEnum: SpotCategory?): String =
            categoryEnum?.displayName ?: "기타"
    }
}
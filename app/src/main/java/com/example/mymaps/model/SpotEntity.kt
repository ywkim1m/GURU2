package com.example.mymaps.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spots")
data class SpotEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,   // 장소명
    val description: String,    // 장소 설명
    val photoUri: String?,  // 장소 사진
    // enum(선택 or NONE) + string(유저가 추가한 경우)
    val categoryEnum: SpotCategory? = SpotCategory.ETC,
    val categoryName: String? = "",
    val categoryPinColorResId: Int = 0,
    val roadAddress: String?, // 도로명주소
    val latitude: Double,     // 위도
    val longitude: Double,  // 경도
    val isSaved: Boolean = false,   // 저장한 스팟
    val isLiked: Boolean = false,   // 좋아요한 스팟
    val isDisliked: Boolean = false,    // 싫어요한 스팟
    val isVisited: Boolean = false  // 방문한 스팟
)
package com.example.mymaps.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "missions",
    foreignKeys = [
        ForeignKey(
            entity = SpotEntity::class,
            parentColumns = ["id"],
            childColumns = ["spotId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["spotId"])])
data class MissionEntity(
    @PrimaryKey val missionId: String,  // 미션 아이디
    val title: String,  // 미션 이름
    val description: String?,   // 설명, null 가능
    val pointReward: Int,   // 보상 포인트
    val spotId: Int?,   // 스팟 id, 외래키
    val isCompleted: Boolean = false,    // 완료 여부
    val visitedDate: String? = null,    // 방문 인증 일자(yyyy-mm-dd), null이면 미완료
    val proofPhotoUri: String? = null   // 방문 인증 사진(Uri)
)

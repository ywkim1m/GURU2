package com.example.mymaps.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SpotDao {
    // 스팟 insert
    @Insert(onConflict = OnConflictStrategy.REPLACE) // ID 같은 데이터 덮어쓰기
    suspend fun insertSpot(spot: SpotEntity)

    // 스팟 update
    @Update
    suspend fun updateSpot(spot: SpotEntity)

    // 스팟 delete
    @Delete
    suspend fun deleteSpot(spot: SpotEntity)

    // 모든 스팟 불러오기
    @Query("SELECT * FROM spots")
    fun getAllSpots(): LiveData<List<SpotEntity>>

    // 특정 enum 카테고리의 스팟
    @Query("SELECT * FROM spots WHERE categoryEnum = :categoryEnum")
    fun getSpotsByCategoryEnum(categoryEnum: SpotCategory): LiveData<List<SpotEntity>>

    // 커스텀(직접입력) 카테고리의 스팟
    @Query("SELECT * FROM spots WHERE categoryEnum = :etcEnum AND categoryName = :categoryName")
    fun getSpotsByCustomCategory(etcEnum: SpotCategory = SpotCategory.ETC, categoryName: String): LiveData<List<SpotEntity>>

    // 저장한 스팟 불러오기
    @Query("SELECT * FROM spots WHERE isSaved = 1")
    fun getSavedSpots(): LiveData<List<SpotEntity>>

    // '좋아요'한 스팟 불러오기
    @Query("SELECT * FROM spots WHERE isLiked = 1")
    fun  getLikedSpots(): LiveData<List<SpotEntity>>

    // '싫어요'한 스팟 불러오기
    @Query("SELECT * FROM spots WHERE isDisliked = 1")
    fun  getDislikedSpots(): LiveData<List<SpotEntity>>

    // 방문한 스팟 불러오기
    @Query("SELECT * FROM spots WHERE isVisited = 1")
    fun getVisitedSpots(): LiveData<List<SpotEntity>>

    // 스팟 방문 시
    @Query("UPDATE spots SET isVisited = 1 WHERE id = :spotId")
    fun markSpotAsVisited(spotId: Int)
}
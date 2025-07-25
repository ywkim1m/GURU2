package com.example.mymaps.model

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
    suspend fun getAllSpots(): List<SpotEntity>

    // 저장한 스팟 불러오기
    @Query("SELECT * FROM spots WHERE isSaved = 1")
    suspend fun getSavedSpots(): List<SpotEntity>

    // '좋아요'한 스팟 불러오기
    @Query("SELECT * FROM spots WHERE isLiked = 1")
    suspend fun  getLikedSpots(): List<SpotEntity>

    // '싫어요'한 스팟 불러오기
    @Query("SELECT * FROM spots WHERE isDisliked = 1")
    suspend fun  getDislikedSpots(): List<SpotEntity>

    // 방문한 스팟 불러오기
    @Query("SELECT * FROM spots WHERE isVisited = 1")
    suspend fun getVisitedSpots(): List<SpotEntity>

    // 스팟 방문 시
    @Query("UPDATE spots SET isVisited = 1 WHERE id = :spotId")
    suspend fun markSpotAsVisited(spotId: Int)
}
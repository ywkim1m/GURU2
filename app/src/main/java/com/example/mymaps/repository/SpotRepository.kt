package com.example.mymaps.repository

import android.devicelock.DeviceId
import com.example.mymaps.model.SpotCategory
import com.example.mymaps.model.SpotDao
import com.example.mymaps.model.SpotEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SpotRepository(
    private val spotDao: SpotDao
) {

    // 스팟 insert
    suspend fun insertSpot(spot: SpotEntity) {
        withContext(Dispatchers.IO) { // 입출력(IO) 전용 쓰레드에서 코드 실행
            spotDao.insertSpot(spot)
        }
    }

    // 스팟 update
    suspend fun updateSpot(spot: SpotEntity) {
        withContext(Dispatchers.IO) {
            spotDao.updateSpot(spot)
        }
    }

    // 스팟 delete
    suspend fun deleteSpot(spot: SpotEntity) {
        withContext(Dispatchers.IO) {
            spotDao.deleteSpot(spot)
        }
    }

    // 모든 스팟 불러오기
    suspend fun getAllSpots(): List<SpotEntity> {
        return withContext(Dispatchers.IO) {
            spotDao.getAllSpots()
        }
    }

    // 저장한 스팟 불러오기
    suspend fun getSavedSpots(): List<SpotEntity> {
        return withContext(Dispatchers.IO) {
            spotDao.getSavedSpots()
        }
    }

    // '좋아요'한 스팟 불러오기
    suspend fun getLikedSpots(): List<SpotEntity> {
        return withContext(Dispatchers.IO) {
            spotDao.getLikedSpots()
        }
    }

    // '싫어요'한 스팟 불러오기
    suspend fun getDislikedSpots(): List<SpotEntity> {
        return withContext(Dispatchers.IO) {
            spotDao.getDislikedSpots()
        }
    }

    // 방문한 스팟 불러오기
    suspend fun getVisitedSpots(): List<SpotEntity> {
        return withContext(Dispatchers.IO) {
            spotDao.getVisitedSpots()
        }
    }

    // 특정 Enum 카테고리별 조회
    suspend fun getSpotsByCategoryEnum(categoryEnum: SpotCategory): List<SpotEntity> = withContext(Dispatchers.IO) {
        spotDao.getSpotsByCategoryEnum(categoryEnum)
    }

    // 커스텀 카테고리별 조회
    suspend fun getSpotsByCustomCategory(categoryName: String): List<SpotEntity> = withContext(Dispatchers.IO) {
        spotDao.getSpotsByCustomCategory(SpotCategory.ETC, categoryName)
    }
}
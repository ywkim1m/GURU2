package com.example.mymaps.repository

import com.example.mymaps.model.MissionDao
import com.example.mymaps.model.MissionEntity
import com.example.mymaps.model.MissionWithSpot
import com.example.mymaps.model.SpotDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MissionRepository(private val missionDao: MissionDao,
                        private val spotDao: SpotDao
) {

    //
    suspend fun getMissionWithSpot(missionId: String): MissionWithSpot? {
        return missionDao.getMissionWithSpot(missionId)
    }

    // 미션 insert
    suspend fun insertMission(mission: MissionEntity) {
        withContext(Dispatchers.IO) {
            missionDao.insertMission(mission)
        }
    }

    // 미션 update
    suspend fun updateMission(mission: MissionEntity) {
        withContext(Dispatchers.IO) {
            missionDao.updateMission(mission)
        }
    }

    // 미션 delete
    suspend fun deleteMission(mission: MissionEntity) {
        withContext(Dispatchers.IO) {
            missionDao.deleteMission(mission)
        }
    }

    // 모든 미션 불러오기
    suspend fun getAllMissions(): List<MissionEntity> {
        return withContext(Dispatchers.IO) {
            missionDao.getAllMissions()
        }
    }

    // 달성 미션 불러오기
    suspend fun getCompletedMissions(): List<MissionEntity> {
        return withContext(Dispatchers.IO) {
            missionDao.getCompletedMissions()
        }
    }

    // 미달성 미션 불러오기
    suspend fun getUncompletedMissions(): List<MissionEntity> {
        return withContext(Dispatchers.IO) {
            missionDao.getUncompletedMissions()
        }
    }

    // 방문 인증 처리 함수
    suspend fun completeMissionWithProof(
        missionId: String,
        visitedDate: String,
        proofPhotoUri: String?
    ) {
        withContext(Dispatchers.IO) {
            missionDao.completeMissionWithProof(missionId, visitedDate, proofPhotoUri)
        }
    }

    // 미션 완료 시 spot visited 처리
    suspend fun completeMissionAndMarkSpotVisited(
        missionId: String,
        visitedDate: String,
        proofPhotoUri: String?,
        spotId: Int?    // Spot의 PK, null 방지
    ) {
        withContext(Dispatchers.IO) {
            missionDao.completeMissionWithProof(missionId, visitedDate, proofPhotoUri)
            spotId?.let { spotDao.markSpotAsVisited(it) }
        }
    }
}
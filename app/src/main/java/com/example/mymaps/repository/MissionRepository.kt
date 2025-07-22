package com.example.mymaps.repository

import com.example.mymaps.model.MissionDao
import com.example.mymaps.model.MissionEntity
import com.example.mymaps.model.MissionWithSpot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MissionRepository(private val missionDao: MissionDao) {

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
}
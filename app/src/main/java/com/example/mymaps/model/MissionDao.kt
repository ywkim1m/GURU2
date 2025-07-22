package com.example.mymaps.model

import androidx.room.*

@Dao
interface MissionDao {
    @Transaction
    @Query("SELECT * FROM missions WHERE missionId = :missionId")
    suspend fun getMissionWithSpot(missionId: String): MissionWithSpot?

    // 미션 insert
    @Insert(onConflict = OnConflictStrategy.REPLACE) // ID 같은 데이터 덮어쓰기
    suspend fun insertMission(mission: MissionEntity)

    // 미션 update
    @Update
    suspend fun updateMission(mission: MissionEntity)

    // 미션 delete
    @Delete
    suspend fun deleteMission(mission: MissionEntity)

    // 모든 미션 불러오기
    @Query("SELECT * FROM missions")
    suspend fun getAllMissions(): List<MissionEntity>

    // 달성 미션 불러오기
    @Query("SELECT * FROM missions WHERE isCompleted = 1")
    suspend fun getCompletedMissions(): List<MissionEntity>

    // 미달성 미션 불러오기
    @Query("SELECT * FROM missions WHERE isCompleted = 0")
    suspend fun  getUncompletedMissions(): List<MissionEntity>

}
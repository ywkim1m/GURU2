package com.example.mymaps.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymaps.model.MissionEntity
import com.example.mymaps.repository.MissionRepository
import kotlinx.coroutines.launch

class MissionViewModel(private val repository: MissionRepository): ViewModel() {
    // XML UI에서 LiveData를 observe하면 값이 바뀔 때 자동으로 UI가 업데이트
    private val _allMissions = MutableLiveData<List<MissionEntity>>() // 내부 상태(수정 가능)
    val allMissions: LiveData<List<MissionEntity>> = _allMissions // 외부 공개(읽기 전용)

    // 모든 미션 불러오기
    fun loadMissions() {
        viewModelScope.launch {     // 백그라운드 작업 시작(코루틴)
            _allMissions.value = repository.getAllMissions()    // DB에서 모든 미션을 불러옴
        }
    }

    // 미션 insert
    fun insertMission(mission: MissionEntity) {
        viewModelScope.launch {
            repository.insertMission(mission)   // 새로운 미션 DB에 저장
            loadMissions()  // 리스트 새로고침
        }
    }

    // 미션 update
    fun updateMission(mission: MissionEntity) {
        viewModelScope.launch {
            repository.updateMission(mission)
            loadMissions()
        }
    }

    // 미션 delete
    fun deleteMission(mission: MissionEntity) {
        viewModelScope.launch {
            repository.deleteMission(mission)
            loadMissions()
        }
    }

    // 달성 미션 불러오기
    fun loadCompletedMissions() {
        viewModelScope.launch {
            _allMissions.value = repository.getCompletedMissions()
        }
    }

    // 미달성 미션 불러오기
    fun loadUncompletedMissions() {
        viewModelScope.launch {
            _allMissions.value = repository.getUncompletedMissions()
        }
    }
}
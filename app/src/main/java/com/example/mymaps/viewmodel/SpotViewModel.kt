package com.example.mymaps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymaps.model.SpotEntity
import com.example.mymaps.repository.SpotRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.mymaps.model.SpotCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SpotViewModel @Inject constructor(
    private val repository: SpotRepository
): ViewModel() {
    // XML UI에서 LiveData를 observe하면 값이 바뀔 때 자동으로 UI가 업데이트
    private val _allSpots = MutableLiveData<List<SpotEntity>>() // 내부 상태(수정 가능)
    val allSpots: LiveData<List<SpotEntity>> = _allSpots // 외부 공개(읽기 전용)

    // 모든 스팟 불러오기
    fun loadSpots() {
        viewModelScope.launch { // 백그라운드 작업 시작(코루틴)
            _allSpots.value = repository.getAllSpots() // DB에서 모든 스팟을 불러옴
        }
    }

    // 스팟 insert
    fun insertSpot(spot: SpotEntity) {
        viewModelScope.launch {
            repository.insertSpot(spot) // 새로운 스팟 DB에 저장
            loadSpots() // 리스트 새로고침
        }
    }

    // 스팟 update
    fun updateSpot(spot: SpotEntity) {
        viewModelScope.launch {
            repository.updateSpot(spot)
            loadSpots()
        }
    }

    // 스팟 delete
    fun deleteSpot(spot: SpotEntity) {
        viewModelScope.launch {
            repository.deleteSpot(spot)
            loadSpots()
        }
    }

    // 저장된 스팟 불러오기
    fun loadSavedSpots() {
        viewModelScope.launch {
            _allSpots.value = repository.getSavedSpots()
        }
    }

    // '좋아요'한 스팟 불러오기
    fun loadLikedSpots() {
        viewModelScope.launch {
            _allSpots.value = repository.getLikedSpots()
        }
    }

    // '싫어요'한 스팟 불러오기
    fun loadDislikedSpots() {
        viewModelScope.launch {
            _allSpots.value = repository.getDislikedSpots()
        }
    }

    // 방문한 스팟 불러오기
    fun loadVisitedSpots() {
        viewModelScope.launch {
            _allSpots.value = repository.getVisitedSpots()
        }
    }

    // 특정 Enum 카테고리별 조회
    fun loadSpotsByEnum(categoryEnum: SpotCategory) {
        viewModelScope.launch {
            _allSpots.value = repository.getSpotsByCategoryEnum(categoryEnum)
        }
    }

    // 커스텀 카테고리별 조회
    fun loadSpotsByCustomCategory(categoryName: String) {
        viewModelScope.launch {
            _allSpots.value = repository.getSpotsByCustomCategory(categoryName)
        }
    }
}
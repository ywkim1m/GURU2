package com.example.mymaps.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.MyApp
import com.example.mymaps.R
import com.example.mymaps.adapter.BadgeAdapter
import com.example.mymaps.data.UserPrefsManager
import com.example.mymaps.model.Badge
import com.example.mymaps.model.SpotEntity
import com.example.mymaps.viewmodel.BadgeViewModel
import com.example.mymaps.viewmodel.MyPageViewModel
import com.example.mymaps.viewmodel.SpotViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*
import com.example.mymaps.viewmodel.SpotViewModelFactory

class MyPageActivity : AppCompatActivity(), OnMapReadyCallback {
    private val spotViewModel: SpotViewModel by viewModels {
        SpotViewModelFactory((application as MyApp).spotRepository)
    }
    private val myPageViewModel: MyPageViewModel by viewModels()

    private lateinit var badgeAdapter: BadgeAdapter

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var btnMyLocationCustom: ImageButton
    // private lateinit var tvNickname: TextView
//    private lateinit var tvLevelText: TextView

    // 핀 필터링 TextViews
    private lateinit var tvFilterRegistered: TextView
    private lateinit var tvFilterLiked: TextView
    private lateinit var tvFilterDisliked: TextView
    private lateinit var tvFilterVisited: TextView

    private var registeredSpots: List<SpotEntity> = emptyList()
    private var likedSpots: List<SpotEntity> = emptyList()
    private var dislikedSpots: List<SpotEntity> = emptyList()
    private var visitedSpots: List<SpotEntity> = emptyList()

    // 네비게이션 바 ImageButtons
    // private lateinit var btnHome: ImageButton
    // private lateinit var btnAddSpotNav: ImageButton
    // private lateinit var btnMyPageNav: ImageButton

    // 공릉동 도깨비시장 좌표 (초기 지도 위치)
    private val GONGNEUNG_DOKKAEBI_MARKET = LatLng(37.6256, 127.0782)


    private var currentMarkers = mutableListOf<Marker>()
    private var currentFilterType: String = "등록한 스팟" // 초기 필터 타입

    // 위치 권한 요청
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                moveToMyLocation()
            } else {
                Toast.makeText(this, "위치 권한이 거부되어 내 위치를 표시할 수 없습니다.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        // 유저 데이터
        val userPrefs = UserPrefsManager(this)
        findViewById<TextView>(R.id.tvNickname).text = userPrefs.userName ?: "익명"

        // 레벨
        val level = userPrefs.userLevel
        findViewById<TextView>(R.id.tvLevelText).text = "LV.$level ${getLevelName(level)}"

        // 뱃지
        val badgeList = userPrefs.badges.toMutableList()

        // 각 레벨별 뱃지를 자동으로 추가
        for (level in 1..userPrefs.userLevel) {
            val badgeId = "badge_$level"
            if (!badgeList.contains(badgeId)) {
                badgeList.add(badgeId)
            }
        }

        // 저장(중복방지)
        userPrefs.badges = badgeList.toSet()

        // 뱃지 리스트를 레벨 기준 내림차순 정렬
        val sortedBadgeList = badgeList.sortedByDescending {
            // badge_3 → 3으로 변환해서 정렬
            it.removePrefix("badge_").toIntOrNull() ?: 0
        }

        // RecyclerView 어댑터에 뱃지 적용
        badgeAdapter = BadgeAdapter(sortedBadgeList.map { Badge(it) })
        val rvBadges = findViewById<RecyclerView>(R.id.rvBadges)
        rvBadges.adapter = badgeAdapter
        rvBadges.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // View 초기화
        btnMyLocationCustom = findViewById(R.id.btnMyLocationCustom)

        tvFilterRegistered = findViewById(R.id.tvFilterRegistered)
        tvFilterLiked = findViewById(R.id.tvFilterLiked)
        tvFilterDisliked = findViewById(R.id.tvFilterDisliked)
        tvFilterVisited = findViewById(R.id.tvFilterVisited)

        // SpotViewModel에서 LiveData로 SpotEntity 전체 불러오기
        spotViewModel.allSpots.observe(this) { spotList ->
            updateSpotsByType(spotList)
            displayMarkersForCurrentFilter()
        }

        // Map 초기화
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 필터 버튼 클릭 리스너 설정
        setupFilterButtons()

        // 하단 네비게이션 바 버튼
        val barOne = findViewById<ImageView>(R.id.bar_one)
        val barTwo = findViewById<ImageView>(R.id.bar_two)
        val barThree = findViewById<ImageView>(R.id.bar_three)

        barOne.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        // 스팟 등록 화면으로 전환
        barTwo.setOnClickListener {
            val intent = Intent(this, SpotRegisterActivity::class.java)
            startActivity(intent)
        }

        // 마이페이지로 전환
        barThree.setOnClickListener {
            // 현재 페이지, 동작x
        }
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GONGNEUNG_DOKKAEBI_MARKET, 16f))

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.setPadding(0, 0, 0, 0)

        // 커스텀 '내 위치' 버튼 클릭 리스너
        btnMyLocationCustom.setOnClickListener {
            checkLocationPermissionForCustomButton()
        }

        // 마커 클릭 리스너 (정보창 표시)
        googleMap.setOnMarkerClickListener { marker ->
            // 방문한 스팟 마커인 경우, 커스텀 정보창 표시
            val spot = getSpotFromMarker(marker)
            if (spot != null) {
                googleMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this, spot))
            }
            marker.showInfoWindow()
            true // true 반환 시 기본 동작 (카메라 이동) 방지
        }

        // 초기 마커 표시
        updateFilterSelection(tvFilterRegistered) // 앱 실행 시 '등록한 스팟'이 기본으로 선택
    }

    private fun setupFilterButtons() {
        val filterButtons = listOf(tvFilterRegistered, tvFilterLiked, tvFilterDisliked, tvFilterVisited)

        filterButtons.forEach { button ->
            button.setOnClickListener {
                updateFilterSelection(button)
            }
        }
    }

    private fun updateFilterSelection(selectedButton: TextView) {
        // 여기를 수정해야 함
        val filterButtons = listOf(tvFilterRegistered, tvFilterLiked, tvFilterDisliked, tvFilterVisited)

        filterButtons.forEach { button ->
            button.isSelected = (button == selectedButton)
        }

        currentFilterType = selectedButton.text.toString()
        displayMarkersForCurrentFilter()
    }

    private fun displayMarkersForCurrentFilter() {
        currentMarkers.forEach { it.remove() }
        currentMarkers.clear()

        val spotsToDisplay = when (currentFilterType) {
            "등록한 스팟" -> registeredSpots
            "좋아요" -> likedSpots
            "싫어요" -> dislikedSpots
            "방문한 스팟" -> visitedSpots
            else -> emptyList()
        }

        spotsToDisplay.forEach { spot ->
            val markerOptions = MarkerOptions()
                .position(LatLng(spot.latitude, spot.longitude))
                .title(spot.name)
                .snippet(spot.categoryName ?: spot.categoryEnum?.displayName ?: "") // Enum or Custom String
                .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColorForFilter(currentFilterType)))

            val marker = googleMap.addMarker(markerOptions)
            marker?.tag = spot // 마커에 SpotEntity 태그
            marker?.let { currentMarkers.add(it) }
        }

        // 필터링된 마커가 있을 경우, 해당 마커들을 포함하는 뷰로 카메라 이동 (선택 사항)
        if (currentMarkers.isNotEmpty()) {
            val builder = LatLngBounds.builder()
            currentMarkers.forEach { builder.include(it.position) }
            val bounds = builder.build()
            val padding = 100
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        } else {
            // 마커가 없으면 초기 위치로 돌아가기 (선택 사항)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(GONGNEUNG_DOKKAEBI_MARKET, 16f))
        }
    }

    // 필터 타입에 따른 마커 색상 변경
    private fun getMarkerColorForFilter(filterType: String): Float {
        return when (filterType) {
            "등록한 스팟" -> BitmapDescriptorFactory.HUE_RED
            "좋아요" -> BitmapDescriptorFactory.HUE_GREEN
            "싫어요" -> BitmapDescriptorFactory.HUE_YELLOW
            "방문한 스팟" -> BitmapDescriptorFactory.HUE_AZURE
            else -> BitmapDescriptorFactory.HUE_ORANGE
        }
    }

    // 마커에서 Spot 객체 가져오기
    private fun getSpotFromMarker(marker: Marker): SpotEntity? {
        return marker.tag as? SpotEntity
    }

    // 레벨 텍스트 업데이트
    private fun getLevelName(level: Int): String = when (level) {
        1 -> "동네 초심자"
        2 -> "길찾기 연습생"
        3 -> "견습 도깨비 사냥꾼"
        4 -> "스팟 탐색가"
        5 -> "동네 정보통"
        6 -> "골목 달인"
        7 -> "핫플 개척자"
        8 -> "로컬 히어로"
        9 -> "전설의 도깨비"
        10 -> "동네 마스터"
        else -> "미정 레벨"
    }

    private fun checkLocationPermissionForCustomButton() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                moveToMyLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(this, "내 위치를 표시하려면 위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun moveToMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f))
                } else {
                    Toast.makeText(this, "현재 위치를 가져올 수 없습니다. 에뮬레이터 위치를 설정해주세요.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "위치 정보를 가져오는 데 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("MyPageActivity", "위치 정보 가져오기 실패", e)
            }
        }
    }



    // 커스텀 정보창 어댑터
    class CustomInfoWindowAdapter(private val context: MyPageActivity, private val spot: SpotEntity) : GoogleMap.InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): android.view.View? {
            return null
        }

        override fun getInfoContents(marker: Marker): android.view.View {
            val view = context.layoutInflater.inflate(R.layout.custom_info_window, null)
            val tvTitle: TextView = view.findViewById(R.id.tvTitle)
            val tvSnippet: TextView = view.findViewById(R.id.tvSnippet)

            tvTitle.text = marker.title
            // 방문한 스팟인 경우 "방문완료" 추가
            if (spot.isVisited) {
                tvSnippet.text = "${marker.snippet}\n방문완료"
            } else {
                tvSnippet.text = marker.snippet
            }
            return view
        }
    }

    private fun updateSpotsByType(spotList: List<SpotEntity>) {
        registeredSpots = spotList.filter { it.isSaved }
        likedSpots = spotList.filter { it.isLiked }
        dislikedSpots = spotList.filter { it.isDisliked }
        visitedSpots = spotList.filter { it.isVisited }
    }
}
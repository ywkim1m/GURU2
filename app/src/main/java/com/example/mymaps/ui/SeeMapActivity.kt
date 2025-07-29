package com.example.mymaps.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mymaps.R
import com.example.mymaps.data.UserPrefsManager
import com.example.mymaps.model.SpotEntity
import com.example.mymaps.viewmodel.SpotViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.mymaps.MyApp
import com.example.mymaps.viewmodel.SpotViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SeeMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var isGpsConfirmed = false // 상태 플래그
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap

    private val spotViewModel: SpotViewModel by viewModels {
        SpotViewModelFactory((application as MyApp).spotRepository)
    }

    // 스팟 위치(기본)
    private var targetLat = 0.0
    private var targetLng = 0.0

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 스팟 객체를 intent로 받음
        val spot = intent.getParcelableExtra("spot", SpotEntity::class.java)
        if (spot == null) {
            Toast.makeText(this, "스팟 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 스팟 좌표 접근
        targetLat = spot.latitude
        targetLng = spot.longitude

        // 상단 뒤로가기 버튼
        val backBtn = findViewById<ImageButton>(R.id.btnBack)
        backBtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // 수정 버튼
        val btnEdit = findViewById<TextView>(R.id.btnEdit)

        // 수정 버튼 클릭
        btnEdit.setOnClickListener {
            // 스팟 수정 화면 이동
            val intent = Intent(this, SpotEditActivity::class.java)
            intent.putExtra("spot", spot)
            startActivity(intent)
        }

        // 바텀시트 버튼
        val closeBtn = findViewById<Button>(R.id.btnClose)
        val certifyBtn = findViewById<Button>(R.id.btnCertify)

        // 인증된 상태면 GPS 버튼 숨김
        if (spot.isVisited) {
            isGpsConfirmed = true
            certifyBtn.visibility = View.GONE

            val params = closeBtn.layoutParams as LinearLayout.LayoutParams
            params.weight = 2f // 남은 영역 모두 차지
            closeBtn.layoutParams = params
        }

        // 닫기 버튼 클릭
        closeBtn.setOnClickListener {
            // 그냥 홈으로 이동 (또는 원하는 로직)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // GPS로 방문 인증 버튼 클릭
        certifyBtn.setOnClickListener {
            if (!isGpsConfirmed) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    // ⭐ 여기서부터 즉시 위치 요청!
                    val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
                        interval = 1000 // 1초
                        numUpdates = 1  // 한 번만 가져오기
                    }

                    val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                            val location = locationResult.lastLocation
                            if (location != null) {
                                // 위치 좌표 토스트로 확인!
                                Toast.makeText(this@SeeMapActivity, "현재 위치: ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()

                                val distance = FloatArray(1)
                                Location.distanceBetween(
                                    location.latitude, location.longitude,
                                    targetLat, targetLng,
                                    distance
                                )
                                val meters = distance[0]
                                if (meters < 30) { // 30m 이내
                                    val dateFormat = SimpleDateFormat("yy/MM/dd", Locale.getDefault())
                                    spot.visitedAt = dateFormat.format(Date())

                                    Toast.makeText(this@SeeMapActivity, "방문 인증 성공!", Toast.LENGTH_SHORT).show()
                                    isGpsConfirmed = true

                                    spot.isVisited = true
                                    spotViewModel.updateSpot(spot)

                                    val userPrefs = UserPrefsManager(this@SeeMapActivity)
                                    if (userPrefs.userLevel < 10) {
                                        userPrefs.userLevel += 1
                                    }

                                    certifyBtn.visibility = View.GONE
                                    val params = closeBtn.layoutParams as LinearLayout.LayoutParams
                                    params.weight = 2f
                                    closeBtn.layoutParams = params

                                    val intent = Intent(this@SeeMapActivity, VisitedActivity::class.java)
                                    intent.putExtra("spot", spot)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@SeeMapActivity, "더 가까이 접근해주세요! (현재 $meters m)", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@SeeMapActivity, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                            // 위치 콜백 해제(메모리 누수 방지)
                            fusedLocationClient.removeLocationUpdates(this)
                        }
                    }

                    // 위치 업데이트 요청 시작!
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

                } else {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
                }
            } else {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }

    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Log.d("SeeMapActivity", "targetLat: $targetLat, targetLng: $targetLng")
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(targetLat, targetLng), 16f))

        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(targetLat, targetLng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
    }
}

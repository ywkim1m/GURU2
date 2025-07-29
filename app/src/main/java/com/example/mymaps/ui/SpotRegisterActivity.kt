package com.example.mymaps.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mymaps.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SpotRegisterActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var tvPlaceName: TextView
    private lateinit var tvPlaceAddress: TextView
    private lateinit var btnAddSpot: Button
    private lateinit var btnClose: ImageButton
    private lateinit var btnMyLocationCustom: ImageButton

    // 공릉동 도깨비시장 좌표
    private val GONGNEUNG_DOKKAEBI_MARKET = LatLng(37.6256, 127.0782)

    // 위치 권한 요청 런처
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
        setContentView(R.layout.activity_spot_register)

        tvPlaceName = findViewById(R.id.tvPlaceName)
        tvPlaceAddress = findViewById(R.id.tvPlaceAddress)
        btnAddSpot = findViewById(R.id.btnAddSpot)
        btnClose = findViewById(R.id.btnClose)
        btnMyLocationCustom = findViewById(R.id.btnMyLocationCustom)

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Map 연결
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnClose.setOnClickListener { finish() }

        // 커스텀 '내 위치' 버튼 클릭 리스너 설정
        btnMyLocationCustom.setOnClickListener {
            checkLocationPermissionForCustomButton()
        }

        btnAddSpot.setOnClickListener {
            val center = googleMap.cameraPosition.target
            Log.d("SpotRegister", "스팟 추가 버튼 클릭: 위도=${center.latitude}, 경도=${center.longitude}")

            val intent = Intent(this, SpotDetailActivity::class.java).apply {
                putExtra("latitude", center.latitude)
                putExtra("longitude", center.longitude)
                putExtra("placeName", tvPlaceName.text.toString())
                putExtra("placeAddress", tvPlaceAddress.text.toString())
            }
            startActivity(intent)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // 공릉동 도깨비시장으로 지도 초기 이동
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GONGNEUNG_DOKKAEBI_MARKET, 18f))
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.setPadding(0, 0, 0, 0)
        googleMap.setOnCameraIdleListener {
            val center = googleMap.cameraPosition.target
            Log.d("SpotRegister", "카메라 이동 완료. 현재 중심 위도=${center.latitude}, 경도=${center.longitude}")
            updatePlaceInfo(center.latitude, center.longitude)
        }
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
                Log.e("SpotRegister", "위치 정보 가져오기 실패", e)
            }
        }
    }

    private fun updatePlaceInfo(lat: Double, lng: Double) {
        tvPlaceAddress.text = "주소 검색 중..."
        tvPlaceName.text = "장소명 검색 중..."
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val geoCoder = Geocoder(this@SpotRegisterActivity, Locale.KOREA)
                val addresses = geoCoder.getFromLocation(lat, lng, 1)

                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val fullAddress = address.getAddressLine(0) ?: "주소 없음"
                    runOnUiThread { tvPlaceAddress.text = fullAddress }
                    Log.d("SpotRegister", "Geocoder 검색된 주소: $fullAddress")

                    val thoroughfare = address.thoroughfare
                    val premises = address.premises
                    val featureName = address.featureName
                    val subLocality = address.subLocality

                    Log.d("SpotRegister", "Geocoder 상세: thoroughfare='$thoroughfare', premises='$premises', featureName='$featureName', subLocality='$subLocality'")

                    val geocoderPlaceName = when {
                        !premises.isNullOrEmpty() -> premises
                        !thoroughfare.isNullOrEmpty() -> thoroughfare
                        !subLocality.isNullOrEmpty() -> subLocality
                        !featureName.isNullOrEmpty() && featureName != fullAddress.split(" ").lastOrNull() -> featureName
                        else -> "장소명 없음 (Geocoder)"
                    }
                    runOnUiThread { tvPlaceName.text = geocoderPlaceName }
                    Log.d("SpotRegister", "장소명 최종 설정 (Geocoder): $geocoderPlaceName")
                } else {
                    runOnUiThread {
                        tvPlaceAddress.text = "주소 없음"
                        tvPlaceName.text = "장소명 없음 (Geocoder)"
                    }
                    Log.d("SpotRegister", "Geocoder 결과 없음: 주소, 장소명 없음")
                }
            } catch (e: Exception) {
                runOnUiThread {
                    tvPlaceAddress.text = "주소 오류"
                    tvPlaceName.text = "장소명 오류 (Geocoder)"
                }
                Log.e("SpotRegister", "Geocoder 오류 발생: ${e.message}", e)
            }
        }
    }
}
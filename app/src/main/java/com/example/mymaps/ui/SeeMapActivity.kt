package com.example.mymaps.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.mymaps.R

class SeeMapActivity : AppCompatActivity() {
    private var isGpsConfirmed = false // 상태 플래그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_map)

        // 상단 뒤로가기 버튼
        val backBtn = findViewById<ImageButton>(R.id.btnBack)
        backBtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // 바텀시트 버튼
        val closeBtn = findViewById<Button>(R.id.btnClose)
        val certifyBtn = findViewById<Button>(R.id.btnCertify)

        // 닫기 버튼 클릭
        closeBtn.setOnClickListener {
            // 그냥 홈으로 이동 (또는 원하는 로직)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // GPS로 방문 인증 버튼 클릭
        certifyBtn.setOnClickListener {
            if (!isGpsConfirmed) {
                // 첫 번째 클릭: 인증 처리 UI 등
                isGpsConfirmed = true
                certifyBtn.text = "홈으로"
                // (필요하다면 이곳에서 지도 인증, 토스트, 상태 변경 등 추가)
            } else {
                // 두 번째 클릭: 홈으로 이동
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }
}

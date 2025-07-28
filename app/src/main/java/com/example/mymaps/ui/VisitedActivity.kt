package com.example.mymaps.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.mymaps.R

class VisitedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visited)

        // 뒤로가기 버튼
        val backButton = findViewById<ImageButton>(R.id.btnBack)
        backButton.setOnClickListener {
            // SeeMapActivity로 이동
            val intent = Intent(this, SeeMapActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 홈으로 버튼
        val goHomeButton = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnGoHome)
        goHomeButton.setOnClickListener {
            // MainActivity(홈)로 이동
            val intent = Intent(this, HomeActivity::class.java)
            // 혹시 스택 초기화하고 싶으면 아래 주석 해제
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}

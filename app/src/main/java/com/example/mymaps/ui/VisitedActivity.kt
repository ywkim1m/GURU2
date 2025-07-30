package com.example.mymaps.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mymaps.R
import com.example.mymaps.data.UserPrefsManager
import com.example.mymaps.model.SpotEntity

class VisitedActivity : AppCompatActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visited)

        // SpotEntity 받아오기 (Parcelable)
        val spot = intent.getParcelableExtra("spot", SpotEntity::class.java)
        if (spot == null) {
            Toast.makeText(this, "스팟 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 축하 메시지, 뱃지 세팅
        val tvTitle = findViewById<TextView>(R.id.tvCompleteTitle)
        val ivBadge = findViewById<ImageView>(R.id.ivBadge)

        // 유저 레벨
        val userLevel = UserPrefsManager(this).userLevel // context 필요

        spot.let {
            tvTitle.text = "10 경험치 적립 완료!\n또 만나요 :)"
            // 레벨별 뱃지
            ivBadge.setImageResource(getBadgeDrawableByLevel(userLevel))
        }

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

            startActivity(intent)
            finish()
        }
    }
    private fun getBadgeDrawableByLevel(level: Int): Int {
        // 리소스 파일이 없으면 기본값
        val resId = resources.getIdentifier(
            "badge_$level",
            "drawable",
            packageName
        )
        return if (resId != 0) resId else R.drawable.ic_image_placeholder
    }
}

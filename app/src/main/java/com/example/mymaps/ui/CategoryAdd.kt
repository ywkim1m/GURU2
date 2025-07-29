package com.example.mymaps.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mymaps.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CategoryAdd : AppCompatActivity() {

    private lateinit var etCategoryName: EditText
    private lateinit var chipGroupColors: ChipGroup
    private lateinit var btnSubmitCategory: Button

    private var selectedPinColorName: String? = null
    private var selectedPinColorResId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_add)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Category)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // UI 요소 초기화
        etCategoryName = findViewById(R.id.etCategoryName)
        chipGroupColors = findViewById(R.id.chipGroupColors)
        btnSubmitCategory = findViewById(R.id.btnSubmitCategory)
        val btnBack: ImageButton = findViewById(R.id.btnBack)

        // ChipGroup에 리스너 설정
        setupChipGroupListener()

        // 뒤로가기 버튼
        btnBack.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        // '추가하기' 버튼
        btnSubmitCategory.setOnClickListener {
            val categoryName = etCategoryName.text.toString().trim()

            if (categoryName.isEmpty()) {
                etCategoryName.error = "카테고리 이름을 입력해주세요."
                return@setOnClickListener
            }

            // 결과를 담을 Intent 생성
            val resultIntent = Intent().apply {
                putExtra("categoryName", categoryName)
                putExtra("categoryPinColorName", selectedPinColorName)
                putExtra("categoryPinColorResId", selectedPinColorResId)
            }

            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "카테고리를 추가했습니다", Toast.LENGTH_SHORT).show()

            finish()
        }
    }

    private fun setupChipGroupListener() {
        chipGroupColors.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val checkedChipId = checkedIds[0]
                val checkedChip: Chip = group.findViewById(checkedChipId)

                selectedPinColorName = checkedChip.text.toString()
                when (checkedChip.text.toString()) {
                    "빨강" -> selectedPinColorResId = R.drawable.dot_red
                    "주황" -> selectedPinColorResId = R.drawable.dot_orange
                    "노랑" -> selectedPinColorResId = R.drawable.dot_yellow
                    "초록" -> selectedPinColorResId = R.drawable.dot_green
                    "파랑" -> selectedPinColorResId = R.drawable.dot_blue
                    "회색" -> selectedPinColorResId = R.drawable.dot_gray
                    else -> selectedPinColorResId = 0
                }
            } else {
                selectedPinColorName = null
                selectedPinColorResId = 0
            }
        }
    }
}
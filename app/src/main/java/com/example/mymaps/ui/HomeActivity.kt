package com.example.mymaps.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.ui.MyPageActivity
import com.example.mymaps.R
import com.example.mymaps.ui.SpotRegisterActivity
import com.example.mymaps.adapter.SpotAdapter
import com.example.mymaps.model.SpotCategory
import com.example.mymaps.model.SpotEntity
import com.example.mymaps.viewmodel.SpotViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.example.mymaps.MyApp
import com.example.mymaps.viewmodel.SpotViewModelFactory
import com.google.android.material.shape.ShapeAppearanceModel


class HomeActivity : AppCompatActivity() {
    private val spotViewModel: SpotViewModel by viewModels {
        SpotViewModelFactory((application as MyApp).spotRepository)
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var spotAdapter: SpotAdapter

    private var currentPosition = 0

    private lateinit var goodImage: ImageView
    private lateinit var badImage: ImageView

    private var isGoodSelected = false
    private var isBadSelected = false

    private lateinit var chipGroupCategory: ChipGroup
    private var selectedCategory: String? = null
    private var allSpotList: List<SpotEntity> = emptyList()

    private lateinit var descText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        chipGroupCategory = findViewById(R.id.chipGroupCategory)

        recyclerView = findViewById(R.id.rvImageCarousel)
        goodImage = findViewById(R.id.good_img)
        badImage = findViewById(R.id.bad_img)

        descText = findViewById(R.id.desc_text)

        // 가로 스와이프, SnapHelper로 한 번에 한 카드
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val snapHelper = androidx.recyclerview.widget.PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        spotAdapter = SpotAdapter(
            onDeleteClick = { },
            onShowMapClick = { spot -> // 지도보기 화면으로 이동
                val intent = Intent(this, SeeMapActivity::class.java)
                intent.putExtra("spot", spot)
                startActivity(intent) }
        )
        recyclerView.adapter = spotAdapter

        // ViewModel에서 데이터 받아서 어댑터에 넣음
        spotViewModel.allSpots.observe(this) { spots ->
            allSpotList = spots.filter { it.isSaved }
            setCategoryChips(makeCategoryList(allSpotList))
            applyCategoryFilter()
        }

        // 스크롤(카드 넘김) 시 현재 position 갱신
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val snapView = snapHelper.findSnapView(rv.layoutManager)
                    currentPosition = rv.layoutManager?.getPosition(snapView ?: return) ?: 0
                    updateLikeDislikeButtons()
                    updateDescText()
                }
            }
        })

        // 하단 좋아요/싫어요 버튼 동작
        goodImage.setOnClickListener {
            val spot = spotAdapter.currentList.getOrNull(currentPosition) ?: return@setOnClickListener
            spotViewModel.updateSpot(spot.copy(isLiked = !spot.isLiked, isDisliked = false))
        }
        badImage.setOnClickListener {
            val spot = spotAdapter.currentList.getOrNull(currentPosition) ?: return@setOnClickListener
            spotViewModel.updateSpot(spot.copy(isDisliked = !spot.isDisliked, isLiked = false))
        }

        // 하단 네비게이션 바 버튼
        val barOne = findViewById<ImageView>(R.id.bar_one)
        val barTwo = findViewById<ImageView>(R.id.bar_two)
        val barThree = findViewById<ImageView>(R.id.bar_three)

        barOne.setOnClickListener {
            // barOne은 현재 화면 (HomeActivity) → 아무 동작 X
        }

        // 스팟 등록 화면으로 전환
        barTwo.setOnClickListener {
            val intent = Intent(this, SpotRegisterActivity::class.java)
            startActivity(intent)
        }

        // 마이페이지로 전환
        barThree.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }

    // 하단 버튼 상태(색상) 갱신
    private fun updateLikeDislikeButtons() {
        val spot = spotAdapter.currentList.getOrNull(currentPosition) ?: return
        goodImage.setImageResource(if (spot.isLiked) R.drawable.good_color else R.drawable.good)
        badImage.setImageResource(if (spot.isDisliked) R.drawable.bad_color else R.drawable.bad)
    }

    // 장소 설명 스크롤
    private fun updateDescText() {
        val spot = spotAdapter.currentList.getOrNull(currentPosition)
        descText.text = spot?.description ?: ""
    }

    // 카테고리 목록 생성
    private fun makeCategoryList(spots: List<SpotEntity>): List<String> {
        val base = mutableSetOf<String>()
        base.addAll(SpotCategory.entries.map { it.displayName })
        base.addAll(
            spots.mapNotNull { it.categoryName }
                .filter { it.isNotBlank() && it != "기타" && SpotCategory.entries.toTypedArray()
                    .none { cat -> cat.displayName == it } }
        )
        return base.toList()
    }

    // ChipGroup에 Chip 동적 추가, 클릭시 필터 동작
    private fun setCategoryChips(categoryList: List<String>) {
        chipGroupCategory.removeAllViews()
        for (cat in categoryList) {
            val chip = Chip(this)

            // dot Drawable 불러와서 크기 설정
            val dot = ContextCompat.getDrawable(this, getDotDrawableRes(cat))!!
            val dotSize = 40
            dot.setBounds(0, 0, dotSize, dotSize)
            val span = ImageSpan(dot, ImageSpan.ALIGN_BOTTOM)

            // SpannableString을 chip 텍스트로
            val spannable = SpannableString("  $cat") // 앞에 한 칸 or 두 칸 띄우기
            spannable.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            chip.text = spannable

            chip.isCheckable = true
            chip.isChecked = cat == selectedCategory
            chip.isCheckedIconVisible = false
            chip.setTextColor(ContextCompat.getColorStateList(this, R.color.chip_text_selector))
            chip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.chip_selector)
            chip.setEnsureMinTouchTargetSize(false)
            chip.minHeight = 35.dpToPx().toInt()
            chip.setPadding(8.dpToPx().toInt(), 0, 8.dpToPx().toInt(), 0)

            // Click 리스너
            chip.setOnClickListener {
                if (selectedCategory == cat) {
                    // 이미 선택한 칩 다시 누르면 전체 보기
                    selectedCategory = null
                } else {
                    selectedCategory = cat
                }
                applyCategoryFilter()
                setCategoryChips(categoryList) // 상태 갱신
            }
            chipGroupCategory.addView(chip)
        }
    }

    // 카테고리별 dot 이미지 반환 함수
    private fun getDotDrawableRes(category: String): Int = when (category) {
        "산책루트" -> R.drawable.dot_red
        "숨은맛집" -> R.drawable.dot_orange
        "포토존" -> R.drawable.dot_yellow
        "쇼핑" -> R.drawable.dot_green
        "공원" -> R.drawable.dot_blue
        else -> R.drawable.dot_gray // 기타 등등
    }

    // dp to px 확장 함수
    fun Int.dpToPx(): Float = this * resources.displayMetrics.density

    // 필터링 함수
    private fun applyCategoryFilter() {
        val filtered = when (selectedCategory) {
            null -> allSpotList
            "기타" -> allSpotList.filter {
                (it.categoryEnum == SpotCategory.ETC) &&
                        (it.categoryName.isNullOrBlank() || it.categoryName == "기타")
            }
            else -> {
                // enumValue가 null이 아니면 Enum 비교 (즉, 앱 기본 카테고리)
                val enumValue = SpotCategory.fromDisplayName(selectedCategory!!)
                if (enumValue != null) {
                    allSpotList.filter {
                        it.categoryEnum == enumValue || it.categoryName == selectedCategory
                    }
                } else {
                    // 직접 입력한 사용자 카테고리
                    allSpotList.filter {
                        it.categoryName == selectedCategory
                    }
                }
            }
        }
        spotAdapter.submitList(filtered)
        currentPosition = 0

        updateLikeDislikeButtons()
    }
}
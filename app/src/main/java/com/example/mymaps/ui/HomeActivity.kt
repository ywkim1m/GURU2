package com.example.mymaps.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.MyPageActivity
import com.example.mymaps.R
import com.example.mymaps.SpotRegisterActivity
import com.example.mymaps.adapter.SpotAdapter
import com.example.mymaps.viewmodel.SpotViewModel


class HomeActivity : AppCompatActivity() {
    private val spotViewModel: SpotViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var spotAdapter: SpotAdapter

    private var currentPosition = 0

    private lateinit var goodImage: ImageView
    private lateinit var badImage: ImageView

    private var isGoodSelected = false
    private var isBadSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.rvImageCarousel)
        goodImage = findViewById(R.id.good_img)
        badImage = findViewById(R.id.bad_img)

        // 가로 스와이프 + SnapHelper로 한 번에 한 카드
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        val snapHelper = androidx.recyclerview.widget.PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        spotAdapter = SpotAdapter(
            onDeleteClick = { } // 카드 내부 버튼 X
        )
        recyclerView.adapter = spotAdapter

        // ViewModel에서 데이터 받아서 어댑터에 넣음
        spotViewModel.allSpots.observe(this) { spots ->
            // isSaved == true만 리스트로 (등록한 스팟)
            spotAdapter.submitList(spots.filter { it.isSaved })
            // 데이터 변경시 현재 카드에 맞게 버튼도 갱신
            updateLikeDislikeButtons()
        }
        spotViewModel.loadSpots()

        // 스크롤(카드 넘김) 시 현재 position 갱신
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val snapView = snapHelper.findSnapView(rv.layoutManager)
                    currentPosition = rv.layoutManager?.getPosition(snapView ?: return) ?: 0
                    updateLikeDislikeButtons()
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
}
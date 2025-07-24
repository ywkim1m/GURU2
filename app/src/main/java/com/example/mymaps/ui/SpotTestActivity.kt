package com.example.mymaps.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.mymaps.R
import com.example.mymaps.adapter.SpotAdapter
import com.example.mymaps.data.AppDatabase
import com.example.mymaps.model.SpotCategory
import com.example.mymaps.model.SpotEntity
import com.example.mymaps.repository.SpotRepository
import com.example.mymaps.viewmodel.SpotViewModel
import com.example.mymaps.viewmodel.SpotViewModelFactory

class SpotTestActivity : AppCompatActivity() {

    private lateinit var viewModel: SpotViewModel
    private lateinit var adapter: SpotAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_test)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "my-db"
        ).build()
        val repository = SpotRepository(db.spotDao())
        val factory = SpotViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[SpotViewModel::class.java]

        adapter = SpotAdapter(
            onDeleteClick = { spot ->
                viewModel.deleteSpot(spot)
            }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.rv_spots)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allSpots.observe(this) { spots ->
            adapter.submitList(spots)
        }

        findViewById<Button>(R.id.btn_add_spot).setOnClickListener {
            // 임시 데이터 추가 예시
            val randomId = (0..99999).random()
            val newSpot = SpotEntity(
                id = randomId,
                name = "스팟 $randomId",
                description = "예시",
                photoUri = null,
                category = SpotCategory.BENCH,
                roadAddress = "도로명주소",
                latitude = 0.0,
                longitude = 0.0
            )
            viewModel.insertSpot(newSpot)
        }

        findViewById<Button>(R.id.btn_load_spots).setOnClickListener {
            viewModel.loadSpots()
        }

        // 최초 로드
        viewModel.loadSpots()
    }
}

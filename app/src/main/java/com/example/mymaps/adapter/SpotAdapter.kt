package com.example.mymaps.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymaps.R
import com.example.mymaps.model.SpotEntity

class SpotAdapter(private val onDeleteClick: (SpotEntity) -> Unit,
                  private val onShowMapClick: (SpotEntity) -> Unit)
    : ListAdapter<SpotEntity, SpotAdapter.SpotViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SpotEntity>() {
            override fun areItemsTheSame(oldItem: SpotEntity, newItem: SpotEntity) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: SpotEntity, newItem: SpotEntity) =
                oldItem == newItem
        }
    }

    inner class SpotViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(spot: SpotEntity) {
            Log.d("SpotAdapter", "bind: name=${spot.name}, photoUri=${spot.photoUri}")

            // 장소명
            view.findViewById<TextView>(R.id.tvSpotName).text = spot.name

            // 썸네일
            val ivSpotImage = view.findViewById<ImageView>(R.id.ivSpotImage)
            if (spot.photoUri.isNullOrEmpty()) {
                ivSpotImage.setImageResource(R.drawable.ic_image_placeholder)
            } else {
                Glide.with(ivSpotImage.context)
                    .load(Uri.parse(spot.photoUri))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(ivSpotImage)
            }

            // 방문완료 UI (방문했을 때만 VISIBLE)
            val visitStatusLayout = view.findViewById<LinearLayout>(R.id.visitStatusLayout)
            val tvVisitStatus = view.findViewById<TextView>(R.id.tvVisitStatus)
            val tvVisitDate = view.findViewById<TextView>(R.id.tvVisitDate)

            if (spot.isVisited) {
                visitStatusLayout.visibility = View.VISIBLE
                tvVisitStatus.text = "방문 완료!"
                // 날짜가 있다면 표시, 없으면 방문일자 없음 또는 공백 처리
                tvVisitDate.text = spot.visitedAt ?: ""
            } else {
                visitStatusLayout.visibility = View.GONE
            }

            // 지도보기 버튼
            val btnShowMap = view.findViewById<Button>(R.id.btnShowMap)
            btnShowMap.setOnClickListener {
                onShowMapClick(spot)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_card, parent, false) // 커스텀 카드 레이아웃
        return SpotViewHolder(v)
    }

    override fun onBindViewHolder(holder: SpotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

package com.example.mymaps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymaps.R
import com.example.mymaps.model.SpotEntity

class SpotAdapter(private val onDeleteClick: (SpotEntity) -> Unit)
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
            view.findViewById<TextView>(R.id.tvSpotName).text = spot.name

            val ivSpotImage = view.findViewById<ImageView>(R.id.ivSpotImage)
            if (spot.photoUri.isNullOrEmpty()) {
                ivSpotImage.setImageResource(R.drawable.ic_image_placeholder)
            } else {
                Glide.with(ivSpotImage)
                    .load(spot.photoUri)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(ivSpotImage)
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

package com.example.mymaps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.model.SpotEntity

class SpotAdapter(
    private val onDeleteClick: (SpotEntity) -> Unit
) : ListAdapter<SpotEntity, SpotAdapter.SpotViewHolder>(diffUtil) {

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
            view.findViewById<TextView>(android.R.id.text1).text =
                spot.name
            view.setOnLongClickListener {
                onDeleteClick(spot)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return SpotViewHolder(v)
    }

    override fun onBindViewHolder(holder: SpotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

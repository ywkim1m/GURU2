package com.example.mymaps.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.R
import com.example.mymaps.model.Badge

class BadgeAdapter(private var badgeList: List<Badge>) :
    RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivBadge: ImageView = view.findViewById(R.id.ivBadge)
//        val tvBadgeName: TextView = view.findViewById(R.id.tvBadgeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badgeList[position]
//        holder.tvBadgeName.text = badge.name

        // 리소스 이름: badge.id (예: badge_1)
        val resId = holder.ivBadge.context.resources.getIdentifier(
            badge.id, "drawable", holder.ivBadge.context.packageName
        )
        if (resId != 0) {
            holder.ivBadge.setImageResource(resId)
        } else {
            // 이미지가 없을 경우, 이미지뷰를 숨길 수 있음
            holder.ivBadge.setImageDrawable(null)
        }
    }

    override fun getItemCount() = badgeList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Badge>) {
        badgeList = newList
        notifyDataSetChanged()
    }
}

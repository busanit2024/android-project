package com.busanit.teamproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoriteAdapter(private val favoriteList: List<FavoriteItem>) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val item = favoriteList[position]
        holder.buildingName.text = item.buildingName
        holder.address.text = item.address
        holder.icon.setImageResource(item.iconResId)
    }

    override fun getItemCount(): Int = favoriteList.size

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val buildingName: TextView = itemView.findViewById(R.id.building_name)
        val address: TextView = itemView.findViewById(R.id.address)
    }
}


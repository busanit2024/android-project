package com.busanit.teamproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoriteAdapter(
    private val favoriteList: List<FavoriteItem>
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ic_star: ImageView = view.findViewById(R.id.ic_star)
        val buildingName: TextView = view.findViewById(R.id.building_name)
        val address: TextView = view.findViewById(R.id.address)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteAdapter.FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteItem = favoriteList[position]
        holder.ic_star.setImageResource(favoriteItem.iconResId)
        holder.buildingName.text = favoriteItem.buildingName
        holder.address.text = favoriteItem.address
    }

    override fun getItemCount(): Int = favoriteList.size

}
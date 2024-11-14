package com.busanit.searchrestroom.myPage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.database.Restroom

class FavoriteAdapter(
    private val restroomList: MutableList<Restroom>,
    private val onItemClick: (Restroom) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    fun updateList(newList: List<Restroom>) {
        restroomList.clear()
        restroomList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icStar: ImageView = view.findViewById(R.id.ic_star)
        val buildingName: TextView = view.findViewById(R.id.building_name)
        val address: TextView = view.findViewById(R.id.address)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(restroomList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val restroom = restroomList[position]
        holder.buildingName.text = restroom.restroomName
        holder.address.text = restroom.location
    }

    override fun getItemCount(): Int = restroomList.size
}
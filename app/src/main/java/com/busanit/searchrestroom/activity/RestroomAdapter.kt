package com.busanit.searchrestroom.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.SearchlistRecyclerviewBinding


class MyViewHolder(val binding: SearchlistRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

class RestroomAdapter(val datas: MutableList<Restroom>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
  override fun getItemCount(): Int {
    return datas?.size ?: 0
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
  = MyViewHolder(SearchlistRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val binding = (holder as MyViewHolder).binding
    binding.restroomName.text = datas!![position].restroomName
    val openTimeText = datas.getOrNull(position)?.let { restroom -> if (restroom.fullTime!!) "상시개방" else restroom.openTime ?: "" } ?: ""
    binding.openTime.text = openTimeText
    binding.location.text = datas[position].location
    binding.chipUnisex.visibility = if (datas[position].unisex == true) View.VISIBLE else View.GONE
    binding.chipDiaper.visibility = if (datas[position].diaper == true) View.VISIBLE else View.GONE
    binding.chipAccessible.visibility = if (datas[position].accessible == true) View.VISIBLE else View.GONE

  }
}
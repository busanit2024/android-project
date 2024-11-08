package com.busanit.searchrestroom.activity

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.SearchlistRecyclerviewBinding


class MyViewHolder(val binding: SearchlistRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root)

class RestroomAdapter(val datas: MutableList<Restroom>?, val currentLat: Double, val currentLong: Double): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  init {
    datas?.sortBy { restroom ->
      calculateDistance(currentLat, currentLong, restroom.latitude!!, restroom.longitude!!)
    }
  }

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

    val distance = calculateDistance(currentLat, currentLong, datas[position].latitude!!, datas[position].longitude!!)
    binding.distance.text = "${distance.toInt()}m"
  }

  private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val results = FloatArray(1)
    Location.distanceBetween(
      lat1,
      lon1,
      lat2,
      lon2,
      results
    )
    return results[0].toDouble()
  }
}
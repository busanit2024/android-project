package com.busanit.teamproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter(
    private val reviewList: List<MyReview>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buildingName: TextView = view.findViewById(R.id.building_name)
        val reViewDate: TextView = view.findViewById(R.id.review_date)
        val reviewContent: TextView = view.findViewById(R.id.review_content)
        val images: List<ImageView> = listOf(
            view.findViewById(R.id.review_image1),
            view.findViewById(R.id.review_image2),
            view.findViewById(R.id.review_image3)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_review_list, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val reviewItem = reviewList[position]
        holder.buildingName.text = reviewItem.buildingName
        holder.reViewDate.text = reviewItem.reg_time.toString()
        holder.reviewContent.text = reviewItem.reviewContent
    }

    override fun getItemCount(): Int = reviewList.size

}
package com.busanit.searchrestroom.myPage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.R

class ReviewAdapter(
    private val reviewList: List<MyReview>,
    private val onEditClick: (position: Int) -> Unit,
    private val onDeleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buildingName: TextView = view.findViewById(R.id.building_name)
        val reviewDate: TextView = view.findViewById(R.id.review_date)
        val reviewContent: TextView = view.findViewById(R.id.review_content)
        val editButton: TextView = view.findViewById(R.id.update)
        val deleteButton: TextView = view.findViewById(R.id.delete)
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
        holder.reviewDate.text = reviewItem.reg_time.toString()
        holder.reviewContent.text = reviewItem.reviewContent

        holder.editButton.setOnClickListener {
            Toast.makeText(holder.itemView.context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
            onEditClick(position)
        }

        holder.deleteButton.setOnClickListener {
            Toast.makeText(holder.itemView.context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = reviewList.size

}
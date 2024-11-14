package com.busanit.searchrestroom.myPage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.dao.ReviewDao
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.database.Review
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ReviewAdapter(
    private val reviewList: List<Review>,
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buildingName: TextView = view.findViewById(R.id.building_name)
        val reviewDate: TextView = view.findViewById(R.id.review_date)
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
        val db = AppDatabase.getDatabase(context = holder.itemView.context)
        val restroomDao = db!!.restroomDao()

        // 코루틴을 통해 데이터베이스에서 데이터를 가져옴
        CoroutineScope(Dispatchers.IO).launch {
            val restRoom = reviewItem.restroomId?.let { restroomDao.getRestroomById(it) }

            withContext(Dispatchers.Main) {
                holder.buildingName.text = restRoom?.restroomName
                holder.reviewDate.text = reviewItem.regTime
                holder.reviewContent.text = reviewItem.content
            }
        }
    }

    override fun getItemCount(): Int = reviewList.size

}
package com.busanit.searchrestroom.myPage

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.busanit.searchrestroom.AuthHelper
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.dao.ReviewDao
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.review.ReviewUpdateActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ReviewAdapter(
    private val reviewList: MutableList<Review>,
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buildingName: TextView = view.findViewById(R.id.building_name)
        val reviewDate: TextView = view.findViewById(R.id.review_date)
        val reviewContent: TextView = view.findViewById(R.id.review_content)
        val editButton: Button = view.findViewById(R.id.update)
        val deleteButton: Button = view.findViewById(R.id.delete)
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
        var restRoom: Restroom? = null

        // 코루틴을 통해 데이터베이스에서 데이터를 가져옴
        CoroutineScope(Dispatchers.IO).launch {
            restRoom = reviewItem.restroomId?.let { restroomDao.getRestroomById(it) }

            withContext(Dispatchers.Main) {
                holder.buildingName.text = restRoom?.restroomName
                holder.reviewDate.text = reviewItem.regTime.toString()
                holder.reviewContent.text = reviewItem.content
            }
        }

        holder.editButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, ReviewUpdateActivity::class.java)
            intent.putExtra("reviewId", reviewItem.reviewId)
            intent.putExtra("restroomId", restRoom?.restroomId)
            intent.putExtra("memberId", AuthHelper.getMemberId())
            intent.putExtra("content", reviewItem.content)
            intent.putExtra("toiletPaperOption", reviewItem.toiletPaperOption)
            intent.putExtra("howManyOption", reviewItem.howManyOption)
            intent.putExtra("cleanlinessOption", reviewItem.cleanlinessOption)
            intent.putExtra("restroomName", restRoom?.restroomName)
            intent.putExtra("location", restRoom?.location)
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context).run {
                setTitle("리뷰 삭제")
                setMessage("리뷰를 삭제하시겠습니까?")
                setPositiveButton("확인") { _, _ ->
                    val db = AppDatabase.getDatabase(holder.itemView.context)
                    db!!.reviewDao().delete(reviewItem)
                    reviewList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, reviewList.size)
                    Toast.makeText(holder.itemView.context, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                setNegativeButton("취소", null)
                show()
            }
        }
    }

    override fun getItemCount(): Int = reviewList.size

}